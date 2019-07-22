app.controller("brandController",function ($scope,$http,$controller,brandService) {

    $controller("baseController",{$scope:$scope});

    $scope.findAll=function () {
        brandService.findAll().success(function (respones) {
            $scope.list=respones;
        })
    };


    //分页
    $scope.findPage=function (currentPage,pageSize) {
        //发送异步get请求
        brandService.findPage(currentPage,pageSize).success(function (respones) {
            //给显示信息和总记录数重新赋值
            $scope.list=respones.rows;
            $scope.paginationConf.totalItems=respones.total;
        })
    };

    //添加数据发送异步请求
    $scope.save=function () {
        //定义方法名变量
        var methodName='add';
        if ($scope.entity.id != null) {
            //如果id不等于null方法名就为修改
            methodName='update';
        }
        //发送异步请求
        brandService.save($scope.entity,methodName).success(function (respones) {
            if (respones.success){
                //成功刷新数据
                $scope.reloadList();
            } else{
                alert(respones.message);
            }
        })
    };

    //点击之后发送请求
    $scope.findOne=function (id) {
        brandService.findOne(id).success(function (respones) {
            //给entity赋值
            $scope.entity=respones;
        })
    };


    //批量删除
    $scope.dele=function () {
        //发送请求
        brandService.dele($scope.selectId).success(function (respones) {
            if (respones.success) {
                //成功刷新
                $scope.reloadList();
            }else{
                //失败提示
                alert(respones.message);
            }
            //删除操作完成之后清空数组的内容
            $scope.selectId=[];
        })
    };

    //对对象进行初始化
    $scope.searchEntity={};
    //需求查询
    $scope.search=function (currentPage,pageSize) {
        //发送异步get请求
        brandService.search(currentPage,pageSize,$scope.searchEntity).success(function (respones) {
            //给显示信息和总记录数重新赋值
            $scope.list=respones.rows;
            $scope.paginationConf.totalItems=respones.total;
        })
    }
});
