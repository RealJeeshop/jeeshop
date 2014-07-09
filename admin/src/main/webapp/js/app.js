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

    app.controller('DatepickerDemoCtrl',function ($scope) {

        $scope.today = function() {
            $scope.dt = new Date();
        };
        $scope.today();

        $scope.minDate = new Date();

        $scope.getMinDate = function(aDate){
            return aDate != null ? aDate : null;
        };

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