(function (){

    var app = angular.module('admin',['ui.bootstrap','admin-catalog','admin-login']);

    app.controller('SideMenuController', function(){
        this.entryId = 'overview';

        this.selectEntry = function(setId){
            this.entryId = setId;
        };

        this.isSelected = function(checkId){
            return this.entryId === checkId;
        };

    });

    /*
    app.factory('httpInterceptor', function ($q, $rootScope, $log) {

        var numLoadings = 0;

        return {
            request: function (config) {

                numLoadings++;

                // Show loader
                $rootScope.$broadcast("loader_show");
                return config || $q.when(config)

            },
            response: function (response) {

                if ((--numLoadings) === 0) {
                    // Hide loader
                    $rootScope.$broadcast("loader_hide");
                }

                return response || $q.when(response);

            },
            responseError: function (response) {

                if (!(--numLoadings)) {
                    // Hide loader
                    $rootScope.$broadcast("loader_hide");
                }

                return $q.reject(response);
            }
        };
    });

    app.config(function ($httpProvider) {
            $httpProvider.interceptors.push('httpInterceptor');
    });

    app.directive("loader", function ($rootScope) {
            return function ($scope, element, attrs) {
                $scope.$on("loader_show", function () {
                    return element.show();
                });
                return $scope.$on("loader_hide", function () {
                    return element.hide();
                });
            };
        }
    );
    */


    app.controller('DatepickerDemoCtrl',function ($scope) {

        $scope.today = function() {
            $scope.dt = new Date();
        };
        $scope.today();

        $scope.open = function($event) {
         $event.preventDefault();
         $event.stopPropagation();

         $scope.opened = true;
        };

        $scope.dateOptions = {
         startingDay: 1
        };
    });
})();