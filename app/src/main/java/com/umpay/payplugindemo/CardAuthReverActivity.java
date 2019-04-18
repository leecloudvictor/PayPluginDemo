package com.umpay.payplugindemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.AuthReversRequest;
import com.umpay.payplugin.bean.CardPreAuthReverResponse;
import com.umpay.payplugin.callback.UMCancelCardCallback;
import com.umpay.payplugin.callback.UMPreAuthReversCallBack;
import com.umpay.payplugin.code.CardPreAuthReversCode;
import com.umpay.payplugin.code.UMCardCode;
import com.umpay.payplugin.util.FastJsonUtils;

import static com.umpay.payplugindemo.CardAuthActivity.AMOUNT;
import static com.umpay.payplugindemo.CardAuthActivity.AUTHCODE;
import static com.umpay.payplugindemo.CardAuthActivity.ORDERDATE;
import static com.umpay.payplugindemo.CardAuthActivity.ORDERID;
import static com.umpay.payplugindemo.CardAuthActivity.TRADENO;


/**
 * 银行卡预授权撤销
 */
public class CardAuthReverActivity extends BaseActivity {

    String amount;
    String orderId;
    //预授权日期
    String orderDate;
    String tradeNo;
    String authCode;

    CardPreAuthReverResponse authReverResponse;
    private TextView  tv_content;
    private Button bt_query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_auth_rever);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            amount = bundle.getString(AMOUNT);
            orderId = bundle.getString(ORDERID);
            orderDate=bundle.getString(ORDERDATE);
            tradeNo=bundle.getString(TRADENO);
            authCode=bundle.getString(AUTHCODE);
        }
        findViewById(R.id.bt_query).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authQuery();
            }
        });
        findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardAuthRever();
            }
        });
        tv_content= (TextView) findViewById(R.id.tv_content);
    }

    /**
     * 预授权查询界面
     */
    void authQuery(){
        Intent intent = new Intent(this, CardAuthQueryActivity.class);
        intent.putExtra(AUTHCODE,authCode);
        intent.putExtra(TRADENO,tradeNo);
        intent.putExtra(ORDERID,orderId);
        intent.putExtra(ORDERDATE,orderDate);
        intent.putExtra(AMOUNT,amount);
        startActivity(intent);

    }

    /**
     * 预授权撤销
     */
    private void cardAuthRever() {

        tv_content.setText("");
        AuthReversRequest request = new AuthReversRequest();
        request.amount = Integer.parseInt(amount);
        request.orderId = orderId;
        request.orderDate=orderDate;
        request.tradeNo=tradeNo;
        request.authCode=authCode;
        UMPay.getInstance().cardPreAuthRever(request, new UMPreAuthReversCallBack() {
            @Override
            public void onReBind(int code, String msg) {
                //重新绑定
                cancelDialog();
                reBind(code, msg);

            }

            @Override
            public void onReverSuccess(CardPreAuthReverResponse reverResponse) {
                cancelDialog();
                authReverResponse = reverResponse;
                Log.e("TAG","onReverSuccess:"+ FastJsonUtils.toJson(reverResponse));

                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\n预授权撤销成功");
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonReverSuccess：" + FastJsonUtils.toJson(reverResponse));
            }

            @Override
            public void onReverFail(CardPreAuthReverResponse reverResponse) {
                cancelDialog();
                authReverResponse = reverResponse;
                Log.e("TAG","onReverFail:"+ FastJsonUtils.toJson(reverResponse));
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\n预授权撤销失败");
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonReverFail：" + FastJsonUtils.toJson(reverResponse));
            }

            @Override
            public void onReverUnknown(CardPreAuthReverResponse reverResponse) {
                cancelDialog();
                authReverResponse = reverResponse;
                Log.e("TAG","onReverUnknown:"+ FastJsonUtils.toJson(reverResponse));
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\n预授权撤销状态未知");
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonReverUnknown：" + FastJsonUtils.toJson(reverResponse));
            }

            @Override
            public void onProgressUpdate(CardPreAuthReverResponse response) {
                authReverResponse = response;
                Log.e("TAG","onProgressUpdate:"+ FastJsonUtils.toJson(response));
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonProgressUpdate：" + FastJsonUtils.toJson(response));
                if (UMCardCode.START_DOWNLOAD_KEY == response.code) {
                    progressDialog("开始下载密钥！");
                } else if (UMCardCode.START_READ_CARD == response.code) {
                    progressDialog("密钥下载成功，开始刷卡！");
                } else if (UMCardCode.PASSWORD_KEYBOARD_OPEN == response.code) {
                    progressDialog("刷卡成功，请输入密码");
                } else if ((CardPreAuthReversCode.SEND_AUTH_REVERS_REQUEST == response.code) ) {
                    progressDialog("密码输入成功，发起预授权撤销");
                }
            }
        });
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
