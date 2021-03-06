'use strict';

// Declare app level module which depends on filters, and services
angular.module('OleLite', [
  'ngRoute',
  'myApp.filters',
  'myApp.services',
  'myApp.directives',
  'myApp.controllers',
  'ui.bootstrap',
  'GOKb'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/view1', {templateUrl: 'partials/partial1.html', controller: 'MyCtrl1'});
  $routeProvider.when('/view2', {templateUrl: 'partials/partial2.html', controller: 'MyCtrl2'});
  $routeProvider.when('/GOKb', {templateUrl: 'partials/GOKb.html', controller: 'GOKbCtrl'});
  $routeProvider.otherwise({redirectTo: '/view1'});
}]);
