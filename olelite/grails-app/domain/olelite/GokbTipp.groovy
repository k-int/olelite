package olelite

import java.sql.Blob
import org.hibernate.Session


class GokbTipp {

  static transients = [ 'sessionFactory' ]
  def sessionFactory

  Integer id
  BigDecimal version
  Date createdDate
  Date lastModifiedDate

  static constraints = {
    version(max:new BigDecimal(99999999.0),scale:0)
    createdDate(nullable:true, blank:false)
    lastModifiedDate(nullable:true, blank:false)
  }

  static mapping = {
    table 'ole_gokb_tipp_t'

    version column: 'VER_NBR'
    // Consider --    id generator: 'hilo', params: [table: 'ole_e_res_rec_s', column: 'id', max_lo: 100]
    // This works OK
    // id generator:'assigned', column:'PKG_ID'
    // seqhilo
    // id generator: 'hilo', params: [table: 'ole_gokb_pkg_t', column: 'PKG_ID', max_lo: 100], column:'PKG_ID'
    id generator: 'hilo', params: [table: 'ole_gokb_tipp_s', column: 'ID', max_lo: 100], column:'TIPP_ID'
    createdDate column:'DATE_CREATED'
    lastModifiedDate column:'LAST_UPDATED'
  }

}
