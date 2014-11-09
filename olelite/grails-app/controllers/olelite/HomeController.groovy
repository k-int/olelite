package olelite

class HomeController {

  def GOKbSyncService

  def index() { 
  }

  def triggerSync() {
    try {
      GOKbSyncService.packageSync();
    }
    catch ( Exception e )  {
      log.error("problem",e)
    }
    redirect(action:'index');
  }

  def createTest() {
    log.debug("createTest");

    def new_pkg = new GokbPackage(packageName:'A new package', packageIdentifier:'00123')
    new_pkg.objId = java.util.UUID.randomUUID().toString()
    log.debug("Save new package: ${new_pkg.packageName} ${new_pkg.id} ${new_pkg}");
    if ( new_pkg.save() ) {
      log.debug("Created OK");
    }
    else {
      log.debug(new_pkg.errors)
    }


    redirect(action:'index');
  }
}
