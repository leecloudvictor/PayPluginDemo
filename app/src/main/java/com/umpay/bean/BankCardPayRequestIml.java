package com.umpay.bean;


import com.umpay.payplugin.bean.BankCardPayRequest;

/**
 * 作者:tianxiaoyang
 * 时间:2017/12/4 10:38
 * 描述:
 */

public class BankCardPayRequestIml extends BankCardPayRequest {

    //代表支付流程中会先返回卡号给接入方
    public static final int INTERRUPT = 1;
    //卡号
    public String account;
    //支付模式
    public int type;
    //显示金额的，就是在密码键盘上的显示金额，不会作为支付金额
    public int displayAmount;
}
