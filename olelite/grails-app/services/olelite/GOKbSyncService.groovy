package olelite

import grails.transaction.Transactional
import com.k_int.goai.OaiClient
import java.text.SimpleDateFormat
import org.springframework.transaction.annotation.*

@Transactional
class GOKbSyncService {

  def packageSync() {
    def oai_client = new OaiClient(host:'https://test-gokb.kuali.org/gokb/oai/packages'); // ?verb=listRecords&metadataPrefix=gokb
    // def oai_client = new OaiClient(host:'https://gokb.k-int.com/gokb/oai/packages'); // ?verb=listRecords&metadataPrefix=gokb
    def max_timestamp = 0
    def date = new Date(0);

    log.debug("Collect package changes since ${date}");

    oai_client.getChangesSince(date, 'gokb') { rec ->
      log.debug("Process..");
      log.debug("Got OAI Record ${rec.header.identifier} datestamp: ${rec.header.datestamp}");
      log.debug("Pakcage name (${rec.metadata.gokb.package.'@id'}) : ${rec.metadata.gokb.package.name.text()}");
      rec.metadata.gokb.package.TIPPs.TIPP.each { tipp ->
        // log.debug("  tipp -> ${tipp.@id} ${tipp.title.name.text}");
      }
    }

  }


}
