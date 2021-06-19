package com.hh.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.alipay.api.domain.OrderDetailResult;
import com.hh.enums.ResponseStatus;
import com.hh.pojo.Goods;
import com.hh.pojo.Order;
import com.hh.pojo.UserDetails;
import com.hh.service.GoodsService;
import com.hh.service.OrderService;
import com.hh.service.UserService;
import com.hh.util.ResultUtil;
import com.hh.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.readers.operation.OperationDeprecatedReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/order")
public class OrderController extends BaseController {

    private UserService userService;
    private OrderService orderService;
    private GoodsService goodsService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Autowired
    public void setGoodsService(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @GetMapping("info/{id}")
    public Object info(HttpServletRequest request, @PathVariable int id){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        Order order=orderService.getOrderById(id);
        Goods goods=goodsService.getGoodsById(order.getGoodsId());
        OrderVo orderVo=new OrderVo();
        orderVo.setId(order.getGoodsId());
        orderVo.setIcon(goods.getImgurl().split(";")[0]);
        orderVo.setName(goods.getGoodsName());
        orderVo.setPrice(order.getCost());
        orderVo.setQuantity((int) Math.round(order.getCost()/goods.getSecondPrice()));
        Map<String ,Object> map=new HashMap<>();
        List<OrderVo> orderVosList = new ArrayList<>();
        orderVosList.add(orderVo);
        map.put("detail",orderVosList);

        //timeline
        List<JSONObject> timelineList = new ArrayList<>();

        JSONObject testObject = new JSONObject();
        testObject.put("status","下单时间");
        testObject.put("time",order.getTime());
        timelineList.add(testObject);
        map.put("timeline",timelineList);//时间线


        return ResultUtil.ok(map);
    }

    @GetMapping("buy")
    public Object buy(HttpServletRequest request){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        String res=orderService.creatOrder(userService.getUserById(userDetails.getUserId()));
        if (res.length()==0){
            return ResultUtil.ok("购买成功");
        }
        return ResultUtil.ok(res.substring(0,res.length()-1)+"库存不足");
    }

    @GetMapping("buyOne/{id}")
    public Object buyOne(HttpServletRequest request, @PathVariable int id){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        int res=orderService.buyOne(userDetails.getUserId(),id);
        if (res==1){
            return ResultUtil.ok();
        }
        return ResultUtil.fail(ResponseStatus.BUY_FAIL);
    }

    @GetMapping("list")
    @ResponseBody
    public Object list(String status,HttpServletRequest request){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        List<Order> _orders;
        if (userDetails.admin){
            _orders=orderService.getAllOrders();
        }else {
            _orders=orderService.getOrdersByUserId(userDetails.getUserId());
        }
        List<Order> orders = new ArrayList<>();
        if(status != null && !status.equals("")){
            for(Order order:_orders){
                if(order.getStatus() == Integer.parseInt(status))
                    orders.add(order);
            }
        }else
            orders = _orders;

        return ResultUtil.ok(orders);
    }

    @PostMapping("cancel/{id}")
    public Object cancel(HttpServletRequest request, @PathVariable int id){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        int res= orderService.cancelOrder(id);
        if (res==1){
            return ResultUtil.ok();
        }
        return ResultUtil.error();
    }

    @PostMapping("ship/{id}")
    public Object ship(HttpServletRequest request, @PathVariable int id){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        int res= orderService.shipOrder(id);
        if (res==1){
            return ResultUtil.ok();
        }
        return ResultUtil.error();
    }

    @PostMapping("receive/{id}")
    public Object receive(HttpServletRequest request, @PathVariable int id){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        int res= orderService.receiveOrder(id);
        if (res==1){
            return ResultUtil.ok();
        }
        return ResultUtil.error();
    }

    @PostMapping("refund/{id}")
    public Object refund(HttpServletRequest request, @PathVariable int id){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        int res= orderService.refundOrder(id);
        if (res==1){
            return ResultUtil.ok();
        }
        return ResultUtil.error();
    }

    @PostMapping("update")
    public Object update(@RequestBody Order order,HttpServletRequest request){
        UserDetails userDetails = getUserDetails(request);
        if (userDetails == null) {
            return ResultUtil.fail(ResponseStatus.NO_LOGIN);
        }
        if(!userDetails.admin){
            return ResultUtil.fail(ResponseStatus.NO_ADMIN);
        }

        if(orderService.updateOrder(order) == 1)
            return ResultUtil.ok(order);
        else
            return ResultUtil.fail(ResponseStatus.PARAM_ERROR);
    }
}
