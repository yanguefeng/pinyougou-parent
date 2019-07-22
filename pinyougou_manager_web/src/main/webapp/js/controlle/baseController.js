app.controller("baseController",function ($scope) {



    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function () {
            $scope.reloadList();//重新加载
        }
    };

    //定义方法获取当前的页数和每页显示的条数
    $scope.reloadList=function () {
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage)
    };

    //定义选中的id数组变量
    $scope.selectIds=[];
    //定义选中id获取的方法
    $scope.updateSelection=function ($event,id) {
        //判断复选框的状态
        if ($event.target.checked) {
            //被选中添加到数组中
            $scope.selectIds.push(id);
        }else{
            //取消选中移除对应的id
            var index= $scope.selectIds.indexOf(id);//获取取消选中id的索引
            $scope.selectIds.splice(index,1)//1为删除的个数
        }
    }

    $scope.jsonToString=function (jsonString,key) {
        var value="";
        var json=JSON.parse(jsonString);
        for (var i = 0; i < json.length; i++) {
            if (i>0){
                value +="，";
            }
            value +=json[i][key];
        }
        return value;
    }

});