package com.qingcheng.controller.file;

import com.aliyun.oss.OSSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.UUID;

@RestController //SpringMVC注解，相当于 @ResponseBody + @Controller
@RequestMapping("/upload") // 跟前端相配置
public class UploadController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private OSSClient ossClient;

    /**
     * 文件上传，上传到本地
     *
     * @param file
     * @return
     */
    @PostMapping("/native")
    public String nativeUpload(@RequestParam("file") MultipartFile file) {
        //获取img目录的真实路径
        String path = request.getSession().getServletContext().getRealPath("img");

        String filePath = path + "/" + file.getOriginalFilename();
        File desFile = new File(filePath);
        if (!desFile.getParentFile().exists()) {
            desFile.mkdirs();
        }
        try {
            file.transferTo(desFile);//内存中的图片写到硬盘上
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("path:---"+filePath);
        return "http://localhost:9101/img/" + file.getOriginalFilename();
    }


    /**
     * 阿里云OSS   存储文件到云端
     *
     * @param file
     * @param folder
     * @return
     */
    @PostMapping("/oss")
    public String ossUpload(@RequestParam("file") MultipartFile file, String folder) {
        String bucketName = "qing-cheng";// bucket名称，需要与实际使用的bucket匹配
        String fileName = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            ossClient.putObject(bucketName, fileName, file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "http://" + bucketName + "."
                + ossClient.getEndpoint().toString().replace("http://", "")
                + "/" + fileName;
    }
}
