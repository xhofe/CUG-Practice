package com.hh.controller.GoodsController;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.hh.controller.BaseController;
import com.hh.pojo.Goods;
import com.hh.pojo.Type;
import com.hh.pojo.UserDetails;
import com.hh.service.GoodsService;
import com.hh.enums.ResponseStatus;
import com.hh.util.PathUtil;
import com.hh.util.ResultUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.lang.model.element.TypeElement;
import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/goods")
public class ReleaseGoodsController extends BaseController {
    private GoodsService goodsService;


    @Autowired
    public void setGoodsService(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @ApiOperation("获取分类列表")
    @GetMapping("/category")
    @ResponseBody
    public Object category(String typeId) {
        if(typeId == null || "".equals(typeId))
            return ResultUtil.ok(goodsService.getAllType());
        else{
            List<Type> _typeList = goodsService.getAllType();
            List<Type> typeList = new ArrayList<>();
            for(Type type:_typeList){
                if(type.getTypeId() == Integer.parseInt(typeId))
                    typeList.add(type);
            }
            return ResultUtil.ok(typeList);
        }
    }

    @PostMapping("/addGoods")
    public Object addGoods(@RequestBody Goods goods, HttpServletRequest request) {
        try {
            UserDetails userDetails = getUserDetails(request);
            if (userDetails == null) {
                return ResultUtil.fail(ResponseStatus.NO_LOGIN);
            }
            if (!userDetails.admin) {
                return ResultUtil.fail(ResponseStatus.NO_ADMIN);
            }
            int userId = userDetails.getUserId();
            goodsService.addGood(goods);
            return ResultUtil.ok(goods);
        } catch (Exception e) {
            return ResultUtil.error();
        }
    }

    @PostMapping("/updateGoods")
    public Object updateGoods(@RequestBody Goods goods, HttpServletRequest request) {
        try {
            UserDetails userDetails = getUserDetails(request);
            if (userDetails == null) {
                return ResultUtil.fail(ResponseStatus.NO_LOGIN);
            }
            if (!userDetails.admin) {
                return ResultUtil.fail(ResponseStatus.NO_ADMIN);
            }
            if (goodsService.updateGood(goods) == 1)
                return ResultUtil.ok(goods);
            else
                return ResultUtil.fail(ResponseStatus.PARAM_ERROR);

        } catch (Exception e) {
            return ResultUtil.error();
        }
    }

    @PostMapping("/deleteGoods")
    public Object deleteGoods(@RequestBody Goods goods, HttpServletRequest request) {
        try {
            UserDetails userDetails = getUserDetails(request);
            if (userDetails == null) {
                return ResultUtil.fail(ResponseStatus.NO_LOGIN);
            }
            if (!userDetails.admin) {
                return ResultUtil.fail(ResponseStatus.NO_ADMIN);
            }
            if (goodsService.deleteGood(goods.getGoodsId()) == 1)
                return ResultUtil.ok(goods);
            else
                return ResultUtil.fail(ResponseStatus.PARAM_ERROR);
        } catch (Exception e) {
            return ResultUtil.error();
        }
    }

    @PostMapping("/searchGoods")
    public Object searchGoods(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        try {
            UserDetails userDetails = getUserDetails(request);
            if (userDetails == null)
                return ResultUtil.fail(ResponseStatus.NO_LOGIN);
            int userId = userDetails.getUserId();
            Map map = new HashMap();

            JSONArray jsonArray = jsonObject.getJSONArray("keywords");
            List<String> keywords = JSONObject.parseArray(jsonArray.toJSONString(), String.class);

            map.put("keywords", keywords);
            map.put("typeId", jsonObject.getIntValue("typeId"));
            map.put("priceLow", jsonObject.getDoubleValue("priceLow"));
            map.put("priceHigh", jsonObject.getDoubleValue("priceHigh"));
            map.put("secondPriceLow", jsonObject.getDoubleValue("secondPriceLow"));
            map.put("secondPriceHigh", jsonObject.getDoubleValue("secondPriceHigh"));

            List<Goods> goods = goodsService.searchGoods(map);
            if (goods.size() != 0) {
                return ResultUtil.ok(goods);
            } else
                return ResultUtil.fail(ResponseStatus.NO_GOODS);
        } catch (Exception e) {
            return ResultUtil.error();
        }
    }

    @GetMapping("product")
    public Object product(@PathParam("type")Integer type,
                          @PathParam("limit") Integer limit,
                          @PathParam("page") Integer page,
                          @PathParam("keyword")String keyword) {
        log.info("参数为type={},limit={},page={},keyword={}",type,limit,page,keyword);
        if(limit==null)limit=12;
        if (page==null)page=1;
        Map<String, Object> res = new HashMap<>();
        List<Goods> goodsList;
        if (type!=null){
            goodsList=goodsService.getGoodsByType(type);
        }else if(keyword!=null){
            Map<String ,Object> para=new HashMap<>();
            para.put("keywords",Arrays.asList(keyword.split(" ")));
            goodsList=goodsService.searchGoods(para);
        }else {
            goodsList=goodsService.getAllGoods();
        }
        int count=goodsList.size();
        log.info("搜索到"+count+"件商品");
        res.put("count", String.valueOf(count));
        if (count>0){
            res.put("goods", Lists.partition(goodsList,limit).get(page-1));
        }else {
            res.put("goods",new ArrayList<>(0));
        }
        return ResultUtil.ok(res);
    }

    @GetMapping("goodsInfo")
    public Object goodsInfo(@RequestParam("id") int id){
        Goods goods=goodsService.getGoodsById(id);
        if (goods==null)return ResultUtil.fail(ResponseStatus.NO_GOODS);
        return ResultUtil.ok(goods);
    }


    @Value("${file.img.path}")
    private String imgFilePath;


    @PostMapping("uploadImg")
    public Object uploadImg(@RequestParam(value="file") MultipartFile file,HttpServletRequest request){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null)
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);

        System.out.println("img file path is"+imgFilePath);
        if(!file.isEmpty()){
            try {
                JSONObject res = new JSONObject();
                JSONObject resUrl = new JSONObject();
                String filename = UUID.randomUUID().toString().replaceAll("-", "");
                String ext = FilenameUtils.getExtension(file.getOriginalFilename());
                String filenames = filename + "." + ext;
                String pathname = PathUtil.getRootPath() +imgFilePath+ filenames;
                file.transferTo(new File(pathname));
                resUrl.put("src", filenames);
                res.put("msg", "");
                res.put("code", 0);
                res.put("data", resUrl);
                return res;
            }catch (IOException e){
                e.printStackTrace();
                return ResultUtil.fail(ResponseStatus.PARAM_ERROR);
            }
        }
        else{
            return ResultUtil.fail(ResponseStatus.PARAM_ERROR);
        }
    }

    @GetMapping("/recommend")
    public Object recommend(){
        try {
            List<Goods> goodsList=goodsService.getGoodsOrderByPop();
            return ResultUtil.ok(Lists.partition(goodsList,8).get(0));
        }catch (Exception e){
            log.error(e.toString());
            return ResultUtil.error();
        }
    }

    @ApiOperation("获取商品列表")
    @GetMapping("/list")
    @ResponseBody
    public Object list(String typeId,String keyword,HttpServletRequest request) {
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null)
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        Map<String,Object> res = new HashMap<>();

        List<Goods> _goodsList = goodsService.getAllGoods();
        List<Goods> goodsList = new ArrayList<>();
        if(typeId != null){
            for (Goods goods:_goodsList){
                if(goods.getTypeId() == Integer.parseInt(typeId))
                    goodsList.add(goods);
            }
        }
        else
            goodsList = _goodsList;
//        res.put("code",0);
//        res.put("msg","成功");
//        res.put("count",goodsList.size());
//        res.put("data",goodsList);
        return ResultUtil.ok(goodsList);
        //return res;
    }

}
