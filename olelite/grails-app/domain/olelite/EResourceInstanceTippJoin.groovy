package olelite

import org.apache.commons.lang.builder.HashCodeBuilder

// This is like IssueEntitlement in KB+
class EResourceInstanceTippJoin implements Serializable {

  GokbTipp tipp
  EResourceRecordInstance eresInst
  
  static constraints = {
  }

  static mapping = {
    table 'ole_gokb_tipp_eres_inst_t'
    tipp column:'TIPP_ID'
    eresInst column:'E_RES_INS_ID'
    id composite: ['tipp', 'eresInst']
    version false
  }

  boolean equals(other) {
    if (!(other instanceof EResourceInstanceTippJoin)) {
      return false
    }

    (( other.tipp?.id == tipp?.id ) && ( other.eresInst?.id == eresInst?.id ))
  }

  int hashCode() {
    def builder = new HashCodeBuilder()
    builder.append tipp?.id
    builder.append eresInst?.id
    builder.toHashCode()
  }

}
