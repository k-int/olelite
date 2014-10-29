package olelite

import static java.util.UUID.randomUUID

class ApiController {

  def grailsApplication
  
  def search() {
    def result = [:]

    User user = springSecurityService.currentUser

    log.debug("Entering SearchController:index");

    result.max = params.max ? Integer.parseInt(params.max) : ( user.defaultPageSize ?: 10 );
    result.offset = params.offset ? Integer.parseInt(params.offset) : 0;

    if ( request.JSON ) {

        result.qbetemplate = request.JSON.cfg

        // Looked up a template from somewhere, see if we can execute a search
        if ( result.qbetemplate ) {
          log.debug("Execute query");
          def qresult = [max:result.max, offset:result.offset]
          result.rows = doQuery(result.qbetemplate, params, qresult)
          log.debug("Query complete");
          result.lasthit = result.offset + result.max > qresult.reccount ? qresult.reccount : ( result.offset + result.max )
  
          // Add the page information.
          result.page_current = (result.offset / result.max) + 1
          result.page_total = (qresult.reccount / result.max).toInteger() + (qresult.reccount % result.max > 0 ? 1 : 0)
        }
        else {
          log.error("no template ${result?.qbetemplate}");
        }
    }
    else {
      log.debug("No request json");
    }

    render result as JSON
  }

  def private doQuery (qbetemplate, params, result) {
    log.debug("doQuery ${result}");
    def target_class = grailsApplication.getArtefact("Domain",qbetemplate.baseclass);
    com.k_int.HQLBuilder.build(grailsApplication, qbetemplate, params, result, target_class, genericOIDService)
    def resultrows = []

    log.debug("process recset..");
    int seq = result.offset
    result.recset.each { rec ->
      // log.debug("process rec..");
      def response_row = [:]
      response_row['__oid'] = rec.class.name+':'+rec.id
      response_row['__seq'] = seq++
      qbetemplate.qbeConfig.qbeResults.each { r ->
        response_row[r.heading] = groovy.util.Eval.x(rec, 'x.' + r.property)
      }
      resultrows.add(response_row);
    }
    resultrows
  }

}
