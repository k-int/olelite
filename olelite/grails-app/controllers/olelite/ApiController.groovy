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

      result.rows = []
      qresult.recset.each { r ->
        def row = []
        def first = true
        r.each { c ->
          if ( first ) {
            row.add(c.class.name)
            row.add(c.id);
            first = false
          }
          else {
            row.add(c)
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


  def private doQuery (qbetemplate, params, result) {
    log.debug("doQuery ${result}");
    def target_class = grailsApplication.getArtefact("Domain",qbetemplate.baseclass);
    com.k_int.HQLBuilder.build(grailsApplication, qbetemplate, params, result, target_class, genericOIDService, 'rows')
    log.debug("process recset..");
  }

}
