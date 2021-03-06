'use strict';

(function() {
  var app = angular.module('Acquisitions',[ 'ui.bootstrap', 
                                            'ui.grid',    // See http://ui-grid.info/ 'ui.grid.pagination',
                                            'ui.grid.infiniteScroll',
                                            'ui.grid.resizeColumns',
                                            'ui.grid.selection'
                                            ]);

  app.run(function($http) {
    $http.defaults.headers.common.Authorization = 'Basic YWRtaW46YWRtaW4='
  });

  app.controller('AcqCollectionsCtrl', ['$scope', '$http', '$log', '$location', 'oleService', function($scope,$http,$log,$location,oleService) {

    $scope.qparams = {};
    $scope.gridOptions = {};
    $scope.gridOptions.multiSelect=false;
    $scope.gridOptions.enableRowSelection=false;
    $scope.gridOptions.enableSelectAll=false;
    $scope.searchStatus = ''
    $scope.selectedPackage = null;

    // $scope.gridOptions.infiniteScrollPercentage = 20;
    // $scope.gridOptions.infiniteScroll = 20;

    $scope.search = function() {
      $log.debug("search %o",$scope.qparams);
      $scope.gridOptions.data = [];
      oleService.getPackages($scope.gridOptions.data, null, $scope.qparams, $scope)
    }
 
    $scope.gridOptions.columnDefs = [
      {name:'Collection Name', 
       field:'title',
       enableColumnResizing: true ,
       cellTemplate: '<a href="/browserApp/#/acquisitions/collections/{{row.entity.__id}}">{{row.entity.title}}</a>'
       },
    ];

    var pageno=0;
    var total = 1000;

    $scope.gridOptions.data = [];
    oleService.getPackages($scope.gridOptions.data, null, $scope.qparams, $scope);

    $scope.gridOptions.onRegisterApi = function (gridApi) {
      $scope.gridApi = gridApi;

      gridApi.infiniteScroll.on.needLoadMoreData($scope,function(){

        $log.debug("gridApi.infiniteScroll.on.needLoadMoreData");

        oleService.getPackages($scope.gridOptions.data, gridApi, $scope.qparams, $scope);
        ++pageno;
        gridApi.infiniteScroll.dataLoaded();
      });

      // gridApi.selection.on.rowSelectionChanged($scope,function(row){
      //   $log.debug("Selected row: %o",row);
      //   if ( row && row.entity ) {
      //     $scope.selectedPackage = row.entity;
      //   }
      // });


    };

  }]);

  app.controller('AcqCollectionCtrl', ['$scope', '$http', '$log', '$location', '$routeParams', 'oleService', 
                                   function($scope,$http,$log,$location,$routeParams,oleService) {

    $scope.model = {
      collectionId: $routeParams.collectionId
    };

    oleService.retrieve({oid:'olelite.EResourceRecord:'+$routeParams.collectionId}, $scope);

    $scope.qparams = {};
    $scope.qparams.eresid = $routeParams.collectionId;
    $scope.gridOptions = {};
    $scope.gridOptions.multiSelect=true;
    $scope.gridOptions.enableRowSelection=true;
    $scope.gridOptions.enableSelectAll=false;
    $scope.searchStatus = ''
    $scope.numSelected=0
    $scope.selectedTipps = []

    // $scope.gridOptions.infiniteScrollPercentage = 20;
    // $scope.gridOptions.infiniteScroll = 20;

    $scope.gridOptions.columnDefs = [
      {name:'Title',
       field:'title',
       enableColumnResizing: true ,
       // cellTemplate: '<a href="/browserApp/#/acquisitions/collections/{{row.entity.__id}}">{{row.entity.title}}</a>'
       },
       {name:'Active', field:'active', enableColumnResizing: true},
       {name:'Global status', field:'status', enableColumnResizing: true},
       {name:'Type', field:'medium', enableColumnResizing: true},
       {name:'ISBN', field:'isbn', enableColumnResizing: true},
       {name:'ISSN', field:'issn', enableColumnResizing: true},
       {name:'eISSN', field:'eissn', enableColumnResizing: true},
       {name:'DOI', field:'doi', enableColumnResizing: true},
       {name:'Start', field:'start', enableColumnResizing: true},
       {name:'End', field:'end', enableColumnResizing: true},
       {name:'Url', field:'url', enableColumnResizing: true},
    ];

    var pageno=0;
    var total = 1000;

    $scope.gridOptions.data = [];
    oleService.getTipps($scope.gridOptions.data, null, $scope.qparams, $scope);

    $scope.gridOptions.onRegisterApi = function (gridApi) {
      $scope.gridApi = gridApi;

      gridApi.infiniteScroll.on.needLoadMoreData($scope,function(){

        $log.debug("gridApi.infiniteScroll.on.needLoadMoreData");

        oleService.getTipps($scope.gridOptions.data, gridApi, $scope.qparams, $scope);
        ++pageno;
        gridApi.infiniteScroll.dataLoaded();
      });

      gridApi.selection.on.rowSelectionChanged($scope,function(row){
        if (row.isSelected) {
          $scope.numSelected++
          $scope.selectedTipps.push(row.entity.id);
        }
        else {
          $scope.numSelected--
          var index = $scope.selectedTipps.indexOf(row.entity.id);
          $scope.selectedTipps.splice(index, 1);
        }
      });


    };

    $scope.ingestTipps = function() {
      // Ingest tipps
      var params = {}
      params.parentResource = $scope.model.collectionId;
      params.tippIds = $scope.selectedTipps;
      oleService.createResourceInstances(params, $scope);
    };



  }]);


  app.controller('AcqVendorsCtrl', ['$scope', '$http', '$log', '$location', '$routeParams', 'oleService', 
                                   function($scope,$http,$log,$location,$routeParams,oleService) {

    $scope.qparams = {};
    $scope.gridOptions = {};
    $scope.gridOptions.multiSelect=false;
    $scope.gridOptions.enableRowSelection=true;
    $scope.gridOptions.enableSelectAll=false;
    $scope.searchStatus = ''
    $scope.selectedPackage = null;

    // $scope.gridOptions.infiniteScrollPercentage = 20;
    // $scope.gridOptions.infiniteScroll = 20;

    $scope.search = function() {
      $log.debug("search %o",$scope.qparams);
      $scope.gridOptions.data = [];
      gokbService.search($scope.gridOptions.data, null, $scope.qparams, $scope)
    }

    $scope.gridOptions.columnDefs = [
      {name:'Vendor Name', field:'name',enableColumnResizing: true },
    ];

    var pageno=0;
    var total = 1000;

    $scope.gridOptions.data = [];
    oleService.search($scope.gridOptions.data, null, $scope.qparams, $scope);

    $scope.gridOptions.onRegisterApi = function (gridApi) {
      $scope.gridApi = gridApi;

      gridApi.infiniteScroll.on.needLoadMoreData($scope,function(){

        $log.debug("gridApi.infiniteScroll.on.needLoadMoreData");

        oleService.search($scope.gridOptions.data, gridApi, $scope.qparams, $scope);
        ++pageno;
        gridApi.infiniteScroll.dataLoaded();
      });

    };

  }]);


  app.controller('AcqVendorCtrl', ['$scope', '$http', '$log', '$location', '$routeParams', 'oleService', 
                                   function($scope,$http,$log,$location,$routeParams,oleService) {

    $scope.createVendor = function() {
      $log.debug("Create vendor ctrl "+$scope.vendorName);
      oleService.createVendor($scope);
    }

    
  }]);


  app.factory('oleService', ['$http', '$log', function($http, $log) {
    // var urlBase = 'http://192.168.2.69:8080/olelite/api';
    var urlBase = 'http://localhost:8080/olelite/api';
    // var urlBase = 'https://gokb.k-int.com/gokb/api';
    var dataFactory = {};
    
    dataFactory.getPackages = function (tgt, gridApi, qparams, scope) {
      $log.debug("getPackages tgt.length:%i",tgt.length);

      qparams.offset = tgt.length;
      qparams.tmpl='collections';
      qparams.max='25';

      $http.get(urlBase+'/search', { params : qparams } ).
        success(function(data,status,headers,config) {
          scope.searchStatus = 'Search found '+data.reccount+' records';

          $log.debug("result: %o",data);
          for (var i = 0; i < data.rows.length; i++) {
            // $log.debug("Adding row %d : %o", i, data.rows[i]);
            tgt.push(data.rows[i]);
            if ( gridApi )
              gridApi.infiniteScroll.dataLoaded();
          }
        }).
        error(function(data,status,headers,config) {
          $log.debug("Error");
        });

      return {}

    };

    dataFactory.getTipps = function (tgt, gridApi, qparams, scope) {
      $log.debug("getTipps tgt.length:%i",tgt.length);

      qparams.offset = tgt.length;
      qparams.max='25';

      // http://localhost:8080/olelite/api/getTipps?eresid=150
      $http.get(urlBase+'/getTipps', { params : qparams } ).
        success(function(data,status,headers,config) {
          scope.searchStatus = 'Search found '+data.reccount+' records';

          $log.debug("result: %o",data);
          for (var i = 0; i < data.rows.length; i++) {
            // $log.debug("Adding row %d : %o", i, data.rows[i]);
            tgt.push(data.rows[i]);
            if ( gridApi )
              gridApi.infiniteScroll.dataLoaded();
          }
        }).
        error(function(data,status,headers,config) {
          $log.debug("Error");
        });

      return {}
    };

    dataFactory.createResourceInstances = function (qparams, scope) {
      $http.post(urlBase+'/createResourceInstances', qparams).
        success(function(data,status,headers,config) {
          $log.debug("OK");
        }).
        error(function(data,status,headers,config) {
          $log.debug("Error");
        });

      return {}
    };

    dataFactory.search = function (tgt, gridApi, qparams, scope) {
      $log.debug("search tgt.length:%i",tgt.length);

      qparams.offset = tgt.length;
      qparams.tmpl='vendors';
      qparams.max='25';

      $http.get(urlBase+'/search', { params : qparams } ).
        success(function(data,status,headers,config) {
          scope.searchStatus = 'Search found '+data.reccount+' records';

          $log.debug("result: %o",data);
          for (var i = 0; i < data.rows.length; i++) {
            // $log.debug("Adding row %d : %o", i, data.rows[i]);
            tgt.push(data.rows[i]);
            if ( gridApi )
              gridApi.infiniteScroll.dataLoaded();
          }
        }).
        error(function(data,status,headers,config) {
          $log.debug("Error");
        });

      return {}

    };


    dataFactory.retrieve = function (qparams, scope) {
      $log.debug("retrieve...");
      $http.get(urlBase+'/retrieve', { params : qparams } ).
        success(function(data,status,headers,config) {
          $log.debug("Got response: %o",data);
          scope.model.rec = data
        }).
        error(function(data,status,headers,config) {
          $log.debug("Error");
        });
    };

    dataFactory.createVendor = function (scope) {
      $log.debug("dataFactory.createVendor..."+scope.vendorName);
      var qparams = { vendorName: scope.vendorName };
      $http.get(urlBase+'/createVendor', { params : qparams } ).
        success(function(data,status,headers,config) {
          $log.debug("Got response: %o",data);
        }).
        error(function(data,status,headers,config) {
          $log.debug("Error");
        });
    };



    return dataFactory;
  }]);


})();

