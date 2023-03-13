package com.rosevii.controller;

import com.rosevii.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * @author: rosevvi
 * @date: 2023/3/7 17:36
 * @version: 1.0
 * @description:
 */
@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String filepath;

    /**
     * 图片上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){ //file必须和前端的name值相同
        //file是一个临时文件，需要转存到指定位置，否则本次请求结束后临时文件会被删除

        //获取原始文件名
        String filename = file.getOriginalFilename();
        //取原始文件名的后缀
        String suffer = filename.substring(filename.indexOf("."));
        //利用uuid生成随机名字，放置文件因为文件名重复而造成文件覆盖
        String uuid = UUID.randomUUID().toString();
        File abstractPath = new File(filepath);
        //判断传过来的文件路径是否存在不存在直接创建
        if (!abstractPath.exists()){
            abstractPath.mkdirs();
        }
        //拼接为最后的路径
        String path = filepath+"/"+uuid+suffer;
        log.info(suffer);
        log.error(path);
        try {
            //将临时文件转移到其他位置
            file.transferTo(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info(path);
        return R.success(uuid+suffer);
    }

    /**
     * 图片下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        System.out.println(filepath+name+"00000000000000000000");
        //输入流，通过输入流读取文件内容
        try {
            FileInputStream fileInputStream = new FileInputStream(filepath+"/"+name);
            //输出流，通过输出流将文件写回浏览器，在浏览器显示图片
            ServletOutputStream outputStream = response.getOutputStream();

            int len =0;
            byte[] bytes=new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }
            fileInputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info(name);
    }
}
