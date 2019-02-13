package com.umpay.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.umpay.bean.YHKPrintBean;
import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.PrintInfo;
import com.umpay.payplugin.callback.BasePrintCallback;
import com.umpay.payplugin.handle.PrintUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.FieldPosition;

/**
 * Created by WK on 2017/3/17.
 */

public class Print {

    private static boolean isConnected = false;
    public static Print instance = new Print();
    public static Context mContext;

    public static Print getInstance(Context context) {
        mContext = context;
        return instance;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 3:
                    Toast.makeText(mContext, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    /**
     * 打印银行卡小票
     *
     * @param printBean
     */
    public void printAuth(YHKPrintBean printBean) {

        printAuth(printBean, 1);
    }

    /**
     * 打印预授权小票
     *
     * @param printBean
     * @param type
     */
    private void printAuth(YHKPrintBean printBean, final int type) {

        String content = getAuthPrintContent(printBean, type);
        toPrintYHK(content, "");
    }


    /**
     * 打印银行卡小票
     *
     * @param printBean
     */
    public void printYHK(YHKPrintBean printBean) {

        printYHK(printBean, 1);
    }

    /**
     * 打印银行卡小票
     *
     * @param printBean
     * @param type
     */
    private void printYHK(YHKPrintBean printBean, final int type) {

        String content = getYhkPrintContent(printBean, type);
        Log.e("TAG", "CONTENT:" + content);
        toPrintYHK(content, "");
    }


    /**
     * 组装银行卡小票数据
     *
     * @param printBean
     * @param type
     * @return
     */
    public String getYhkPrintContent(YHKPrintBean printBean, int type) {

        //调取打印的方法
        final JSONObject printJson = new JSONObject();
        JSONArray printTest = new JSONArray();
        String str = "商户联";
        if (type == 2) {
            str = "用户联";
        }
        printTest.put(PrintUtils.setStringContent("注意：此小票仅供接入方参考，具体请根据自己的情况打印！", 1, 3));
        printTest.put(PrintUtils.setStringContent("惠商+电子凭证", 2, 2));
        printTest.put(PrintUtils.setStringContent("------------------------------", 1, 2));
        printTest.put(PrintUtils.setStringContent(str + "                请妥善保管", 1, 2));
        printTest.put(PrintUtils.setStringContent("商户名称：" + "骏麟科技", 1, 2));
        printTest.put(PrintUtils.setStringContent("商户编号：" + printBean.unionPayMerId, 1, 2));
        printTest.put(PrintUtils.setStringContent("门店名称：" + "测试门店", 1, 2));
        printTest.put(PrintUtils.setStringContent("终端编号：" + printBean.storeNO, 1, 2));
        printTest.put(PrintUtils.setStringContent("------------------------------", 1, 2));
        printTest.put(PrintUtils.setStringContent("收 单 行：" + printBean.acquiring, 1, 2));
        printTest.put(PrintUtils.setStringContent("发 卡 行：" + printBean.bankName, 1, 2));
        printTest.put(PrintUtils.setStringContent("卡    号：", 1, 2));
        printTest.put(PrintUtils.setStringContent(printBean.account, 1, 3));
        printTest.put(PrintUtils.setStringContent("有 效 期：" + printBean.cardExpiryDate, 1, 2));
        printTest.put(PrintUtils.setStringContent("流 水 号：" + printBean.tradeNo, 1, 2));
        printTest.put(PrintUtils.setStringContent("交易类型：" + printBean.tradeType, 1, 2));
        printTest.put(PrintUtils.setStringContent("凭 证 号：" + printBean.uPayTrace, 1, 2));
        printTest.put(PrintUtils.setStringContent("授 权 码：" + printBean.authCode, 1, 2));
        printTest.put(PrintUtils.setStringContent("批 次 号：" + printBean.batchId, 1, 2));
        printTest.put(PrintUtils.setStringContent("参 考 号：" + printBean.paySeq, 1, 2));
        printTest.put(PrintUtils.setStringContent("日    期：" + printBean.getDate(), 1, 2));
        printTest.put(PrintUtils.setStringContent("时    间：" + printBean.getTime(), 1, 2));

        if (!printBean.tradeType.equals("退款") && printBean.isPrintResult()) {
            printTest.put(PrintUtils.setStringContent("活动名称：" + printBean.marketName, 1, 2));
            printTest.put(PrintUtils.setStringContent("应付金额：" + printBean.orgAmount + "元", 1, 2));
            printTest.put(PrintUtils.setStringContent("实付金额：", 1, 2));
            printTest.put(PrintUtils.setStringContent("       " + printBean.payAmount + "元", 1, 3));
        } else {
            printTest.put(PrintUtils.setStringContent("金    额：", 1, 2));
            printTest.put(PrintUtils.setStringContent("       " + printBean.payAmount + "元", 1, 3));
        }

        printTest.put(PrintUtils.setStringContent("操 作 员：" + printBean.operator, 1, 2));
        printTest.put(PrintUtils.setStringContent("------------------------------", 1, 2));
        printTest.put(PrintUtils.setStringContent("持卡人签名：", 1, 2));
        printTest.put(PrintUtils.setfreeLine("3"));
        printTest.put(PrintUtils.setStringContent("客服电话：400-112-5881", 1, 2));

        printTest.put(PrintUtils.setStringContent("技术支持：联动优势惠商+", 1, 2));
        printTest.put(PrintUtils.setfreeLine("4"));
        try {
            printJson.put("spos", printTest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return printJson.toString();
    }


    /**
     * 组装银行卡预授权小票信息
     *
     * @param printBean
     * @param type
     * @return
     */
    public String getAuthPrintContent(YHKPrintBean printBean, int type) {

        //调取打印的方法
        final JSONObject printJson = new JSONObject();
        JSONArray printTest = new JSONArray();
        String str = "商户联";
        if (type == 2) {
            str = "用户联";
        }
        printTest.put(PrintUtils.setStringContent("注意：此小票仅供接入方参考，具体请根据自己的情况打印！", 1, 3));
        printTest.put(PrintUtils.setStringContent("惠商+电子凭证", 2, 2));
        printTest.put(PrintUtils.setStringContent("------------------------------", 1, 2));
        printTest.put(PrintUtils.setStringContent(str + "                请妥善保管", 1, 2));
        printTest.put(PrintUtils.setStringContent("商户名称：" + "骏麟科技", 1, 2));
        printTest.put(PrintUtils.setStringContent("商户编号：" + printBean.unionPayMerId, 1, 2));
        printTest.put(PrintUtils.setStringContent("门店名称：" + "测试门店", 1, 2));
        printTest.put(PrintUtils.setStringContent("终端编号：" + printBean.storeNO, 1, 2));
        printTest.put(PrintUtils.setStringContent("------------------------------", 1, 2));
        printTest.put(PrintUtils.setStringContent("收 单 行：" + printBean.acquiring, 1, 2));
        printTest.put(PrintUtils.setStringContent("发 卡 行：" + printBean.bankName, 1, 2));
        printTest.put(PrintUtils.setStringContent("卡    号：", 1, 2));
        printTest.put(PrintUtils.setStringContent(printBean.account + printBean.getSubPayType(), 1, 3));
        printTest.put(PrintUtils.setStringContent("有 效 期：" + printBean.cardExpiryDate, 1, 2));
        printTest.put(PrintUtils.setStringContent("流 水 号：" + printBean.tradeNo, 1, 2));
        printTest.put(PrintUtils.setStringContent("交易类型：" + printBean.tradeType, 1, 2));
        printTest.put(PrintUtils.setStringContent("凭 证 号：" + printBean.uPayTrace, 1, 2));
        printTest.put(PrintUtils.setStringContent("授 权 码：" + printBean.authCode, 1, 2));
        printTest.put(PrintUtils.setStringContent("批 次 号：" + printBean.batchId, 1, 2));
        printTest.put(PrintUtils.setStringContent("参 考 号：" + printBean.paySeq, 1, 2));
        printTest.put(PrintUtils.setStringContent("日    期：" + printBean.getDate(), 1, 2));
        printTest.put(PrintUtils.setStringContent("时    间：" + printBean.getTime(), 1, 2));

        printTest.put(PrintUtils.setStringContent("金    额：", 1, 2));
        printTest.put(PrintUtils.setStringContent("       " + printBean.payAmount + "元", 1, 3));

        printTest.put(PrintUtils.setStringContent("操 作 员：" + printBean.operator, 1, 2));
        printTest.put(PrintUtils.setStringContent("------------------------------", 1, 2));
        printTest.put(PrintUtils.setStringContent("持卡人签名：", 1, 2));
        printTest.put(PrintUtils.setfreeLine("3"));
        printTest.put(PrintUtils.setStringContent("客服电话：400-112-5881", 1, 2));

        printTest.put(PrintUtils.setStringContent("技术支持：联动优势惠商+", 1, 2));
        printTest.put(PrintUtils.setStringContent("授权码+卡号条码", 1, 2));
        printTest.put(PrintUtils.setfreeLine("1"));
        printTest.put(PrintUtils.setTwoDimension(
                yuanToFen(printBean.payAmount) +
                        "|" + printBean.orderDate +
                        "|" + printBean.orderId +
                        "|" + printBean.authCode +
                        "|" + printBean.account, 2, 6));
        printTest.put(PrintUtils.setfreeLine("4"));
        try {
            printJson.put("spos", printTest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return printJson.toString();
    }


    /**
     * 元转分
     *
     * @param s
     * @return
     */
    public static String yuanToFen(String s) {
        if (s == null) {
            return "0.00";
        }

        DecimalFormat df = new DecimalFormat("0.00");
        StringBuffer sb = df.format(Double.parseDouble(s),
                new StringBuffer(), new FieldPosition(0));
        int idx = sb.toString().indexOf(".");
        sb.deleteCharAt(idx);
        for (; sb.length() != 1; ) {
            if (sb.charAt(0) == '0') {
                sb.deleteCharAt(0);
            } else {
                break;
            }
        }
        return sb.toString();
    }


    String fontType = "simsun.ttc";
    int space = 4;


    /**
     * 开始打印
     *
     * @param content
     * @param imgPath
     */
    private void toPrintYHK(String content, String imgPath) {

        //注意该回调都在子线程
//        UMPay.getInstance().print(content, imgPath, fontType, space, new MyPrintListener());
        PrintInfo printInfo = new PrintInfo();
        printInfo.lineSpace = space;
        printInfo.imagePath = imgPath;
        printInfo.fontType = fontType;
        UMPay.getInstance().print(content, printInfo, new MyPrintListener());

    }


    //普通小票打印监听
    public class MyPrintListener implements BasePrintCallback {

        @Override
        public void onStart() {
            //打印开始
        }

        @Override
        public void onFinish() {

        }

        @Override
        public void onError(int i, String s) {
            Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onReBind(int code, String msg) {

        }
    }
}
