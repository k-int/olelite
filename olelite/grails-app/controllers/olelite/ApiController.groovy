package olelite

import static java.util.UUID.randomUUID
import grails.converters.JSON
// import grails.plugins.springsecurity.Secured
import grails.util.GrailsNameUtils
import java.security.SecureRandom
import org.apache.commons.codec.binary.Base64

class ApiController {

  def grailsApplication
  def genericOIDService
  
  def search() {
    def result = [:]

    // User user = springSecurityService.currentUser

    log.debug("Entering SearchController:index");

    result.max = params.max ? Integer.parseInt(params.max) : 10
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

    def qbetemplate = grailsApplication.config.globalSearchTemplates[params.tmpl]


    // Looked up a template from somewhere, see if we can execute a search
    if ( qbetemplate ) {
      log.debug("Execute query");
      def qresult = [max:result.max, offset:result.offset]
      doQuery(qbetemplate, params, qresult)
      log.debug("Query complete");
      result.lasthit = result.offset + result.max > qresult.reccount ? qresult.reccount : ( result.offset + result.max )
  
      // Add the page information.
      result.page_current = (result.offset / result.max) + 1
      result.page_total = (qresult.reccount / result.max).toInteger() + (qresult.reccount % result.max > 0 ? 1 : 0)
      result.reccount = qresult.reccount

      result.rows = []
      qresult.recset.each { r ->
        def row = [:]
        def first = true
        int i=0;
        r.each { c ->
          if ( first ) {
            row['__class']=c.class.name
            row['__id']=c.id
            first = false
          }
          else {
            row[qbetemplate.qbeConfig.qbeResults[i++].property] = c
          }
        }
        result.rows.add(row);
      }
    } 
    else {
      log.error("no template ${result?.qbetemplate}");
    }

    render result as JSON
  }

  def retrieve() {
    log.debug("retrieve ${params}");
    def result = genericOIDService.resolveOID(params.oid)
    render result as JSON
  }

  def private doQuery (qbetemplate, params, result) {
    log.debug("doQuery ${result}");
    def target_class = grailsApplication.getArtefact("Domain",qbetemplate.baseclass);
    com.k_int.HQLBuilder.build(grailsApplication, qbetemplate, params, result, target_class, genericOIDService, 'rows')
    log.debug("process recset..");
  }

  def materialisePackage() {
    log.debug("materialisePackage ${params}");
    def result = [:]
    result.status="ERROR"
    if ( params.pkgid ) {
      def gokb_pkg_to_materialise = GokbPackage.get(params.pkgid)
      log.debug("pkg : ${gokb_pkg_to_materialise}");
      if ( gokb_pkg_to_materialise ) {
        def new_eres = new EResourceRecord();
        new_eres.id = "${gokb_pkg_to_materialise.id}";
        new_eres.title = gokb_pkg_to_materialise.packageName;
        new_eres.pkg = gokb_pkg_to_materialise.objId
        log.debug("Save: ${new_eres}");

        if ( new_eres.validate() ) {
          if ( new_eres.save(flush:true) ) {
            new_eres.save(flush:true)
            result.status = 'OK'
            result.__id = new_eres.id;
          }
          else {
            log.error("problem saving new eres");
            new_eres.errors.each { 
              log.error(it);
            }
          }
        }

        log.debug("new eres: ${result}");
      }
    }
    render result as JSON
  }

  def getTipps() {
    def result = [:]
    def sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
    result.rows = []

    def qr = GokbTipp.executeQuery('''
select t.id, t.isbn, t.issn, t.eissn, t.doi , t.coverageStartDate, t.coverageEndDate, t.accessUrl, t.gokbTitle, i.eresInst.id,
       t.coverageStartVolume, t.coverageStartIssue, t.coverageEndVolume, t.coverageEndIssue
from GokbTipp t left outer join t.instances as i with i.eresInst.id = :errid,
     EResourceRecord err 
where err.id = :errid 
  and err.pkg = t.pkg.objId
''', [errid:params.eresid], [max:3000]);

    qr.each {

      def start = ( it[5] ? sdf.format(it[5]) : '' ) + ( it[10] ? " / v:${it[10]}" : '' ) + ( it[11] ? " / i:${it[11]}" : '' )
      def end = ( it[6] ? sdf.format(it[6]) : '' ) + ( it[12] ? " / v:${it[12]}" : '' ) + ( it[13] ? " / i:${it[13]}" : '' )

      result.rows.add([
                       id:it[0],
                       isbn:it[1],
                       issn:it[2],
                       eissn:it[3],
                       doi:it[4],
                       start:start,
                       end:end,
                       url:it[7],
                       title:it[8],
                       eresId:it[9],
                       medium:'Journal'
                      ])
    }

    render result as JSON
  }
}
