(function (){
    var app = angular.module('admin',['admin-catalog']);

    app.controller('AdminController', function(){
        this.product = gem;
    });

    app.controller('RowController', function(){
        this.rowId = 'overview';

        this.selectRow = function(setId){
            this.rowId = setId;
        };

        this.isSelected = function(checkId){
            return this.rowId === checkId;
        };
    });

    var gem = {
        name:'Toto',
        price:'2.95 &euro;',
        description:'tata titi'
    }
})();