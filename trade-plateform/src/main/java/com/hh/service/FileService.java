package com.hh.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    /**
     * 保存单上传文件
     * @param file 文件
     * @param path 保存路径
     * @return 路径
     */
    public String SaveUploadFile(MultipartFile file,String path);

}
