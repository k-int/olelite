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
    $scope.gridOptions.enableRowSelection=true;
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
      {name:'Collection Name', field:'title',enableColumnResizing: true },
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

      gridApi.selection.on.rowSelectionChanged($scope,function(row){
        $log.debug("Selected row: %o",row);
        if ( row && row.entity ) {
          $scope.selectedPackage = row.entity;
        }
      });

    };

  }]);

  app.controller('AcqCollectionCtrl', ['$scope', '$http', '$log', '$location', '$routeParams', 'oleService', 
                                   function($scope,$http,$log,$location,$routeParams,oleService) {

    $scope.model = {
      collectionId: $routeParams.collectionId
    };

    oleService.retrieve({cls:'hello',id:'220'}, $scope);

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

    dataFactory.retrieve = function (qparams, scope) {
      $log.debug("retrieve...");
      $http.get(urlBase+'/retrieve', { params : qparams } ).
        success(function(data,status,headers,config) {
          log.debug("Got response: %o",data);
        }).
        error(function(data,status,headers,config) {
          $log.debug("Error");
        });
    };

    return dataFactory;
  }]);


})();
