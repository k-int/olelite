package olelite

class EResourceRecord {

  String id
  String title
  String description
  String isbn
  BigDecimal version

  static constraints = {
    version(max:new BigDecimal(99999999.0),scale:0)
  }

  static mapping = {
    table 'ole_e_res_rec_t'

    version column: 'VER_NBR'
    // Consider --    id generator: 'hilo', params: [table: 'ole_e_res_rec_s', column: 'id', max_lo: 100]
    // id generator:'assigned', column:'E_RES_REC_ID'
    // seqhilo
    id generator: 'hilo', params: [table: 'ole_e_res_rec_s', column: 'id', max_lo: 100], column:'E_RES_REC_ID'
    title column:'TITLE'
    description column:'DESCR'
    isbn column:'ISBN', length:100
    // | E_RES_REC_ID      | varchar(10)  | NO   | PRI |         |       |
    // | OBJ_ID            | varchar(36)  | YES  |     | NULL    |       |
    // | VER_NBR           | decimal(8,0) | YES  |     | NULL    |       |
    // | TITLE             | varchar(100) | YES  |     | NULL    |       |
    // | DESCR             | varchar(800) | YES  |     | NULL    |       |
    // | PUBHR             | varchar(500) | YES  |     | NULL    |       |
    // | GOKB_ID           | varchar(100) | YES  |     | NULL    |       |
    // | ISBN              | varchar(100) | YES  |     | NULL    |       |
    // | STAT_ID           | varchar(40)  | YES  | MUL | NULL    |       |
    // | STAT_DT           | varchar(40)  | YES  |     | NULL    |       |
    // | PLTFRM_PROV       | varchar(250) | YES  |     | NULL    |       |
    // | FD_CD             | varchar(10)  | YES  |     | NULL    |       |
    // | VNDR_NM           | varchar(40)  | YES  |     | NULL    |       |
    // | VNDR_ID           | varchar(40)  | YES  |     | NULL    |       |
    // | ESTD_PR           | varchar(40)  | YES  |     | NULL    |       |
    // | ORD_TYP_ID        | decimal(8,0) | YES  |     | NULL    |       |
    // | PYMT_TYP_ID       | varchar(10)  | YES  | MUL | NULL    |       |
    // | PCKG_TYP_ID       | varchar(10)  | YES  | MUL | NULL    |       |
    // | PCKG_SCP_ID       | varchar(10)  | YES  | MUL | NULL    |       |
    // | BRKBLE            | varchar(1)   | YES  |     | NULL    |       |
    // | FD_TITLE_LST      | varchar(1)   | YES  |     | NULL    |       |
    // | NTE               | varchar(800) | YES  |     | NULL    |       |
    // | PUB_DISP_NOTE     | varchar(800) | YES  |     | NULL    |       |
    // | REQ_SEL_COMM      | varchar(800) | YES  |     | NULL    |       |
    // | REQ_PRTY_ID       | varchar(40)  | YES  | MUL | NULL    |       |
    // | TECH_REQ          | varchar(800) | YES  |     | NULL    |       |
    // | ACC_TYP_ID        | varchar(40)  | YES  | MUL | NULL    |       |
    // | NUM_SIMULT_USER   | varchar(25)  | YES  |     | NULL    |       |
    // | AUTHCAT_TYP_ID    | varchar(40)  | YES  | MUL | NULL    |       |
    // | STAT_SRCH_CD_ID   | decimal(8,0) | YES  | MUL | NULL    |       |
    // | ACC_LOC_ID        | varchar(40)  | YES  |     | NULL    |       |
    // | TRL_ND            | varchar(1)   | YES  |     | NULL    |       |
    // | TRL_STAT          | varchar(40)  | YES  |     | NULL    |       |
    // | LIC_ND            | varchar(1)   | YES  |     | NULL    |       |
    // | LIC_REQ_STAT      | varchar(40)  | YES  |     | NULL    |       |
    // | ORD_PAY_STAT      | varchar(40)  | YES  |     | NULL    |       |
    // | ACT_STAT          | varchar(40)  | YES  |     | NULL    |       |
    // | DEF_COVR          | varchar(100) | YES  |     | NULL    |       |
    // | DEF_PER_ACC       | varchar(100) | YES  |     | NULL    |       |
    // | EINST_FLAG        | varchar(1)   | YES  |     | NULL    |       |
    // | E_RES_REC_DOC_NUM | varchar(40)  | YES  |     | NULL    |       |
  }
}
