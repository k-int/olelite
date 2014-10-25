'use strict';


(function() {
  var app = angular.module('GOKb',[ 'ngRoute', 'myApp.filters', 'myApp.services', 'myApp.directives', 'myApp.controllers', 'ui.bootstrap', 'ui.grid']);

  app.controller('GOKbCtrl', function($scope,$http) {

    $scope.packageData = [
      { 'name' : 'testPackage' }
    ];

  });

})();

