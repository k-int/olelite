package olelite

class VendorHeader {

  BigDecimal id
  String objId
  String vendorType = 'PO'
  BigDecimal version

  static constraints = {
    id(max:new BigDecimal(99999999.0),scale:0)
    version(max:new BigDecimal(99999999.0),scale:0)
  }

  // pur_vndr_alias_t for vendor aliases
  static mapping = {
    table 'pur_vndr_hdr_t'

    // | VNDR_HDR_GNRTD_ID    | decimal(10,0) | NO   | PRI | 0       |       |
    // id generator: 'hilo', params: [table: 'pur_vndr_dtl_t', column: 'VNDR_HDR_GNRTD_ID', max_lo: 100], column:'VNDR_HDR_GNRTD_ID'
    id generator: 'increment', column:'VNDR_HDR_GNRTD_ID'
    // | OBJ_ID                 | varchar(36)   | NO   | UNI | NULL    |       |
    objId column:'OBJ_ID'
    // | VER_NBR                | decimal(8,0)  | NO   |     | 1       |       |
    version column: 'VER_NBR'
    // | VNDR_TYP_CD          | varchar(4)    | NO   | MUL | NULL    |       |
    vendorType column: 'VNDR_TYP_CD'
  }
}
