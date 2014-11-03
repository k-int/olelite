package olelite

class GokbPackage {

  Integer id
  String packageName
  String objId
  String packageIdentifier
  BigDecimal version
  byte[] content

  static constraints = {
    version(max:new BigDecimal(99999999.0),scale:0)
  }

  static mapping = {
    table 'ole_gokb_pkg_t'

    version column: 'VER_NBR'
    // Consider --    id generator: 'hilo', params: [table: 'ole_e_res_rec_s', column: 'id', max_lo: 100]
    // This works OK
    // id generator:'assigned', column:'PKG_ID'
    // seqhilo
    // id generator: 'hilo', params: [table: 'ole_gokb_pkg_t', column: 'PKG_ID', max_lo: 100], column:'PKG_ID'
    id generator: 'hilo', params: [table: 'ole_gokb_pkg_s', column: 'ID', max_lo: 100], column:'PKG_ID'
    packageName column:'PKG_NAME'
    objId column:'OBJ_ID'
    content column:'PKG_CONTENT', type:'blob'
    packageIdentifier column:'PKG_IDENTIFIER'
  }
}
