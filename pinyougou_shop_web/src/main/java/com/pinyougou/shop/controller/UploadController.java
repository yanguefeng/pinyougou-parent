package com.pinyougou.shop.controller;


import com.pinyougou.entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/upload")
public class UploadController {


    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;//文件服务器地址

    @RequestMapping("/uploadFile.do")
    @ResponseBody
    public Result uploadFile(MultipartFile uploadFile){
        //获取文件扩展名
        String originalFilename = uploadFile.getOriginalFilename();
        //截取最后一个点+1索引之后的东西
        String extName=originalFilename.substring(originalFilename.lastIndexOf(".")+1);
        try {
            //通过工具类获得fast客户端
            FastDFSClient fastDFSClient=new FastDFSClient("classpath:config/fdfs_client.conf");
            //执行上传处理
            String path = fastDFSClient.uploadFile(uploadFile.getBytes(), extName);
            //访问上传服务器的全地址
            String fastDfsPath=FILE_SERVER_URL+path;
            return new Result(true,fastDfsPath);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
