package olelite

import grails.transaction.Transactional
import com.k_int.goai.OaiClient
import java.text.SimpleDateFormat
import org.springframework.transaction.annotation.*
import groovy.json.*

@Transactional
class GOKbSyncService {

  def sessionFactory

  def onNewTipp = { ctx, tipp_record, auto_accept ->
    def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

     // title: [
     //   name:tip.title.name.text(),
     //   identifiers:[]
     //   ],
     //   titleId:tip.title.'@id'.text(),
     //   platform:tip.platform.name.text(),
     //   platformId:tip.platform.'@id'.text(),
     //   coverage:[],
     //   url:tip.url.text(),
     //   identifiers:[]



    // log.debug("onNewTipp (ctx=${ctx})");
    def new_tipp = new GokbTipp()
    new_tipp.objId = java.util.UUID.randomUUID().toString()
    new_tipp.isbn = getIdentifierValue(tipp_record.title.identifiers,'isbn');
    new_tipp.issn = getIdentifierValue(tipp_record.title.identifiers,'issn');
    new_tipp.eissn = getIdentifierValue(tipp_record.title.identifiers,'eissn');
    new_tipp.doi = getIdentifierValue(tipp_record.title.identifiers,'doi');
    new_tipp.gokbTitle = tipp_record.title.name
    new_tipp.pkg = ctx.pkg;
    new_tipp.accessUrl = tipp_record.title.url;

    if ( tipp_record.coverage?.size() > 0 ) {
      new_tipp.coverageStartDate = tipp_record.coverage[0].startDate ? sdf.parse(tipp_record.coverage[0].startDate) : null;
      new_tipp.coverageStartVolume = tipp_record.coverage[0].startVolume;
      new_tipp.coverageStartIssue = tipp_record.coverage[0].startIssue;
      new_tipp.coverageEndDate = tipp_record.coverage[0].endDate ? sdf.parse(tipp_record.coverage[0].endDate) : null;
      new_tipp.coverageEndVolume = tipp_record.coverage[0].endVolume;
      new_tipp.coverageEndIssue = tipp_record.coverage[0].endIssue;
      new_tipp.coverageDepth = tipp_record.coverage[0].coverageDepth;
      new_tipp.coverageNote = tipp_record.coverage[0].coverageNote;
    }

    new_tipp.createdDate = new Date();
    new_tipp.lastModifiedDate = new Date()
    new_tipp.save();
  }

  def onUpdatedTipp = { ctx, new_tipp_record, original_tipp_record, tipp_diff, auto_accept ->
    // log.debug("onUpdatedTipp (ctx=${ctx})");
  }

  def onDeletedTipp = { ctx, tipp_record, auto_accept ->
    // log.debug("onDeletedTipp (ctx=${ctx})");
  }

  def onPkgPropChange = { ctx, property, value, auto_accept ->
    // log.debug("onPkgPropChange (ctx=${ctx})");
  }

  def onTippUnchanged = { ctx, tipp_record ->
    // log.debug("onTippUnchanged (ctx=${ctx})");
  }

  def packageSync() {
    def oai_client = new OaiClient(host:'http://localhost:8081/gokb/oai/packages'); // ?verb=listRecords&metadataPrefix=gokb
    // def oai_client = new OaiClient(host:'https://test-gokb.kuali.org/gokb/oai/packages'); // ?verb=listRecords&metadataPrefix=gokb
    // def oai_client = new OaiClient(host:'https://gokb.k-int.com/gokb/oai/packages'); // ?verb=listRecords&metadataPrefix=gokb
    def max_timestamp = 0
    def date = new Date(0);

    log.debug("Collect package changes since ${date}");

    def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    oai_client.getChangesSince(date, 'gokb') { rec ->
      GokbPackage.withNewTransaction { status ->
        // log.debug("Process..");
        def package_identifier = rec.header.identifier.text();
        // log.debug("Got OAI Record ${package_identifier} datestamp: ${rec.header.datestamp}");
  
  
        def newpkg = packageConv(rec.metadata)
        def newpkg_json = new JsonBuilder( newpkg.parsed_rec ).toPrettyString()
  
        def old_package = null
  
        // Step 1 : See if we can locate an existing record for this package - If we can, then this is an update
        // If we can't then this is a new package...
        // log.debug("looking up package by identifier: ${package_identifier}");
  
        def package_record = GokbPackage.findByPackageIdentifier(package_identifier)
  
        if ( package_record == null ) {
          log.debug("Initialise a new package...");
          old_package = [tipps:[]]
          package_record = new GokbPackage(packageIdentifier:package_identifier);
          package_record.objId = java.util.UUID.randomUUID().toString()
          package_record.localStatus = 'Not imported'
          package_record.createdDate = sdf.parse(newpkg.parsed_rec.dateCreated);
          package_record.lastModifiedDate = new Date()
          package_record.packageName = newpkg.parsed_rec.packageName
          package_record.save(flush:true,failOnError:true);
        }
        else {
          // Load in old package record
          log.debug("Loading old package...");
          def rdr = BufferedReader(new InputStreamReader(package_record.content));
          old_package = JSON.parse(rdr)
        }
  
        def ctx = [ pkg: package_record ]
        def auto_accept_flag = false;

        // Call the various handlers
        com.k_int.GokbDiffEngine.diff(ctx, old_package, newpkg.parsed_rec, onNewTipp, onUpdatedTipp, onDeletedTipp, onPkgPropChange, onTippUnchanged, auto_accept_flag)
  
  
        package_record.setContent(newpkg_json.getBytes('UTF-8'));
        package_record.numTitles = newpkg.parsed_rec.tipps?.size();
        package_record.globalStatus = newpkg.parsed_rec.status
        package_record.primaryPlatform = newpkg.parsed_rec.nominalPlatform
        package_record.primaryPlatformProvider = newpkg.parsed_rec.nominalProvider
        package_record.lastModifiedDate = new Date()

        package_record.save(flush:true,failOnError:true);
      }
    }

  }

  /**
   *  Take in a gokb metadata prefix package record and turn it into a map suitable for processing by the
   *  gokb diff engine
   */
  def packageConv = { md ->
    println("Package conv...");
    // Convert XML to internal structure and return
    def result = [:]
    // result.parsed_rec = xml.text().getBytes();
    result.title = md.gokb.package.name.text()

    result.parsed_rec = [:]
    result.parsed_rec.packageName = md.gokb.package.name.text()
    result.parsed_rec.packageId = md.gokb.package.'@id'.text()
    result.parsed_rec.status = md.gokb.package.status?.text()
    result.parsed_rec.nominalPlatform = md.gokb.package.nominalPlatform?.text()
    result.parsed_rec.nominalProvider = md.gokb.package.nominalProvider?.text()
    result.parsed_rec.dateCreated = md.gokb.package.dateCreated?.text()
    result.parsed_rec.scope = md.gokb.package.scope?.text()
    result.parsed_rec.listStatus = md.gokb.package.listStatus?.text()
    result.parsed_rec.tipps = []
    int ctr=0
    md.gokb.package.TIPPs.TIPP.each { tip ->
      // log.debug("Processing tipp ${ctr++} from package ${result.parsed_rec.packageId} - ${result.title}");
      def newtip = [
                     title: [
                       name:tip.title.name.text(), 
                       identifiers:[]
                     ],
                     titleId:tip.title.'@id'.text(),
                     platform:tip.platform.name.text(),
                     platformId:tip.platform.'@id'.text(),
                     coverage:[],
                     url:tip.url.text(),
                     identifiers:[]
                   ];

      tip.coverage.each { cov ->
        newtip.coverage.add([
                       startDate:cov.'@startDate'.text(),
                       endDate:cov.'@endDate'.text(),
                       startVolume:cov.'@startVolume'.text(),
                       endVolume:cov.'@endVolume'.text(),
                       startIssue:cov.'@startIssue'.text(),
                       endIssue:cov.'@endIssue'.text(),
                       coverageDepth:cov.'@coverageDepth'.text(),
                       coverageNote:cov.'@coverageNote'.text(),
                     ]);
      }

      tip.title.identifiers.identifier.each { id ->
        newtip.title.identifiers.add([namespace:id.'@namespace'.text(), value:id.'@value'.text()]);
      }
      newtip.title.identifiers.add([namespace:'uri',value:newtip.titleId]);

      // log.debug("Harmonise identifiers");
      // harmoniseTitleIdentifiers(newtip);

      result.parsed_rec.tipps.add(newtip)
    }

    result.parsed_rec.tipps.sort{it.titleId}
    println("Rec conversion for package returns object with title ${result.parsed_rec.title} and ${result.parsed_rec.tipps?.size()} tipps");
    return result
  }

  static def getIdentifierValue(idlst, namespace) {
    def result = null;
    idlst.each {
      if ( it.namespace == namespace ) {
        result = it.value;
      }
    }
    return result;
  }
}
