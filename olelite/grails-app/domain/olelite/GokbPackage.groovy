package olelite

class GokbPackage {

  String id
  String packageName
  String packageIdentifier
  BigDecimal version

  static constraints = {
    version(max:new BigDecimal(99999999.0),scale:0)
  }

  static mapping = {
    table 'ole_gokb_pkg_t'

    version column: 'VER_NBR'
    // Consider --    id generator: 'hilo', params: [table: 'ole_e_res_rec_s', column: 'id', max_lo: 100]
    // id generator:'assigned', column:'E_RES_REC_ID'
    // seqhilo
    id generator: 'hilo', params: [table: 'ole_gokb_pkg_s', column: 'id', max_lo: 100], column:'PKG_ID'
    packageName column:'PKG_NAME'
    packageIdentifier column:'PKG_IDENTIFIER'
  }
}
