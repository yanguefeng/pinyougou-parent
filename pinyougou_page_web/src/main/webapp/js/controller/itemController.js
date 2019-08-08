app.controller("itemController", function ($scope,$http) {

    $scope.num = 1;
    //购物数量的添加
    $scope.addNum = function (num) {
        $scope.num += num;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    }


    //定义规格变量
    $scope.specification = {};
    //点击给规格变量赋值
    $scope.selectSpecification = function (key, value) {
        $scope.specification[key] = value;

    }
    //判断是否被点击的属性
    $scope.isSelected = function (key, value) {
        return $scope.specification[key] == value;
    }


    //定义sku列表变量
    $scope.sku = [];

    //定义方法来完成初始化的时候使用默认的规格
    $scope.loadSku = function () {
        $scope.sku = skuList[0];
        //为了不影响数值的变化使用深克隆来给对象添加属性
        $scope.specification = JSON.parse(JSON.stringify($scope.sku.spec));
    }


    //定义方法判断两个对象是否相等
    matchObject = function (map1, map2) {
        //循环遍历看是否相等
        for (var k in map1) {
            if (map1[k] != map2[k]) {
                return false;
            }
        }

        //查看两个对象的元素个数是否相等
        for (var k in map2) {
            if (map2[k] != map1[k]) {
                return false;
            }
        }

        return true;

    }

    //定义方法遍历集合，查看是否有与之匹配的对象
    $scope.searchSku = function () {

        for (var i = 0; i < skuList.length; i++) {
            //判断点击选中的规格列表和生成的是否相等
            if (matchObject(skuList[i].spec, $scope.specification)) {
                $scope.sku = skuList[i];
                return;
            }
        }
    };
    //添加到购物车
    $scope.addToCart=function () {
        $http.get('http://localhost:9107/cart/addGoodsToCartList.do?itemId='+$scope.sku.id+"&num="+$scope.num,
            {"withCredentials":true}).success(
            function (response) {
                if (response.success){
                    //成功跳转到购物车页面
                    location.href="http://localhost:9107/cart.html";
                } else{
                    alert(response.message)
                }
            }
        )
    }
});