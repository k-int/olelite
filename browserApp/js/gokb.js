'use strict';


(function() {
  var app = angular.module('GOKb',[ 'ngRoute', 'myApp.filters', 'myApp.services', 'myApp.directives', 'myApp.controllers', 'ui.bootstrap']);

  app.controller('GOKbCtrl', function($scope,$http,ngDialog,$log) {
    $scope.root={};

    $scope.init = function(oid, modelChangeFunc, notifyRecordFunc) {
    };
  });
})();

