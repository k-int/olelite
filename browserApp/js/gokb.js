'use strict';

(function() {
  var app = angular.module('GOKb',[ 'ui.bootstrap', 
                                    'ui.grid', 
                                    // 'ui.grid.pagination',
                                    'ui.grid.infiniteScroll',
                                    ]);

  app.run(function($http) {
    $http.defaults.headers.common.Authorization = 'Basic YWRtaW46YWRtaW4='
  });

  app.controller('GOKbCtrl', ['$scope', '$http', '$log', 'gokbService', function($scope,$http,$log,gokbService) {

    $scope.gridOptions = {};
    // $scope.gridOptions.infiniteScrollPercentage = 20;
    // $scope.gridOptions.infiniteScroll = 20;
 
    $scope.gridOptions.columnDefs = [
      {name:'Package Name', field:'name'},
      {name:'GOKb Status', field:'status'},
      {name:'OLE Status'},
      {name:'Primary Platform'},
      {name:'Primary Platform Provider'},
      {name:'# TIPPS'},
      {name:'Date Created'},
      {name:'Date Updated'}
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
    gokbService.getPackages($scope.gridOptions.data)
    // $scope.gridOptions.data = getData(0);

    $scope.gridOptions.onRegisterApi = function (gridApi) {
      $scope.gridApi = gridApi;

      gridApi.infiniteScroll.on.needLoadMoreData($scope,function(){

        $log.debug("gridApi.infiniteScroll.on.needLoadMoreData");

        gokbService.getPackages($scope.gridOptions.data);
        ++pageno;
        gridApi.infiniteScroll.dataLoaded();
      });
    };
 
  }]);

  app.factory('gokbService', ['$http', '$log', function($http, $log) {
    var urlBase = 'http://localhost:8080/gokb/api';
    var dataFactory = {};
    
    dataFactory.getPackages = function (tgt) {
      $log.debug("getPackages ");

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
            {heading:'status', property:'status.value'},
          ],
        }
      };

      $http.post(urlBase+'/search', {cfg:qconfig}).
        success(function(data,status,headers,config) {
          $log.debug("OK:: data %o",data);
          for (var i = 0; i < data.rows.length; i++) {
            $log.debug("Adding row %d : %o", i, data.rows[i]);
            tgt.push(data.rows[i]);
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

