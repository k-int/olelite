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
      {name:'Primary Platform', field:'nominalPlatform'},
      {name:'Primary Platform Provider', field:'provider'},
      {name:'# TIPPS', field:'numTipps'},
      {name:'Date Created', field:'dateCreated'},
      {name:'Date Updated', field:'lastUpdated'}
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
    gokbService.getPackages($scope.gridOptions.data, null)
    // $scope.gridOptions.data = getData(0);

    $scope.gridOptions.onRegisterApi = function (gridApi) {
      $scope.gridApi = gridApi;

      gridApi.infiniteScroll.on.needLoadMoreData($scope,function(){

        $log.debug("gridApi.infiniteScroll.on.needLoadMoreData");

        gokbService.getPackages($scope.gridOptions.data, gridApi);
        ++pageno;
        gridApi.infiniteScroll.dataLoaded();
      });
    };
 
  }]);

  app.factory('gokbService', ['$http', '$log', function($http, $log) {
    var urlBase = 'http://localhost:8080/gokb/api';
    var dataFactory = {};
    
    dataFactory.getPackages = function (tgt, gridApi) {
      $log.debug("getPackages tgt.length:%i",tgt.length);

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

      $http.post(urlBase+'/search', {cfg:qconfig}, { params : {offset:tgt.length} } ).
        success(function(data,status,headers,config) {
          $log.debug("OK:: data %o",data);
          for (var i = 0; i < data.rows.length; i++) {
            $log.debug("Adding row %d : %o", i, data.rows[i]);
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

