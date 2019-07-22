app.controller("searchController",function ($scope,searchService) {



    //关键词搜索
        $scope.search=function () {
            searchService.search($scope.searchMap).success(function (response) {
                $scope.resultMap=response;
            })
        }


});

