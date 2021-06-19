package com.hh.controller.GoodsController;

import com.hh.controller.BaseController;
import com.hh.pojo.Collect;
import com.hh.pojo.Goods;
import com.hh.pojo.UserDetails;
import com.hh.service.CollectService;
import com.hh.enums.ResponseStatus;
import com.hh.service.GoodsService;
import com.hh.util.ResultUtil;
import com.hh.vo.CollectVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/goods/collect")
public class CollectController extends BaseController {

    private CollectService collectService;
    private GoodsService goodsService;

    @Autowired
    public void setGoodsService(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @Autowired
    public void setService(CollectService service) {
        this.collectService = service;
    }

    @GetMapping("delete/{ids}")
    public Object delete(HttpServletRequest request, @PathVariable List<Integer> ids){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        for (Integer id : ids) {
            collectService.deleteCollect(id);
        }
        return ResultUtil.ok();
    }

    @PostMapping("count")
    public Object count(@RequestBody CollectVo collectVo,HttpServletRequest request){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        int res=collectService.updateCount(collectVo);
        if (res!=0)return ResultUtil.ok();
        return ResultUtil.error();
    }

    @GetMapping("check/{id}")
    public Object check(@PathVariable("id") int id,
                        @RequestParam("checked") Boolean checked,
                        HttpServletRequest request){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        int res;
        if (0==id){
            res=collectService.updateAllCheck(userDetails.getUserId(),checked);
        }else {
            res=collectService.updateOneCheck(id, checked);
        }
        return ResultUtil.ok(res);
    }

    @GetMapping("list")
    public Object list(HttpServletRequest request){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        List<Collect> collects=collectService.getCollectsByUserId(userDetails.getUserId());
        List<CollectVo> collectVos=collects.stream().map(
                collect -> {
                    Goods goods=goodsService.getGoodsById(collect.getGoodsId());
                    CollectVo collectVo=new CollectVo();
                    collectVo.setId(collect.getCollectId());
                    collectVo.setName(goods.getGoodsName());
                    collectVo.setPrice(goods.getSecondPrice());
                    collectVo.setQuantity(collect.getCount());
                    collectVo.setChecked(collect.getChecked()==1);
                    collectVo.setIcon(goods.getImgurl().split(";")[0]);
                    return collectVo;
                }
        ).collect(Collectors.toList());
        return ResultUtil.ok(collectVos);
    }

    @GetMapping("has")
    public Object hasCollect(@RequestParam("goodsId")int goodsId, HttpServletRequest request){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        Collect collect=new Collect();
        collect.setGoodsId(goodsId);
        collect.setUserId(userDetails.getUserId());
        collect=collectService.hasCollect(collect);
        boolean has=collect!=null;
        Map<String ,Object> map=new HashMap<>();
        map.put("has",has);
        if (has){
            map.put("collectId",collect.getCollectId());
        }
        return ResultUtil.ok(map);
    }

    @GetMapping("add")
    public Object addCollect(@RequestParam("goodsId")int goodsId,HttpServletRequest request){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        Collect collect=new Collect();
        collect.setUserId(userDetails.getUserId());
        collect.setGoodsId(goodsId);
        Collect collect1=collectService.hasCollect(collect);
        if (collect1!=null){
            return ResultUtil.fail(ResponseStatus.HAS_COLLECT);
        }
        int res=collectService.addCollect(collect);
        collect=collectService.hasCollect(collect);
        if (res==1){
            return ResultUtil.ok(collect.getCollectId());
        }else {
            return ResultUtil.error();
        }
    }

    @GetMapping("cancel")
    public Object cancelCollect(@RequestParam("collectId")int collectId,HttpServletRequest request){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        int res=collectService.deleteCollect(collectId);
        if (res==1){
            return ResultUtil.ok();
        }else {
            return ResultUtil.error();
        }
    }
}
