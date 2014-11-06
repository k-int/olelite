'use strict';

(function() {
  var app = angular.module('GOKb',[ 'ui.bootstrap', 
                                    'ui.grid', 
                                    // 'ui.grid.pagination',
                                    'ui.grid.infiniteScroll',
                                    'ui.grid.resizeColumns'
                                    ]);

  app.run(function($http) {
    $http.defaults.headers.common.Authorization = 'Basic YWRtaW46YWRtaW4='
  });

  app.controller('GOKbCtrl', ['$scope', '$http', '$log', 'gokbService', function($scope,$http,$log,gokbService) {

    $scope.qparams = {};
    $scope.gridOptions = {};
    $scope.searchStatus = ''

    // $scope.gridOptions.infiniteScrollPercentage = 20;
    // $scope.gridOptions.infiniteScroll = 20;

    $scope.search = function() {
      $log.debug("search %o",$scope.qparams);
      $scope.gridOptions.data = [];
      gokbService.getPackages($scope.gridOptions.data, null, $scope.qparams, $scope)
    }
 
    $scope.gridOptions.columnDefs = [
      {name:'Package Name', field:'packageName',enableColumnResizing: true },
      {name:'Global Status', field:'status',enableColumnResizing: true },
      {name:'Local Status',enableColumnResizing: true },
      {name:'Primary Platform', field:'nominalPlatform',enableColumnResizing: true },
      {name:'Primary Platform Provider', field:'provider',enableColumnResizing: true },
      {name:'# Titles', field:'numTitles',enableColumnResizing: true },
      {name:'Date Created', field:'dateCreated',enableColumnResizing: true },
      {name:'Date Updated', field:'lastUpdated',enableColumnResizing: true }
    ];

    var pageno=0;
    var total = 1000;

    $scope.gridOptions.data = [];
    gokbService.getPackages($scope.gridOptions.data, null, $scope.qparams, $scope)

    $scope.gridOptions.onRegisterApi = function (gridApi) {
      $scope.gridApi = gridApi;

      gridApi.infiniteScroll.on.needLoadMoreData($scope,function(){

        $log.debug("gridApi.infiniteScroll.on.needLoadMoreData");

        gokbService.getPackages($scope.gridOptions.data, gridApi, $scope.qparams, $scope);
        ++pageno;
        gridApi.infiniteScroll.dataLoaded();
      });
    };
 
  }]);

  app.factory('gokbService', ['$http', '$log', function($http, $log) {
    var urlBase = 'http://localhost:8080/olelite/api';
    // var urlBase = 'https://gokb.k-int.com/gokb/api';
    var dataFactory = {};
    
    dataFactory.getPackages = function (tgt, gridApi, qparams, scope) {
      $log.debug("getPackages tgt.length:%i",tgt.length);

      qparams.offset = tgt.length;
      qparams.tmpl='packages';
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

    return dataFactory;
  }]);


})();

