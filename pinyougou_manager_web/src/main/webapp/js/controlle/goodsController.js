 //控制层 
app.controller('goodsController' ,function($scope,$controller ,itemCatService,goodsService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	} ;
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//图片的显示
				$scope.entity.goodsDesc.itemImages=JSON.parse(response.goodsDesc.itemImages);
				$scope.itemList=response.itemList;
				var title="";
                for (var i = 0; i < $scope.itemList.length; i++) {
					title += $scope.itemList[i].title+" 价格"+$scope.itemList[i].price+"元，";
                }
                $scope.titles=title;
			}
		);				
	};
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity); //修改
		}else{
			serviceObject=goodsService.add( $scope.entity);//增加
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};


    //定义一个数组申请显示状态
    $scope.auditStatusArray=['未审核','审核通过','审核未通过','已关闭'];

    //定义一个方法用于分级的展示
    $scope.itemCatList=[];
    /**
     * 此方法通过查询所有的分类信息，把查到的数据id当做索引，name当做值来构成一个数据，在页面通过索引取值显示等级信息
     */
    $scope.findItemCatList=function () {
        itemCatService.findAll().success(function (response) {
            for (var i = 0; i < response.length; i++) {
                //把查询到分级数据的id当做索引，name当做索引对应的值
                $scope.itemCatList[response[i].id]=response[i].name;
            }
        })
    };
    //初始化一个变量
    $scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[],customAttributeItems:[]}};

    //商品的审核
	$scope.updateStatus=function (status) {
		goodsService.updateStatus($scope.selectIds,status).success(function (response) {
			if (response.success) {
                alert(response.message);
				$scope.reloadList();
				$scope.selectIds=[];
			}else {
				alert(response.message);
			}
        })
    };

	//商品删除
    $scope.uplateIsDelete=function () {
		goodsService.uplateIsDelete($scope.selectIds).success(function (response) {
            if (response.success) {
                alert(response.message);
                $scope.reloadList();
                $scope.selectIds=[];
            }else {
                alert(response.message);
            }
        })
    }

});	
