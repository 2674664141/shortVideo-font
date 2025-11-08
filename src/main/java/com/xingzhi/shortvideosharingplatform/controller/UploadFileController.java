package com.xingzhi.shortvideosharingplatform.controller;

import com.xingzhi.shortvideosharingplatform.common.Result;
import com.xingzhi.shortvideosharingplatform.model.Merge;
import com.xingzhi.shortvideosharingplatform.utils.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/upload")
public class UploadFileController {

    @Value("${star-vision.upload-directory}")
    private  String uploadDirectory;
    //文件上传
    @PostMapping("/chunk")
    public Result<String> uploadChunk(@RequestParam("fileHash") String file, @RequestParam("chunkHash")
    String chunkHash,@RequestParam("fileType") String fileType, @RequestParam("chunk") MultipartFile chunk){
        try{
            String uploadPath = uploadDirectory + "/" + fileType + "/" + file;
            File file1 = new File(uploadPath);

            if(!file1.exists()){
                file1.mkdir();
            }
            chunk.transferTo(new File(uploadPath + "/" + chunkHash));
            return Result.success(uploadPath);

        } catch (Exception e){
            e.printStackTrace();
            return Result.error(504, "上传失败");
        }
    }
    //文件合并
    @PostMapping("/merge")
    public Result<String> mergeChunk(@RequestBody Merge merge){
        try {
            String uploadPath = uploadDirectory + "/" + merge.getMimeType() + "/" + merge.getFileHash();
            File file = new File(uploadPath);
            if (!file.exists() || file.listFiles() == null) {
                return Result.error(401, "合并目录不存在");
            }
            List<File> fileList = Arrays.stream(Objects.requireNonNull(file.listFiles())).sorted(new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return Integer.parseInt(getFileIndex(o1.getName())) - Integer.parseInt(getFileIndex(o2.getName()));
                }

                public String getFileIndex(String fileName) {
                    return fileName.split("-")[1];
                }
            }).collect(Collectors.toList());
            File targetFile = new File(uploadDirectory + "/" + merge.getMimeType() + "/" + System.currentTimeMillis() + "." + merge.getFileType());
            FileOutputStream outputStream = new FileOutputStream(targetFile);
            for (File file1 : fileList) {
                FileInputStream inputStream = new FileInputStream(file1);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();
            }
            outputStream.close();
            FileUtils.deleteFolder(file);
            return Result.success("http://localhost:3000/" + merge.getMimeType() + "/" + targetFile.getName(), "合并文件成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(504, "合并失败，发生未知错误");
        }
    }
}