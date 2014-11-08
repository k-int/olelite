package olelite

import java.sql.Blob
import org.hibernate.Session


class GokbPackage {

  static transients = [ 'sessionFactory' ]
  def sessionFactory

  Integer id
  String packageName
  String objId
  String packageIdentifier
  BigDecimal version
  Blob content
  BigDecimal numTitles
  String globalStatus
  String localStatus
  String primaryPlatform
  String primaryPlatformProvider
  Date createdDate
  Date lastModifiedDate

  // Done this to avoid modifying the OLE tables - ideally 1:m between this and ERR
  // EResourceRecord eres

  static constraints = {
    version(max:new BigDecimal(99999999.0),scale:0)
    numTitles(max:new BigDecimal(99999999.0),scale:0)
    numTitles(nullable:true, blank:false)
    globalStatus(nullable:true, blank:true)
    localStatus(nullable:true, blank:true)
    primaryPlatform(nullable:true, blank:true)
    primaryPlatformProvider(nullable:true, blank:true)
    createdDate(nullable:true, blank:false)
    lastModifiedDate(nullable:true, blank:false)
    // eres(nullable:true, blank:false)
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
    numTitles column:'TITLE_COUNT'
    globalStatus column:'GLOBAL_STATUS'
    localStatus column:'LOCAL_STATUS'
    primaryPlatform column:'PRIMARY_PLAT'
    primaryPlatformProvider column:'PRIMARY_PLAT_PROV'
    createdDate column:'DATE_CREATED'
    lastModifiedDate column:'LAST_UPDATED'
    // eres column:'ERES_FK'
  }

  def setContent(byte[] bytes) {
    Session hib_ses = sessionFactory.getCurrentSession()
    content = hib_ses.getLobHelper().createBlob(bytes);
  }

  def setContent(InputStream is, long length) {
    Session hib_ses = sessionFactory.getCurrentSession()
    content = hib_ses.getLobHelper().createBlob(is, length)
  }

  def getContent() {
    return content?.binaryStream
  }

  Long getContentSize() {
    return content?.length() ?: 0
  }


}
