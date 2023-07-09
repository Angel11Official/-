package org.reggieapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.reggieapp.common.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.Servlet;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    //默认路径 从配置文件中拿到
    @Value("${reggie.path}")
    private String basePath;


    //文件上传
    @PostMapping("/upload")
    //注意类型MultipartFile && 参数名和前端名字一致
    public R<String> upload(MultipartFile file) throws IOException {

        //原始文件名
        String fileOriginalFilename = file.getOriginalFilename();
        //使用UUID来随机生成文件名
        String fileName = UUID.randomUUID().toString();
        //截取原始文件名中的后缀
        String last = fileOriginalFilename.substring(fileOriginalFilename.lastIndexOf('.'));
        fileName += last;

        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if(!dir.exists()){
            dir.mkdirs();
        }

        //file是一个临时文件 需要转存 否则本次请求结束后文件会消失
        //调用transferto来转存
        file.transferTo(new File(basePath+fileName));
        log.info(file.toString());
        return R.success(fileName);
    }


    //文件下载
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        //输入流 通过输入流 读取文件内容

        //读到输入流中
        FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));

        //输出流 通过输出流将文件写回浏览器 在浏览器上展示
        ServletOutputStream outputStream = response.getOutputStream();
        //设置返回类型 这里是图片
        response.setContentType("image/jpeg");
        byte[] bytes = new byte[1024];
        int len = 0;
        //输入流读到bytes数组
        while((len= fileInputStream.read(bytes))!=-1){
             outputStream.write(bytes,0,len);
             outputStream.flush();
        }
        //关闭资源
        outputStream.close();
        fileInputStream.close();
    }
}
