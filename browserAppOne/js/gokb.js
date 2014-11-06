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

    // $scope.gridOptions.infiniteScrollPercentage = 20;
    // $scope.gridOptions.infiniteScroll = 20;

    $scope.search = function() {
      $log.debug("search %o",$scope.qparams);
      $scope.gridOptions.data = [];
      gokbService.getPackages($scope.gridOptions.data, null, $scope.qparams)
    }
 
    $scope.gridOptions.columnDefs = [
      {name:'Package Name', field:'name',enableColumnResizing: true },
      {name:'Global Status', field:'status',enableColumnResizing: true },
      {name:'Local Status',enableColumnResizing: true },
      {name:'Primary Platform', field:'nominalPlatform',enableColumnResizing: true },
      {name:'Primary Platform Provider', field:'provider',enableColumnResizing: true },
      {name:'# Titles', field:'numTipps',enableColumnResizing: true },
      {name:'Date Created', field:'dateCreated',enableColumnResizing: true },
      {name:'Date Updated', field:'lastUpdated',enableColumnResizing: true }
    ];

    var pageno=0;
    var total = 1000;

    var getData = function(page) {
      var page_of_data = []
      // $log.debug("Calling get packages...");
      // var result = gokbService.getPackages();
      // $log.debug("result of getPackages %o",result);
  
      for (var i = 0; i < 10; ++i) {
        page_of_data.push({'name':'Test Package['+pageno+'] '+i});
      }
      return page_of_data;
    };

    $scope.gridOptions.data = [];
    gokbService.getPackages($scope.gridOptions.data, null, $scope.qparams)
    // $scope.gridOptions.data = getData(0);

    $scope.gridOptions.onRegisterApi = function (gridApi) {
      $scope.gridApi = gridApi;

      gridApi.infiniteScroll.on.needLoadMoreData($scope,function(){

        $log.debug("gridApi.infiniteScroll.on.needLoadMoreData");

        gokbService.getPackages($scope.gridOptions.data, gridApi, $scope.qparams);
        ++pageno;
        gridApi.infiniteScroll.dataLoaded();
      });
    };
 
  }]);

  app.factory('gokbService', ['$http', '$log', function($http, $log) {
    var urlBase = 'http://localhost:8080/gokb/api';
    // var urlBase = 'https://gokb.k-int.com/gokb/api';
    var dataFactory = {};
    
    dataFactory.getPackages = function (tgt, gridApi, qparams) {
      $log.debug("getPackages tgt.length:%i",tgt.length);

      qparams.offset = tgt.length;

      // This is the config for the search
      var qconfig = {
        baseclass:'org.gokb.cred.Package',
        defaultSort:'name',
        defaultOrder:'asc',
        qbeConfig:{
          qbeForm:[
            {
              prompt:'Name of Package',
              qparam:'qp_name',
              placeholder:'Package Name',
              contextTree:{'ctxtp':'qry', 'comparator' : 'ilike', 'prop':'normname', 'wildcard':'B', normalise:true}
            }
          ],
          qbeGlobals:[
            {'ctxtp':'filter', 'prop':'status.value', 'comparator' : 'eq', 'value':'Deleted', 'negate' : true, 'prompt':'Hide Deleted',
             'qparam':'qp_showDeleted', 'default':'on'}
          ],
          qbeResults:[
            {heading:'name', property:'name'},
            {heading:'nominalPlatform', property:'nominalPlatform?.name'},
            {heading:'provider', property:'nominalPlatform?.provider?.name'},
            {heading:'status', property:'status.value'},
            {heading:'dateCreated', property:'dateCreated'},
            {heading:'lastUpdated', property:'lastUpdated'},
            {heading:'numTipps', property:'tipps.size()'},
          ],
        }
      };

      $http.post(urlBase+'/search', {cfg:qconfig}, { params : qparams } ).
        success(function(data,status,headers,config) {
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

