package com.hh.enums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum ResponseStatus {

    /**
     * 请求成功
     * */
    OK(200,"SUCCESS"),

    /**
     * 服务器异常
     * */
    ERROR(500,"未知异常，请联系管理员！"),

    /**
     * 参数错误
     * */
    PARAM_ERROR(400,"非法参数！"),

    /**
     * 拒绝访问
     * */
    FORBIDDEN(403,"拒绝访问！"),

    /**
     * 商品相关错误
     */
    NO_GOODS(600,"没有s商品"),
    HAS_COLLECT(601,"已经收藏过该商品了"),
    BUY_FAIL(602,"购买失败"),
    /**
     * 用户相关错误
     * */
    NO_LOGIN(1001, "未登录或登陆失效！"),
    VEL_CODE_ERROR(1002, "验证码错误！"),
    USER_EMAIL_EXIST(1003,"该邮箱已注册！"),
    USERNAME_PASS_ERROR(1004,"用户名或密码错误！"),
    TWO_PASSWORD_DIFF(1005, "两次输入的新密码不匹配!"),
    OLD_PASSWORD_ERROR(1006,  "旧密码不匹配!"),
    NO_CODE(1007,"先获取验证码!"),

    /**
     * 评论相关错误
     * */
    HAS_COMMENTS(3001, "已评价过该商品！"),
    NO_BUY(3002,"没有购买该商品"),

    /**
     * 文件错误
     */
    IMG_UPLOAD_FAIL(4001,"图片上传失败"),

    /**
     * 权限相关错误
     */
    NO_ADMIN(5001,"需要管理员权限"),

    /**
     * 支付相关错误
     */
    BLANK_ORDER_NO(6001,"订单编号不能为空"),
    AMOUNT_LESS_THAN_0(6002,"退款金额必须大于0"),
    REFUND_FAIL(6003,"订单退款失败"),
    FAILED_GENERATE_ORDER(6004,"订单生成失败"),

    /**
     * 其他通用错误
     * */
    PASSWORD_ERROR(88001,"密码错误！");

    private int code;
    private String msg;


}
