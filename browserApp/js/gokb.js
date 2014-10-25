'use strict';


(function() {
  var app = angular.module('GOKb',[ 'ngRoute', 'myApp.filters', 'myApp.services', 'myApp.directives', 'myApp.controllers', 'ui.bootstrap', 'ui.grid']);

  app.controller('GOKbCtrl', function($scope,$http) {

    $scope.gridOptions = {  };
 
    $scope.gridOptions.columnDefs = [
      {name:'Package Name'},
      {name:'GOKb Status'},
      {name:'OLE Status'},
      {name:'Primary Platform'},
      {name:'Primary Platform Provider'},
      {name:'# TIPPS'},
      {name:'Date Created'},
      {name:'Date Updated'}
    ];
 
    // $http.get('/data/10000_complex.json')
    // .success(function(data) {
    //   $scope.gridOptions.data = data;
    // });

    $scope.gridOptions.data = [
      { 'name' : 'testPackage' },
      { 'name' : 'testPackage' },
      { 'name' : 'testPackage' },
      { 'name' : 'testPackage' },
      { 'name' : 'testPackage' },
      { 'name' : 'testPackage' },
      { 'name' : 'testPackage' },
      { 'name' : 'testPackage' }
    ];




  });

})();

