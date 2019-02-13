package com.umpay.bean;


/**
 * Created by xiaoyang on 2016/3/29.
 */
public class YHKPrintBean extends PrintBean {

    //收单行，默认联动优势
    public String acquiring = "联动优势";
    //发卡行
    public String bankName;
    //卡号
    public String account;
    //有效期
    public String cardExpiryDate;
    //凭证号
    public String uPayTrace;
    //批次号
    public String batchId;
    //参考号
    public String paySeq;
    //商户编号,商户在银联注册的商户号
    public String unionPayMerId;
    /**
     * 营销标识
     */
    public String resultCode = "F";
    /**
     * 第三方营销活动名称
     */
    public String marketName;
    /**
     * 原始支付金额
     */
    public String orgAmount;
    /**
     * 减免金额
     */
    public String reduceAmt;
    /**
     * 授权码
     */
    public String authCode;

    /**
     * 订单号
     */
    public String orderId;

    /**
     * 订单号
     */
    public String orderDate;


    /**
     * 是否打印营销活动
     *
     * @return
     */
    public boolean isPrintResult() {
        return "T".equalsIgnoreCase(resultCode);
    }

//    CT：磁条卡
//    IC: IC卡
//    YS: 闪付

    public String subPayType = "";

    /**
     * 获取支付
     *
     * @return
     */
    public String getSubPayType() {
        if (subPayType == null || "".equals(subPayType)) {
            return "";
        }
        if (subPayType.equals("CT")) {
            return "/S";
        } else if (subPayType.equals("IC")) {
            return "/C";
        } else if (subPayType.equals("YS")) {
            return "/F";
        }
        return "";
    }

}
