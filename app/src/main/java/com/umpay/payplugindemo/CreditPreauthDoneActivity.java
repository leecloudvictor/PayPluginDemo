package com.umpay.payplugindemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.BinderThread;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.umpay.bean.PrintBean;
import com.umpay.bean.YHKPrintBean;
import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.CreditPreauthDoneRequest;
import com.umpay.payplugin.bean.CreditPreauthDoneResponse;
import com.umpay.payplugin.callback.UMCancelCardCallback;
import com.umpay.payplugin.callback.UMCreditPreauthDoneCallBack;
import com.umpay.payplugin.code.CreditPreauthDoneCode;
import com.umpay.payplugin.code.UMCardCode;
import com.umpay.payplugin.util.FastJsonUtils;
import com.umpay.utils.StringUtil;

import java.util.TimerTask;

import static com.umpay.payplugin.code.CreditPreauthDoneCode.CREDIT_PREAUTH_DONE_SUCCESS;
import static com.umpay.payplugindemo.CardAuthActivity.AMOUNT;
import static com.umpay.payplugindemo.CardAuthActivity.AUTHCODE;
import static com.umpay.payplugindemo.CardAuthActivity.ORDERDATE;
import static com.umpay.payplugindemo.CardAuthActivity.ORDERID;
import static com.umpay.payplugindemo.CardAuthActivity.TRADENO;

/**
 * 预授权完成
 * Created by Administrator on 2017/9/8.
 */

public class CreditPreauthDoneActivity extends BaseActivity implements View.OnClickListener {
    private Button bt_cardAuthDone;
    private Button bt_select;
    private TextView tv_content;
    private ScrollView scrollView;
    /**
     * 预授权金额
     */
    private String amount;
    /**
     * 授权码
     */
    private String authCode;
    /**
     * 订单号
     */
    private String orderId;
    /**
     * 预授权返回的订单号
     */
    private String tradeNo;
    /**
     * 预授权的订单日期
     */
    private String orderDate;

    /**
     * 预授权撤销
     */
    private Button bt_rever;

    /**
     * 预授权完成的响应
     */
    private CreditPreauthDoneResponse doneResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_preauth_done);
        bt_cardAuthDone = (Button) findViewById(R.id.bt_cardAuthDone);
        tv_content = (TextView) findViewById(R.id.tv_content);
        scrollView = (ScrollView) findViewById(R.id.sc);
        bt_rever = (Button) findViewById(R.id.bt_rever);
        bt_select = (Button) findViewById(R.id.bt_select);
        bt_cardAuthDone.setOnClickListener(this);
        bt_rever.setOnClickListener(this);
        bt_select.setOnClickListener(this);
        Intent intent = getIntent();
        if (intent != null) {
            authCode = intent.getStringExtra(AUTHCODE);
            orderId = intent.getStringExtra(ORDERID);
            orderDate = intent.getStringExtra(ORDERDATE);
            amount = intent.getStringExtra(AMOUNT);
            tradeNo = intent.getStringExtra(TRADENO);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bt_cardAuthDone:
                //预授权完成
                cardAuthDone();
                break;
            case R.id.bt_rever:
                authDoneRever();
                break;
            case R.id.bt_select:
                doneSelect();
                break;
        }
    }


    //授权完成撤销
    void authDoneRever() {
        if (doneResponse != null && (CREDIT_PREAUTH_DONE_SUCCESS == doneResponse.code)) {
            Intent intent = new Intent(this, CardAuthDoneReverActivity.class);
            intent.putExtra(AUTHCODE, authCode);
            intent.putExtra(TRADENO, tradeNo);
            intent.putExtra(ORDERID, orderId);
            intent.putExtra(ORDERDATE, orderDate);
            intent.putExtra(AMOUNT, amount);
            startActivity(intent);
        } else {
            tv_content.setText("请先发起一笔预授权完成的请求");
        }

    }

    /**
     * 发起预授权完成请求
     */
    private void cardAuthDone() {
        tv_content.setText("");
        final CreditPreauthDoneRequest doneRequest = new CreditPreauthDoneRequest();
        doneRequest.orderDate = orderDate;
        doneRequest.amount = Integer.parseInt(amount);
        doneRequest.orderId = orderId;
        //授权码
        doneRequest.authCode = authCode;
        progressDialog("正在完成预授权，请稍后");

        UMPay.getInstance().creditPreauthDone(doneRequest, new UMCreditPreauthDoneCallBack() {
            @Override
            public void onReBind(int code, String msg) {
                cancelDialog();
                reBind(code, msg);
            }

            @Override
            public void onPaySuccess(CreditPreauthDoneResponse response) {

                doneResponse = response;
                cancelDialog();
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\n预授权完成成功");
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonPaySuccess：" + FastJsonUtils.toJson(response));
                print();
            }

            @Override
            public void onPayFail(CreditPreauthDoneResponse response) {
                doneResponse = response;
                cancelDialog();
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\n预授权完成失败");
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonPayFail：" + FastJsonUtils.toJson(response));
            }

            @Override
            public void onPayUnknown(CreditPreauthDoneResponse response) {
                doneResponse = response;
                cancelDialog();
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\n预授权完成状态未知，可调用预授权查询接口查询最终状态");
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonPayUnknown：" + FastJsonUtils.toJson(response));
            }

            @Override
            public void onProgressUpdate(CreditPreauthDoneResponse response) {
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonProgressUpdate：" + FastJsonUtils.toJson(response));
                Log.e("response",FastJsonUtils.toJson(response));
                if (UMCardCode.START_DOWNLOAD_KEY == response.code) {
                    progressDialog("开始下载密钥！");
                } else if (UMCardCode.START_READ_CARD == response.code) {
                    progressDialog("密钥下载成功，开始刷卡！");
                } else if (UMCardCode.PASSWORD_KEYBOARD_OPEN == response.code) {
                    progressDialog("刷卡成功，请输入密码");
                } else if (CreditPreauthDoneCode.SEND_CREDIT_PREAUTH_DONE == response.code) {

                    progressDialog("密码输入成功，发起预授权完成");
                }
            }
        });

    }

    private void print() {
        if (doneResponse != null && doneResponse.code == CREDIT_PREAUTH_DONE_SUCCESS) {
            getPrintContent(doneResponse);
        }
    }

    private void getPrintContent(CreditPreauthDoneResponse bean) {
        YHKPrintBean printBean = new YHKPrintBean();
        printBean.type = PrintBean.YHK_S;
        printBean.date = bean.orderDate;
        printBean.time = bean.platTime;
        printBean.operator = "联动优势";
        printBean.payAmount = StringUtil.convertCent2Dollar(String.valueOf(bean.amount));
        printBean.state = 1;
        printBean.storeNO = bean.unionPayPosId;
        printBean.tradeNo = bean.tradeNo;
        printBean.tradeType = PrintBean.CONSUME;
        printBean.bankName = bean.bankName;
        printBean.subPayType = bean.payType;
        printBean.account = bean.account + printBean.getSubPayType();
        printBean.unionPayMerId = bean.unionPayMerId;
        printBean.cardExpiryDate = bean.cardExpiryDate;
        printBean.uPayTrace = bean.uPayTrace;
        printBean.batchId = bean.batchId;
        printBean.paySeq = bean.paySeq;
        printBean.authCode = bean.authCode;
        com.umpay.utils.Print.getInstance(this).printYHK(printBean);

    }

    /**
     * 跳转查询页面
     */
    void doneSelect() {
        Intent intent = new Intent(this, CardAuthQueryActivity.class);
        if (doneResponse != null) {
            intent.putExtra(ORDERID, orderId);

            intent.putExtra(ORDERDATE, orderDate);
        }
        startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMPay.getInstance().stopSearchCard(new UMCancelCardCallback() {
            @Override
            public void onCancelSuccess() {

            }

            @Override
            public void onCancelFail(String error) {

            }

            @Override
            public void onReBind(int code, String msg) {

            }
        });
    }
}
