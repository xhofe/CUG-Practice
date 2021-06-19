package com.hh.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.hh.config.AliPayConfig;
import com.hh.enums.OrderStatus;
import com.hh.enums.ResponseStatus;
import com.hh.mapper.OrderMapper;
import com.hh.pojo.Order;
import com.hh.service.AliPayService;
import com.hh.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AliPayServiceImpl implements AliPayService {

    private AliPayConfig alipayConfig;
    private AlipayClient alipayClient;
    private OrderMapper orderMapper;

    @Autowired
    public void setOrderMapper(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Autowired
    public void setAlipayClient(AlipayClient alipayClient) {
        this.alipayClient = alipayClient;
    }

    @Autowired
    public void setAlipayConfig(AliPayConfig alipayConfig) {
        this.alipayConfig = alipayConfig;
    }

    @Override
    public String createOrder(String orderNo, double amount, String body) throws AlipayApiException {
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setSubject(body);
        model.setOutTradeNo(orderNo);
        model.setTotalAmount(String.valueOf(amount));
        model.setProductCode("FAST_INSTANT_TRADE_PAY");//QUICK_WAP_PAY
//        model.setPassbackParams("公用回传参数，如果请求时传递了该参数，则返回给商户时会回传该参数");

        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradePagePayRequest ali_request = new AlipayTradePagePayRequest();
        ali_request.setBizModel(model);
//        ali_request.setNotifyUrl(alipayConfig.getNotifyUrl());// 回调地址
        ali_request.setNotifyUrl(alipayConfig.getNotifyUrl());// 异步通知
        ali_request.setReturnUrl(alipayConfig.getReturnUrl());
        AlipayTradePagePayResponse ali_response = alipayClient.pageExecute(ali_request);//"GET"直接获取URL
//        AlipayTradeAppPayResponse ali_response = alipayClient.pageExecute(ali_request,"GET");
        //就是orderString 可以直接给客户端请求，无需再做处理。
        return ali_response.getBody();
    }

    @Override
    public boolean notify(String tradeStatus, String orderNo, String tradeNo) {
        if ("TRADE_FINISHED".equals(tradeStatus) || "TRADE_SUCCESS".equals(tradeStatus)) {
            // 支付成功，根据业务逻辑修改相应数据的状态
//             boolean state = orderPaymentService.updatePaymentState(orderNo, tradeNo);
            Order order=orderMapper.getOrderById(Integer.parseInt(orderNo));
            if (order==null)return false;
            order.setStatus(OrderStatus.TO_BE_SHIPPED.getCode());
            int res=orderMapper.updateOrder(order);
            //state
            return res == 1;
        }
        return false;
    }

    @Override
    public boolean rsaCheckV1(HttpServletRequest request){
        try {
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }

            boolean verifyResult = AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipayPublicKey(), alipayConfig.getCharset(), alipayConfig.getSignType());
            return verifyResult;
        } catch (AlipayApiException e) {
            log.debug("verify sign in error, exception is:", e);
            return false;
        }
    }

    @Override
    public ResultUtil refund(String orderNo, double amount, String refundReason) {
        if(StringUtils.isBlank(orderNo)){
            return ResultUtil.fail(ResponseStatus.BLANK_ORDER_NO);
        }
        if(amount <= 0){
            return ResultUtil.fail(ResponseStatus.AMOUNT_LESS_THAN_0);
        }

        AlipayTradeRefundModel model=new AlipayTradeRefundModel();
        // 商户订单号
        model.setOutTradeNo(orderNo);
        // 退款金额
        model.setRefundAmount(String.valueOf(amount));
        // 退款原因
        model.setRefundReason(refundReason);
        // 退款订单号(同一个订单可以分多次部分退款，当分多次时必传)
        // model.setOutRequestNo(UUID.randomUUID().toString());
        AlipayTradeRefundRequest alipayRequest = new AlipayTradeRefundRequest();
        alipayRequest.setBizModel(model);
        AlipayTradeRefundResponse alipayResponse = null;
        try {
            alipayResponse = alipayClient.execute(alipayRequest);
        } catch (AlipayApiException e) {
            log.error("订单退款失败，异常原因:", e);
        }
        if(alipayResponse != null){
            String code = alipayResponse.getCode();
            String subCode = alipayResponse.getSubCode();
            String subMsg = alipayResponse.getSubMsg();
            if("10000".equals(code)
                    && StringUtils.isBlank(subCode)
                    && StringUtils.isBlank(subMsg)){
                // 表示退款申请接受成功，结果通过退款查询接口查询
                // 修改用户订单状态为退款
                return ResultUtil.ok("订单退款成功");
            }
            return ResultUtil.fail(Integer.parseInt(subCode),subMsg);
        }
        return ResultUtil.fail(ResponseStatus.REFUND_FAIL);
    }
}
