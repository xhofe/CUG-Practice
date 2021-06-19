package com.hh.service.impl;

import com.hh.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {
    @Override
    public String SaveUploadFile(MultipartFile file, String path) {
        if (file.isEmpty())return null;
        String fileName = file.getOriginalFilename();
        if (fileName==null)return null;
        log.info("上传的文件名为：" + fileName);
        // 获取文件的后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        fileName= UUID.randomUUID()+suffixName;
        File dest=new File(path+fileName);
        //dest=new File(dest.getAbsolutePath()+fileName);
        // 检测是否存在目录
        if (!dest.getParentFile().exists()) {
            if (!dest.getParentFile().mkdirs()){
                log.error(dest.getParent()+"文件夹创建失败");
                return null;
            }
        }
        try {
            file.transferTo(dest);
            return fileName;
        } catch (IllegalStateException | IOException e) {
            log.error(e.toString());
        }
        return null;
    }
}
