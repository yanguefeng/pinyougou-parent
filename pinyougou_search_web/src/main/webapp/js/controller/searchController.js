app.controller("searchController", function ($scope,$location ,searchService) {


    //定义搜索数据结构
    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'currentPage': 1,
        'pageSize': 20,
        'sort':'',
        'sortFiled':''
    };

    $scope.resultMap={totalPages:'',brandList:[]};
    //关键词搜索
    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
            // $scope.resultMap.brandList=response.brandList;
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
        $scope.firstDot=true;
        $scope.lastDot=true;
        /*
        分页展示五页的判断操作，判断起始页小于等于三和尾页页数等于尾页-2
         */
        if ($scope.resultMap.totalPages>5) {//判断总页数是否大于5
            if ($scope.searchMap.currentPage <=3){//当前页小于3
                lastPage=5;//满足条件最大页数为5
                $scope.firstDot=false;//前面没点
            }else if ($scope.searchMap.currentPage>=lastPage-2){//当前页大于等于最大页数-2
                firstPage=lastPage-4;//满足条件起始页等于当前页-4
                $scope.lastDot=false;//后面没点
            }else{//显示当前页共五页的页数
                //不在上述的条件中就前-2后+2
                firstPage=$scope.searchMap.currentPage-2;
                lastPage=$scope.searchMap.currentPage+2;

            }
        }else{
            //都没有点
            $scope.firstDot=false;
            $scope.lastDot=false;
        }

        //封装页码数组，起始要设置为上述的变量开使页变量，结束为设置的总页数变量
        for (var i = firstPage; i <=lastPage; i++) {
            $scope.pageList.push(i);
        }
    };

    //完成点击页码的查询
    $scope.queryByCurrenPage=function (currentPage) {
        if (currentPage < 1 || currentPage > $scope.resultMap.totalPages) {
            return ;
        }
        $scope.searchMap.currentPage=currentPage;
        $scope.search();
    };

    //判断是否是第一页
    $scope.isToPage=function () {
        return $scope.searchMap.currentPage==1;
    };

    //判断是否是最后一页
    $scope.isEndPage=function () {
        return $scope.searchMap.currentPage==$scope.resultMap.totalPages;
    }

    //排序查询
    $scope.searchSort=function (sortFiled, sort) {
        $scope.searchMap.sort=sort;
        $scope.searchMap.sortFile=sortFiled;
        $scope.search();
    }

    //主页传递参数接收
    $scope.loadKeyords=function () {
        $scope.searchMap.keywords=$location.search().keywords;
        $scope.search();
    };

    //判断关键子字符串是否包含品牌
    $scope.kewordsIsBrand=function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
                return true;
            }
        }
        return false;
    }
});

