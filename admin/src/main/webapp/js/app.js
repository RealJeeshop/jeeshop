(function (){
    var app = angular.module('admin',[]);
    app.controller('AdminController', function(){
        this.product = gem;
    });

    var gem = {
        name:'Toto',
        price:'2.95 &euro;',
        description:'tata titi'
    }
})();