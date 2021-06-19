package com.hh.controller;


import com.hh.enums.ResponseStatus;
import com.hh.pojo.Goods;
import com.hh.pojo.Order;
import com.hh.service.AliPayService;
import com.hh.service.GoodsService;
import com.hh.service.OrderService;
import com.hh.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Api(tags = "支付宝支付接口管理")
@Slf4j
@RestController
@RequestMapping("/api/alipay")
public class AliPayController extends BaseController {

    private AliPayService aliPayService;
    private OrderService orderService;
    private GoodsService goodsService;

    @Autowired
    public void setAliPayService(AliPayService aliPayService) {
        this.aliPayService = aliPayService;
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Autowired
    public void setGoodsService(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    /**
     * 创建订单
     */
    @ApiOperation(value = "创建订单", notes = "支付宝支付创建订单")
    @PostMapping("/pay/{orderId}")
    public Object createOrder(@PathVariable int orderId
//                                 @ApiParam(value = "订单号") @RequestParam int orderNo,
//                                 @ApiParam(value = "订单金额") @RequestParam double amount,
//                                 @ApiParam(value = "商品名称") @RequestParam String body
                                  ) {
        try {
            // 1、验证订单是否存在
            Order order=orderService.getOrderById(orderId);
            double amount=order.getCost();
            String body=goodsService.getGoodsById(order.getGoodsId()).getGoodsName();
            // 2、创建支付宝订单
            String orderStr = aliPayService.createOrder(String.valueOf(orderId), amount, body);
//            return ResultUtil.ok(orderStr);
//            log.info(orderStr);
            return orderStr;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResultUtil.fail(ResponseStatus.FAILED_GENERATE_ORDER);
        }
    }

    /**
     * 支付异步通知
     * 接收到异步通知并验签通过后，一定要检查通知内容，
     * 包括通知中的app_id、out_trade_no、total_amount是否与请求中的一致，并根据trade_status进行后续业务处理。
     * https://docs.open.alipay.com/194/103296
     */
    @RequestMapping("/notify")
    public String notify(HttpServletRequest request) {
        // 验证签名
        boolean flag = aliPayService.rsaCheckV1(request);
        if (flag) {
            String tradeStatus = request.getParameter("trade_status"); // 交易状态
            String outTradeNo = request.getParameter("out_trade_no"); // 商户订单号
            String tradeNo = request.getParameter("trade_no"); // 支付宝订单号
            /**
             * 还可以从request中获取更多有用的参数，自己尝试
             */
            boolean notify = aliPayService.notify(tradeStatus, outTradeNo, tradeNo);
            if(notify){
                log.info(outTradeNo+"订单支付成功");
                return "success";
            }
            log.info(outTradeNo+"订单支付失败");
        }
        return "fail";
    }

    @ApiOperation(value = "退款", notes = "退款")
    @PostMapping("/refund")
    public ResultUtil refund(@ApiParam(value = "订单号") @RequestParam String orderNo,
                             @ApiParam(value = "退款金额") @RequestParam double amount,
                             @ApiParam(value = "退款原因") @RequestParam(required = false) String refundReason) {
        return aliPayService.refund(orderNo, amount, refundReason);
    }
}
