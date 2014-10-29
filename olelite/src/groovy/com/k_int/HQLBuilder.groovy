package com.k_int

import groovy.util.logging.*
import org.gokb.cred.*;
import org.codehaus.groovy.grails.commons.GrailsClassUtils

@Log4j
public class HQLBuilder {

  /**
   *  Accept a qbetemplate of the form
   *  [
   *		baseclass:'Fully.Qualified.Class.Name.To.Search',
   *		title:'Title Of Search',
   *		qbeConfig:[
   *			// For querying over associations and joins, here we will need to set up scopes to be referenced in the qbeForm config
   *			// Until we need them tho, they are omitted. qbeForm entries with no explicit scope are at the root object.
   *			qbeForm:[
   *				[
   *					prompt:'Name or Title',
   *					qparam:'qp_name',
   *					placeholder:'Name or title of item',
   *					contextTree:['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'name']
   *				],
   *				[
   *					prompt:'ID',
   *					qparam:'qp_id',
   *					placeholder:'ID of item',
   *					contextTree:['ctxtp':'qry', 'comparator' : 'eq', 'prop':'id', 'type' : 'java.lang.Long']
   *				],
   *				[
   *					prompt:'SID',
   *					qparam:'qp_sid',
   *					placeholder:'SID for item',
   *					contextTree:['ctxtp':'qry', 'comparator' : 'eq', 'prop':'ids.value']
   *				],
   *			],
   *			qbeResults:[
   *				[heading:'Type', property:'class.simpleName'],
   *				[heading:'Name/Title', property:'name', link:[controller:'resource',action:'show',id:'x.r.class.name+\':\'+x.r.id'] ]
   *			]
   *		]
   *	]
   *
   *
   */
  public static def build(grailsApplication, 
                          qbetemplate, 
                          params,
                          result, 
                          target_class, 
                          genericOIDService,
                          returnObjectsOrRows='objects') {
    // select o from Clazz as o where 

    // log.debug("build ${params}");

    // Step 1 : Walk through all the properties defined in the template and build a list of criteria
    def criteria = []
    qbetemplate.qbeConfig.qbeForm.each { query_prop_def ->
      if ( ( params[query_prop_def.qparam] != null ) && ( params[query_prop_def.qparam].length() > 0 ) ) {
        criteria.add([defn:query_prop_def, value:params[query_prop_def.qparam]]);
      }
    }

    qbetemplate.qbeConfig.qbeGlobals.each { global_prop_def ->
      // log.debug("Adding query global: ${global_prop_def}");
      // creat a contextTree so we can process the filter just like something added to the query tree
      // Is this global user selectable
      if ( global_prop_def.qparam != null ) {  // Yes
        if ( params[global_prop_def.qparam] == null ) { // If it's not be set
          if ( global_prop_def.default == 'on' ) { // And the default is set
            criteria.add([defn:[qparam:global_prop_def.prop.replaceAll('.','_'),contextTree:global_prop_def],value:global_prop_def.value])
          }
        }
        else if ( params[global_prop_def.qparam] == 'on' ) { // It's set explicitly, if its on, add the criteria
          criteria.add([defn:[qparam:global_prop_def.prop.replaceAll('.','_'),contextTree:global_prop_def],value:global_prop_def.value])
        }
      }
      else {
        criteria.add([defn:[qparam:global_prop_def.prop.replaceAll('.','_'),contextTree:global_prop_def],value:global_prop_def.value])
      }
    }

    def hql_builder_context = [:]
    hql_builder_context.declared_scopes = [:]
    hql_builder_context.query_clauses = []
    hql_builder_context.bindvars = [:]
    hql_builder_context.genericOIDService = genericOIDService;
    hql_builder_context.sort = params.sort ?: qbetemplate.defaultSort
    hql_builder_context.order = params.order ?: qbetemplate.defaultOrder

    def baseclass = target_class.getClazz()
    criteria.each { crit ->
      // log.debug("Processing crit: ${crit}");
      processProperty(hql_builder_context,crit,baseclass)
      // List props = crit.def..split("\\.")
    }

    // log.debug("At end of build, ${hql_builder_context}");
    hql_builder_context.declared_scopes.each { ds ->
      // log.debug("Scope: ${ds}");
    }

    hql_builder_context.query_clauses.each { qc ->
      // log.debug("QueryClause: ${qc}");
    }

    def hql = outputHql(hql_builder_context, qbetemplate)
    // log.debug("HQL: ${hql}");
    // log.debug("BindVars: ${hql_builder_context.bindvars}");

    def count_hql = "select count (o) ${hql}"
    def fetch_hql = null
    if ( returnObjectsOrRows=='objects' ) {
      fetch_hql = "select o ${hql}"
    }
    else {
      fetch_hql = "select ${buildFieldList(qbetemplate.qbeConfig.qbeResults)} ${hql}"
    }

    log.debug("Attempt count qry ${count_hql}");
    // log.debug("Attempt qry ${fetch_hql}");

    result.reccount = baseclass.executeQuery(count_hql, hql_builder_context.bindvars)[0]
    log.debug("Got count result: ${result.reccount}");

    def query_params = [:]
    if ( result.max )
      query_params.max = result.max;
    if ( result.offset )
      query_params.offset = result.offset

    log.debug("Get data rows..");
    result.recset = baseclass.executeQuery(fetch_hql, hql_builder_context.bindvars,query_params);
    log.debug("Returning..");
  }

  static def processProperty(hql_builder_context,crit,baseclass) {
    // log.debug("processProperty ${hql_builder_context}, ${crit}");
    switch ( crit.defn.contextTree.ctxtp ) {
      case 'qry':
        processQryContextType(hql_builder_context,crit,baseclass)
        break;
      case 'filter':
        processQryContextType(hql_builder_context,crit,baseclass)
        break;
      default:
        log.error("Unhandled property context type ${crit}");
        break;
    }
  }

  static def processQryContextType(hql_builder_context,crit, baseclass) {
    List l =  crit.defn.contextTree.prop.split("\\.")
    processQryContextType(hql_builder_context, crit, l, 'o', baseclass)
  }

  static def processQryContextType(hql_builder_context,crit, proppath, parent_scope, the_class) {

    // log.debug("processQryContextType.... ${proppath}");

    if ( proppath.size() > 1 ) {
      
      def head = proppath.remove(0)
      def newscope = parent_scope+'_'+head
      if ( hql_builder_context.declared_scopes.containsKey(newscope) ) {
        // Already established scope for this context
        // log.debug("${newscope} already a declared contest");
      }
      else {
        // Target class can be looked up in standard way.
        def target_class = GrailsClassUtils.getPropertyType(the_class, head)
          
        // Standard association, just make a bind variable..
        establishScope(hql_builder_context, parent_scope, head, newscope)
        processQryContextType(hql_builder_context,crit, proppath, newscope, target_class)
      }
    }
    else {
      // log.debug("head prop...");
      // If this is an ordinary property, add the operation. If it's a special, the make the extra joins
      // log.debug("Standard property...");
      // The property is a standard property
      addQueryClauseFor(crit,hql_builder_context,parent_scope+'.'+proppath[0])
    }
  }

  static def establishScope(hql_builder_context, parent_scope, property_to_join, newscope_name) {
    // log.debug("Establish scope ${newscope_name} as a child of ${parent_scope} property ${property_to_join}");
    hql_builder_context.declared_scopes[newscope_name] = "${parent_scope}.${property_to_join} as ${newscope_name}" 
  }

  static def addQueryClauseFor(crit, hql_builder_context, scoped_property) {

    switch ( crit.defn.contextTree.comparator ) {
      case 'eq':
        hql_builder_context.query_clauses.add("${crit.defn.contextTree.negate?'not ':''}${scoped_property} = :${crit.defn.qparam}");
        if ( crit.defn.type=='lookup' ) {
          hql_builder_context.bindvars[crit.defn.qparam] = hql_builder_context.genericOIDService.resolveOID2(crit.value)
        }
        else {
          switch ( crit.defn.contextTree.type ) {
            case 'java.lang.Long':
              hql_builder_context.bindvars[crit.defn.qparam] = Long.parseLong(crit.value)
              break;
            default:
              hql_builder_context.bindvars[crit.defn.qparam] = crit.value.toString();
              break;
          }
        }
        break;
      case 'ilike':
        hql_builder_context.query_clauses.add("${crit.defn.contextTree.negate?'not ':''}lower(${scoped_property}) like :${crit.defn.qparam}");
        def base_value = crit.value.toLowerCase()
        // if ( crit.defn.contextTree.normalise == true ) {
        //   base_value = org.gokb.GOKbTextUtils.normaliseString(base_value)
        // }
        hql_builder_context.bindvars[crit.defn.qparam] = ( ( crit.defn.contextTree.wildcard=='L' || crit.defn.contextTree.wildcard=='B') ? '%' : '') +
                                                         base_value +
                                                         ( ( crit.defn.contextTree.wildcard=='R' || crit.defn.contextTree.wildcard=='B') ? '%' : '')
      default:
        log.error("Unhandled comparator '${crit.defn.contextTree.comparator}'. crit: ${crit}");
    }
  }

  static def outputHql(hql_builder_context, qbetemplate) {
    StringWriter sw = new StringWriter()
    sw.write(" from ${qbetemplate.baseclass} as o\n")

    hql_builder_context.declared_scopes.each { scope_name,ds ->
      sw.write(" join ${ds}\n");
    }
    
    if ( hql_builder_context.query_clauses.size() > 0 ) {
      sw.write(" where");
      boolean conjunction=false
      hql_builder_context.query_clauses.each { qc ->
        if ( conjunction ) {
          // output and on second and subsequent clauses
          sw.write(" AND");
        }
        else {  
          conjunction=true
        }
        sw.write(" ");
        sw.write(qc);
      }
    }

    if ( ( hql_builder_context.sort != null ) && ( hql_builder_context.sort.length() > 0 ) ) {
      sw.write(" order by o.${hql_builder_context.sort} ${hql_builder_context.order}");
    }

    // Return the toString of the writer
    sw.toString();
  }

  static def buildFieldList(defns) {
    def result = new java.io.StringWriter()
    result.write('o.id');
    result.write(',o.class');
    defns.each { defn ->
      result.write(",o.");
      result.write(defn.property);
    }
    result.toString();
  }
}
