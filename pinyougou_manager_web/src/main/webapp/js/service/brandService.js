//创建服务层
app.service("brandService",function ($http) {
    this.findAll=function () {
        return $http.get("../brand/findAll.do");
    }

    this.findPage=function (currentPage, pageSize) {
        return $http.get("../brand/findPage.do?currentPage="+currentPage+"&pageSize="+pageSize);
    }

    this.save=function (entity,methodName) {
        return $http.post("../brand/"+methodName+".do",entity);
    }

    this.findOne=function (id) {
        return $http.get('../brand/findOne.do?id='+id);
    }

    this.dele=function (selectId) {
        return $http.get('../brand/delete.do?ids='+selectId);
    }
    this.search=function (currentPage,pageSize,searchEntity) {
        return $http.post("../brand/search.do?currentPage="+currentPage+"&pageSize="+pageSize,searchEntity)
    }

    this.findBrandList=function () {
        return $http.get("../brand/selectOptionList.do");
    }
});