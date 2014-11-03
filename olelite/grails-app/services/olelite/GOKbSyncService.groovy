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
    // log.debug("onNewTipp");
  }

  def onUpdatedTipp = { ctx, new_tipp_record, original_tipp_record, tipp_diff, auto_accept ->
    // log.debug("onUpdatedTipp");
  }

  def onDeletedTipp = { ctx, tipp_record, auto_accept ->
    // log.debug("onDeletedTipp");
  }

  def onPkgPropChange = { ctx, property, value, auto_accept ->
    // log.debug("onPkgPropChange");
  }

  def onTippUnchanged = { ctx, tipp_record ->
    // log.debug("onTippUnchanged");
  }

  def packageSync() {
    def oai_client = new OaiClient(host:'https://test-gokb.kuali.org/gokb/oai/packages'); // ?verb=listRecords&metadataPrefix=gokb
    // def oai_client = new OaiClient(host:'https://gokb.k-int.com/gokb/oai/packages'); // ?verb=listRecords&metadataPrefix=gokb
    def max_timestamp = 0
    def date = new Date(0);

    log.debug("Collect package changes since ${date}");

    oai_client.getChangesSince(date, 'gokb') { rec ->
      log.debug("Process..");
      def package_identifier = rec.header.identifier.text();
      log.debug("Got OAI Record ${package_identifier} datestamp: ${rec.header.datestamp}");


      def newpkg = packageConv(rec.metadata)
      def newpkg_json = new JsonBuilder( newpkg.parsed_rec ).toPrettyString()

      def old_package = null

      // Step 1 : See if we can locate an existing record for this package - If we can, then this is an update
      // If we can't then this is a new package...
      log.debug("looking up package by identifier: ${package_identifier}");

      def package_record = GokbPackage.findByPackageIdentifier(package_identifier)

      if ( package_record == null ) {
        log.debug("Initialise a new package...");
        old_package = [tipps:[]]
        package_record = new GokbPackage(packageIdentifier:package_identifier);
        package_record.objId = java.util.UUID.randomUUID().toString()
        package_record.packageName = newpkg.parsed_rec.packageName
      }
      else {
        // Load in old package record
        log.debug("Loading old package...");
        def rdr = BufferedReader(new InputStreamReader(package_record.content));
        old_package = JSON.parse(rdr)
      }

      def ctx = null;
      def auto_accept_flag = false;
      com.k_int.GokbDiffEngine.diff(ctx, old_package, newpkg.parsed_rec, onNewTipp, onUpdatedTipp, onDeletedTipp, onPkgPropChange, onTippUnchanged, auto_accept_flag)


      package_record.setContent(newpkg_json.getBytes('UTF-8'));
      package_record.save(flush:true,failOnError:true);
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

}
