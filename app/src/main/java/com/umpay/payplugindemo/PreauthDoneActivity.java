package com.umpay.payplugindemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.umpay.bean.PrintBean;
import com.umpay.bean.YHKPrintBean;
import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.PreauthDoneRequest;
import com.umpay.payplugin.bean.PreauthDoneResponse;
import com.umpay.payplugin.callback.UMPreauthDoneCallBack;
import com.umpay.payplugin.code.PreauthDoneCode;
import com.umpay.payplugin.util.FastJsonUtils;
import com.umpay.utils.StringUtil;
import static com.umpay.payplugindemo.CardAuthActivity.ACCOUNT;
import static com.umpay.payplugindemo.CardAuthActivity.AMOUNT;
import static com.umpay.payplugindemo.CardAuthActivity.AUTHCODE;
import static com.umpay.payplugindemo.CardAuthActivity.ORDERDATE;
import static com.umpay.payplugindemo.CardAuthActivity.ORDERID;
import static com.umpay.payplugindemo.CardAuthActivity.TRADENO;

/**
 * 预授权完成无卡
 * Created by dxn on 2017/9/11.
 */

public class PreauthDoneActivity extends BaseActivity implements View.OnClickListener{
    private Button bt_AuthDone;
    private Button bt_AuthSelect;
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
     * 预授权的卡号
     */
    private String account;
    private PreauthDoneResponse  preauthDoneResponse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preauth_done);
        bt_AuthDone = (Button) findViewById(R.id.bt_AuthDone);
        bt_AuthSelect = (Button) findViewById(R.id.bt_AuthSelect);
        tv_content = (TextView) findViewById(R.id.tv_content);
        scrollView = (ScrollView) findViewById(R.id.sc);
        bt_AuthDone.setOnClickListener(this);
        bt_AuthSelect.setOnClickListener(this);

        Intent intent=getIntent();
        if(intent!=null){
            authCode=intent.getStringExtra(AUTHCODE);
            orderId=intent.getStringExtra(ORDERID);
            orderDate=intent.getStringExtra(ORDERDATE);
            amount=intent.getStringExtra(AMOUNT);
            tradeNo=intent.getStringExtra(TRADENO);
            account=intent.getStringExtra(ACCOUNT);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_AuthDone:
                cardAuthDone();
                break;
            case R.id.bt_AuthSelect:
                authSelect();
                break;
        }
    }

    private void cardAuthDone() {
        tv_content.setText("");
        final PreauthDoneRequest doneRequest = new PreauthDoneRequest();
        doneRequest.orderId = orderId;
        doneRequest.amount = Integer.parseInt(amount);
        doneRequest.account = account;
        doneRequest.authCode = authCode;
        doneRequest.orderDate = orderDate;
        progressDialog("正在完成预授权，请稍后");
        UMPay.getInstance().preauthDone(doneRequest, new UMPreauthDoneCallBack() {
            @Override
            public void onReBind(int code, String msg) {
                cancelDialog();
                reBind(code, msg);
            }

            @Override
            public void onPaySuccess(PreauthDoneResponse response) {
                cancelDialog();
                preauthDoneResponse=response;
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\n预授权完成成功");
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonPaySuccess：" + FastJsonUtils.toJson(response));
                print();
            }

            @Override
            public void onPayFail(PreauthDoneResponse response) {
                cancelDialog();
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\n预授权完成失败");
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonPayFail：" + FastJsonUtils.toJson(response));
            }

            @Override
            public void onPayUnknown(PreauthDoneResponse response) {
                cancelDialog();
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\n预授权完成状态未知，可调用预授权查询接口查询最终状态");
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonPayUnknown：" + FastJsonUtils.toJson(response));
            }

        });

    }

    private void print() {
        if (preauthDoneResponse != null && preauthDoneResponse.code == PreauthDoneCode.PREAUTH_DONE_SUCCESS) {
            getPrintContent(preauthDoneResponse);
        }
    }

    private void getPrintContent(PreauthDoneResponse bean) {
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
     * 预授权查询界面
     */
    void authSelect() {
        Intent intent = new Intent(this, CardAuthQueryActivity.class);
        if (preauthDoneResponse != null) {
            intent.putExtra(ORDERID, orderId);

            intent.putExtra(ORDERDATE, orderDate);
        }
        startActivity(intent);

    }
}
