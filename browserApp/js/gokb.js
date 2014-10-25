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
      {name:'GOKb Status'},
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
      $log.debug("Calling get packages...");
      var result = gokbService.getPackages();
      $log.debug("result of getPackages %o",result);
  
      for (var i = 0; i < 10; ++i) {
        page_of_data.push({'name':'Test Package['+pageno+'] '+i});
      }
      return page_of_data;
    };

    $scope.gridOptions.data = getData(pageno);

    $scope.gridOptions.onRegisterApi = function (gridApi) {
      $scope.gridApi = gridApi;

     gridApi.infiniteScroll.on.needLoadMoreData($scope,function(){
        $scope.gridOptions.data = getData(pageno);
        ++pageno;
        gridApi.infiniteScroll.dataLoaded();

        // $http.get('/data/10000_complex.json')
        //   .success(function(data) {
        //     $scope.gridOptions.data = getData(data, page);
        //     ++page;
        //     gridApi.infiniteScroll.dataLoaded();
        //   })
        //   .error(function() {
        //     gridApi.infiniteScroll.dataLoaded();
        //   });
      });
    };
 
  }]);

  app.factory('gokbService', ['$http', '$log', function($http, $log) {
    var urlBase = 'http://localhost:8080/gokb/api';
    var dataFactory = {};

    dataFactory.getPackages = function () {
      $log.info("getPackages");
      $log.debug("getPackages");
      return $http.get(urlBase+'/search?qbe=g%3A1packages');
    };

    return dataFactory;
  }]);


})();

