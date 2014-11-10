package olelite

class Vendor {

  BigDecimal id
  String objId
  String name
  String active
  String collectTax
  BigDecimal version
  VendorHeader header

  static constraints = {
    id(max:new BigDecimal(99999999.0),scale:0)
    version(max:new BigDecimal(99999999.0),scale:0)
  }

  // static hasOne = [
  //   pkg:GokbPackage
  // ]

  // static mappedBy = [
  //   pkg:'eres'
  // ]

  // pur_vndr_alias_t for vendor aliases
  static mapping = {
    table 'pur_vndr_dtl_t'


    // | VNDR_HDR_GNRTD_ID      | decimal(10,0) | NO   | PRI | 0       |       |
    // id generator: 'hilo', params: [table: 'pur_vndr_dtl_t', column: 'VNDR_HDR_GNRTD_ID', max_lo: 100], column:'VNDR_HDR_GNRTD_ID'
    // id generator: 'increment', column:'VNDR_HDR_GNRTD_ID'
    id generator: 'assigned', column:'VNDR_HDR_GNRTD_ID'
    
    header column:'VNDR_HDR_GNRTD_ID', insertable: false, updateable: false
    // | VNDR_DTL_ASND_ID       | decimal(10,0) | NO   | PRI | 0       |       |
    // | OBJ_ID                 | varchar(36)   | NO   | UNI | NULL    |       |
    objId column:'OBJ_ID'
    // | VER_NBR                | decimal(8,0)  | NO   |     | 1       |       |
    version column: 'VER_NBR'
    // | VNDR_PARENT_IND        | varchar(1)    | YES  |     | NULL    |       |
    // | VNDR_NM                | varchar(45)   | NO   |     | NULL    |       |
    name column:'VNDR_NM'
    // | DOBJ_MAINT_CD_ACTV_IND | varchar(1)    | NO   |     | NULL    |       |
    active column:'DOBJ_MAINT_CD_ACTV_IND'
    // | VNDR_INACTV_REAS_CD    | varchar(4)    | YES  | MUL | NULL    |       |
    // | VNDR_DUNS_NBR          | varchar(9)    | YES  |     | NULL    |       |
    // | VNDR_PMT_TERM_CD       | varchar(5)    | YES  | MUL | NULL    |       |
    // | VNDR_SHP_TTL_CD        | varchar(4)    | YES  | MUL | NULL    |       |
    // | VNDR_SHP_PMT_TERM_CD   | varchar(4)    | YES  | MUL | NULL    |       |
    // | VNDR_CNFM_IND          | varchar(1)    | YES  |     | NULL    |       |
    // | VNDR_PRPYMT_IND        | varchar(1)    | YES  |     | NULL    |       |
    // | VNDR_CCRD_IND          | varchar(1)    | YES  |     | NULL    |       |
    // | VNDR_MIN_ORD_AMT       | decimal(7,2)  | YES  |     | NULL    |       |
    // | VNDR_URL_ADDR          | varchar(45)   | YES  |     | NULL    |       |
    // | VNDR_SOLD_TO_NM        | varchar(100)  | YES  |     | NULL    |       |
    // | VNDR_RMT_NM            | varchar(45)   | YES  |     | NULL    |       |
    // | VNDR_RSTRC_IND         | varchar(1)    | YES  |     | NULL    |       |
    // | VNDR_RSTRC_REAS_TXT    | varchar(60)   | YES  |     | NULL    |       |
    // | VNDR_RSTRC_DT          | datetime      | YES  |     | NULL    |       |
    // | VNDR_RSTRC_PRSN_ID     | varchar(40)   | YES  |     | NULL    |       |
    // | VNDR_SOLD_TO_GNRTD_ID  | decimal(10,0) | YES  | MUL | NULL    |       |
    // | VNDR_SOLD_TO_ASND_ID   | decimal(10,0) | YES  |     | NULL    |       |
    // | VNDR_1ST_LST_NM_IND    | varchar(1)    | YES  |     | NULL    |       |
    // | COLLECT_TAX_IND        | varchar(1)    | NO   |     | Y       |       |
    collectTax column:'COLLECT_TAX_IND'
    // | OLE_CURR_TYP_ID        | decimal(8,0)  | YES  | MUL | NULL    |       |
    // | VNDR_PMT_MTHD_ID       | decimal(10,0) | YES  | MUL | NULL    |       |
    // | VNDR_LINK_ID           | varchar(40)   | YES  |     | NULL    |       |
    // | VNDR_CLM_INTRVL        | varchar(40)   | YES  |     | NULL    |       |

  }
}
