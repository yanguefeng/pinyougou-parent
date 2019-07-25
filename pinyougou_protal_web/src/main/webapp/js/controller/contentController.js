app.controller("contentContrller",function ($scope,contenService) {
    
    
    //根据
    $scope.findByCategoryId=function (categroyId) {

        //定义广告集合
        $scope.contentList=[];
        contenService.findByCategoryId(categroyId).success(function (response) {
            //给数据索引位置添加集合
            $scope.contentList[categroyId]=response;
        });
    };
    $scope.search=function () {
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
});