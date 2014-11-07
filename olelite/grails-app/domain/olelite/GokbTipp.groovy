package olelite

class GokbTipp {

  Integer id
  BigDecimal version
  String isbn
  String issn
  String eissn
  String doi
  GokbPackage pkg
  String accessUrl
  Date coverageStartDate
  String coverageStartVolume
  String coverageStartIssue
  Date coverageEndDate
  String coverageEndVolume
  String coverageEndIssue

  Date createdDate
  Date lastModifiedDate

  static constraints = {
    version(max:new BigDecimal(99999999.0),scale:0)
    isbn(nullable:true, blank:true);
    issn(nullable:true, blank:true);
    eissn(nullable:true, blank:true);
    doi(nullable:true, blank:true);
    accessUrl(nullable:true, blank:true);
    coverageStartDate(nullable:true, blank:true);
    coverageStartVolume(nullable:true, blank:true);
    coverageStartIssue(nullable:true, blank:true);
    coverageEndDate(nullable:true, blank:true);
    coverageEndVolume(nullable:true, blank:true);
    coverageEndIssue(nullable:true, blank:true);
    createdDate(nullable:true, blank:false)
    lastModifiedDate(nullable:true, blank:false)
  }

  static mapping = {
    table 'ole_gokb_tipp_t'
    version column: 'VER_NBR'
    id generator: 'hilo', params: [table: 'ole_gokb_tipp_s', column: 'ID', max_lo: 100], column:'TIPP_ID'
    isbn column:'ISBN'
    issn column:'ISSN'
    eissn column:'EISSN'
    doi column:'DOI'
    pkg column:'GOKB_PKG_FK'
    accessUrl column:'ACCESS_URL'
    coverageStartDate column:'COV_START_DATE'
    coverageStartVolume column:'COV_START_VOL'
    coverageStartIssue column:'COV_START_ISS'
    coverageEndDate column:'COV_END_DATE'
    coverageEndVolume column:'COV_END_VOL'
    coverageEndIssue column:'COV_END_ISS'
    createdDate column:'DATE_CREATED'
    lastModifiedDate column:'LAST_UPDATED'
  }

}
