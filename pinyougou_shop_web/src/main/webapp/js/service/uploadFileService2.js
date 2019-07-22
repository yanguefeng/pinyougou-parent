app.service("uploadFileService2",function ($http) {


    this.upload=function () {
        var formData=new FormData();
        formData.append("uploadFile",document.getElementById("file").files[0]);
        return $http({
            url:'../upload/uploadFile.do',
            method:'post',
            data:formData,
            headers:{ 'Content-Type':undefined },
            transformRequest: angular.identity
        });
    }
});