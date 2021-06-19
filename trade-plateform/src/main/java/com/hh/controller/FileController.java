package com.hh.controller;

import com.hh.pojo.UserDetails;
import com.hh.service.FileService;
import com.hh.util.PathUtil;
import com.hh.enums.ResponseStatus;
import com.hh.util.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
public class FileController extends BaseController {

    @Value("${file.img.path}")
    private String IMG_PATH;
    FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("upload")
    public Object upload(@RequestParam("file") MultipartFile img, HttpServletRequest request){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        String res=fileService.SaveUploadFile(img, PathUtil.getRootPath()+IMG_PATH);
        if (res==null){
            return ResultUtil.fail(ResponseStatus.IMG_UPLOAD_FAIL);
        }
        Map<String ,String > map=new HashMap<>(1);
        map.put("path","/img/"+res);
        return ResultUtil.ok(map);
    }
}
