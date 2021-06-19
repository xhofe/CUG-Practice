package com.hh.controller.GoodsController;

import com.hh.controller.BaseController;
import com.hh.enums.ResponseStatus;
import com.hh.pojo.Type;
import com.hh.pojo.UserDetails;
import com.hh.service.GoodsService;
import com.hh.util.ResultUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/goods/type/")
public class TypeController extends BaseController {
    private GoodsService goodsService;

    @Autowired
    public void setGoodsService(GoodsService goodsService){
        this.goodsService = goodsService;
    }


    @ApiOperation("添加root分类")
    @PostMapping("/addRoot")
    @ResponseBody
    public Object addRoot(@RequestBody Type type, HttpServletRequest request){
        UserDetails userDetails = getUserDetails(request);
        if(userDetails == null)
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        if(goodsService.addType(type) == 1)
            return ResultUtil.ok();
        else
            return ResultUtil.fail(ResponseStatus.PARAM_ERROR);
    }

    @ApiOperation("删除种类")
    @PostMapping("deleteType")
    @ResponseBody
    public Object deleteType(@RequestBody Type type,HttpServletRequest request){
        UserDetails userDetails = getUserDetails(request);
        if(userDetails == null)
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        if(goodsService.deleteType(type.getTypeId()) == 1){
            return ResultUtil.ok(type);
        }else{
            return ResultUtil.fail(ResponseStatus.PARAM_ERROR);
        }
    }

    @ApiOperation("修改种类")
    @PostMapping("updateType")
    @ResponseBody
    public Object updateType(@RequestBody Type type,HttpServletRequest request){
        UserDetails userDetails = getUserDetails(request);
        if(userDetails == null)
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        if(goodsService.updateType(type) == 1){
            return ResultUtil.ok(type);
        }else{
            return ResultUtil.fail(ResponseStatus.PARAM_ERROR);
        }
    }
}
