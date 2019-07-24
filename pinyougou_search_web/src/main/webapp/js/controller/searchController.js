app.controller("searchController", function ($scope, searchService) {


    //定义搜索数据结构
    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'currentPage': 1,
        'pageSize': 20
    };

    //关键词搜索
    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
           buildPageLable();
        })
    }

    //定义方法给searchMap添加数据完成面包屑的产生
    $scope.addSearchItem = function (key, value) {
        if ('category' == key || 'brand' == key || 'price' == key) {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }

        //再添加是调用搜索方法
        $scope.search();
    }

    //面包屑条件的取消
    $scope.removeSearchItem = function (key) {
        if ('category' == key || 'brand' == key || 'price' == key) {
            $scope.searchMap[key] = "";
        } else {
            delete $scope.searchMap.spec[key];
        }
        //再删除是调用搜索方法
        $scope.search();
    };


    /**
     * 分页的完成
     *
     */
    buildPageLable = function () {
        $scope.pageList = [];//定义分页数组

        //定义起始页和最后一页变量
        var firstPage=1;
        var lastPage=$scope.resultMap.totalPages;
        //省略号的判断
        var firstDot=true;
        var lastDot=true;
        /*
        分页展示五页的判断操作，判断起始页小于等于三和尾页页数等于尾页-2
         */
        if ($scope.resultMap.totalPages>5) {//判断总页数是否大于5
            if (firstPage <=3){//当前页小于3
                lastPage=5;
                firstDot=false;//前面没点
            }else if ($scope.searchMap.currentPage>=lastPage-2){//当前页大于等于最大页数-2
                firstPage=lastPage-4;
                lastDot=false;//后面没点
            }else{//显示当前页共五页的页数
                lastPage=currentPage-2;
                firstPage=currentPage+2;

            }
        }else{
            //都没有点
            firstDot=false;
            lastDot=false;
        }

        //封装页码数组
        for (var i = 1; i < lastPage; i++) {
            $scope.pageList[i]=i;
        }
    }

    //完成点击页码的查询
    $scope.queryByCurrenPage=function (page) {
        if (page < 1 || page > $scope.resultMap.totalPages) {
            return ;
        }
        $scope.searchMap.currentPage=page;
        $scope.search();
    }

    //判断是否是第一页
    $scope.isToPage=function () {
        return $scope.searchMap.currentPage==1;
    }

    //判断是否是最后一页
    $scope.isEndPage=function () {
        return $scope.searchMap.currentPage==$scope.resultMap.totalPages;
    }
});

