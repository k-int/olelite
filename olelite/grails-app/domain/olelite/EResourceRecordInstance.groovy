package olelite

// This is like IssueEntitlement in KB+
class EResourceRecordInstance {

  String id
  String objId
  BigDecimal version
  EResourceRecord parent
  String isbn
  String platform
  String publisher
  Date coverageStartDate
  String coverageStartVolume
  String coverageStartIssue
  Date coverageEndDate
  String coverageEndVolume
  String coverageEndIssue
  Date accessStartDate
  String accessStartVolume
  String accessStartIssue
  Date accessEndDate
  String accessEndVolume
  String accessEndIssue

  static constraints = {
    version(max:new BigDecimal(99999999),scale:0)
  }

  static mapping = {
    table 'ole_e_res_rec_ins_t'
    version column: 'VER_NBR'
    id generator:'assigned', column:'E_RES_INS_ID'
    objId column: 'OBJ_ID'
    // id generator: 'hilo', params: [table: 'ole_e_res_rec_s', column: 'id', max_lo: 100], column:'E_RES_INS_ID'
    isbn column:'ISBN'
    publisher column:'PUBHR'
    platform column:'PLTFRM'
    parent column:'E_RES_REC_ID'
    coverageStartDate column:'COV_SRT_DT'
    coverageStartVolume column:'COV_SRT_VOL'
    coverageStartIssue column:'COV_SRT_ISS'
    coverageEndDate column:'COV_END_DT'
    coverageEndVolume column:'COV_END_VOL'
    coverageEndIssue column:'COV_END_ISS'
    accessStartDate column:'PRPTL_ACC_SRT_DT'
    accessStartVolume column:'PRPTL_ACC_SRT_VOL'
    accessStartIssue column:'PRPTL_ACC_SRT_ISS'
    accessEndDate column:'PRPTL_ACC_END_DT'
    accessEndVolume column:'PRPTL_ACC_END_VOL'
    accessEndIssue column:'PRPTL_ACC_END_ISS'
  }

    // +-------------------+--------------+------+-----+---------+-------+
    // | Field             | Type         | Null | Key | Default | Extra |
    // +-------------------+--------------+------+-----+---------+-------+
    // | E_RES_INS_ID      | varchar(10)  | NO   | PRI |         |       |
    // | OBJ_ID            | varchar(36)  | YES  |     | NULL    |       |
    // | VER_NBR           | decimal(8,0) | YES  |     | NULL    |       |
    // | INST_ID           | varchar(50)  | YES  |     | NULL    |       |
    // | HOLD_ID           | varchar(50)  | YES  |     | NULL    |       |
    // | INST_FLAG         | varchar(10)  | YES  |     | NULL    |       |
    // | INST_NM           | varchar(500) | YES  |     | NULL    |       |
    // | ISBN              | varchar(800) | YES  |     | NULL    |       |
    // | INST_HOLD         | varchar(500) | YES  |     | NULL    |       |
    // | PUB_DISP_NTE      | varchar(800) | YES  |     | NULL    |       |
    // | PUBHR             | varchar(200) | YES  |     | NULL    |       |
    // | PLTFRM            | varchar(100) | YES  |     | NULL    |       |
    // | STATUS            | varchar(40)  | YES  |     | NULL    |       |
    // | SUB_STATUS        | varchar(40)  | YES  |     | NULL    |       |
    // | AUTO_INST_REC     | varchar(100) | YES  |     | NULL    |       |
    // | COV_SRT_DT        | datetime     | YES  |     | NULL    |       |
    // | COV_SRT_VOL       | varchar(40)  | YES  |     | NULL    |       |
    // | COV_SRT_ISS       | varchar(40)  | YES  |     | NULL    |       |
    // | COV_END_DT        | datetime     | YES  |     | NULL    |       |
    // | COV_END_VOL       | varchar(40)  | YES  |     | NULL    |       |
    // | COV_END_ISS       | varchar(40)  | YES  |     | NULL    |       |
    // | PRPTL_ACC_SRT_DT  | datetime     | YES  |     | NULL    |       |
    // | PRPTL_ACC_SRT_VOL | varchar(40)  | YES  |     | NULL    |       |
    // | PRPTL_ACC_SRT_ISS | varchar(40)  | YES  |     | NULL    |       |
    // | PRPTL_ACC_END_DT  | datetime     | YES  |     | NULL    |       |
    // | PRPTL_ACC_END_VOL | varchar(40)  | YES  |     | NULL    |       |
    // | PRPTL_ACC_END_ISS | varchar(40)  | YES  |     | NULL    |       |
    // | E_RES_REC_ID      | varchar(10)  | YES  | MUL | NULL    |       |
    // | BIB_ID            | varchar(40)  | YES  |     | NULL    |       |
    // +-------------------+--------------+------+-----+---------+-------+
    // 
}
