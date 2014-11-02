package olelite

class HomeController {

  def GOKbSyncService

  def index() { 
  }

  def triggerSync() {
     GOKbSyncService.packageSync();
  }
}
