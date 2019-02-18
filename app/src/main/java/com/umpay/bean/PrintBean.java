package com.umpay.bean;


import android.support.annotation.NonNull;

import com.umpay.utils.DateUtil;

import java.io.Serializable;

/**
 * Created by xiaoyang on 2015/12/10.
 */
public class PrintBean implements Serializable {



    public static final int WX = 1;
    public static final int YHK = 2;
    public static final int ZFB = 4;


    @NonNull
    /**
     * 终端编号
     */
    public String storeNO;
    /**
     * 流水号
     */
    public String tradeNo;
    /**
     * 日期
     */
    public String date;



    public String getDate() {
        return DateUtil.getDate(date);
    }

    /**
     * 时间
     */
    public String time;

    public String getTime() {
        return DateUtil.getTime(time);
    }

    /**
     * 支付类型 WX 为微信支付，BK为银行卡支付，TK为电子券 AL为支付宝
     */
    public String type;

    public static final String WX_S = "WX";
    public static final String YHK_S = "BK";
    public static final String ZFB_S = "AL";


    public String getType() {
        if (WX_S.equals(type)) {
            return "微信支付";
        } else if (YHK_S.equals(type)) {
            return "银行卡支付";
        } else if (ZFB_S.equals(type)) {
            return "支付宝支付";
        }
        return "未知支付";
    }

    /**
     * 支付成功
     */
    public static final int SUCCESS = 1;
    /**
     * 支付失败
     */
    public static final int FAIL = 0;
    /**
     * 交易撤销
     */
    public static final int revoke=2;
    /**
     * 订单状态 0失败，1成功，2冲正（撤销）
     */
    public int state;

    public String getState() {
        return state == FAIL ? "失败" : "成功";
    }

    /**
     * 订单金额
     */
    public String orderAmount;

    /**
     * 实际金额
     */
    public String payAmount;

    /**
     * 操作员
     */
    public String operator;


    public static final String CONSUME = "消费";
    public static final String REFUND = "退款";
    /**
     * 交易类型
     */
    public String tradeType;


    public String getFailureContent() {
        return failureContent;
    }

    public void setFailureContent(String failureContent) {
        this.failureContent = failureContent;
    }

    /**
     * 失败原因
     */
    public String failureContent;
}
