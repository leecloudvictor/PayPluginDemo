package com.umpay.payplugindemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.CardAuthDoneReverRequest;
import com.umpay.payplugin.bean.CardAuthDoneReverResponse;
import com.umpay.payplugin.callback.UMCancelCardCallback;
import com.umpay.payplugin.callback.UMPreAuthDoneReverCallBack;
import com.umpay.payplugin.code.CardAuthDoneReverCode;
import com.umpay.payplugin.code.UMCardCode;
import com.umpay.payplugin.util.FastJsonUtils;

import static com.umpay.payplugindemo.CardAuthActivity.AMOUNT;
import static com.umpay.payplugindemo.CardAuthActivity.AUTHCODE;
import static com.umpay.payplugindemo.CardAuthActivity.ORDERDATE;
import static com.umpay.payplugindemo.CardAuthActivity.ORDERID;
import static com.umpay.payplugindemo.CardAuthActivity.TRADENO;

/**
 * 预授权完成撤销  撤销的时候必须有卡
 * 无卡完成的订单 不允许撤销
 */
public class CardAuthDoneReverActivity extends BaseActivity {

    private Button bt;
    private TextView tv_content;
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

    private Button bt_query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_auth_done_rever);
        bt = (Button) findViewById(R.id.bt);
        bt_query= (Button) findViewById(R.id.bt_query);
        tv_content = (TextView) findViewById(R.id.tv_content);
        Intent intent = getIntent();
        if (intent != null) {
            authCode = intent.getStringExtra(AUTHCODE);
            orderId = intent.getStringExtra(ORDERID);
            orderDate = intent.getStringExtra(ORDERDATE);
            amount = intent.getStringExtra(AMOUNT);
            tradeNo = intent.getStringExtra(TRADENO);
        }
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rever();
            }
        });

        bt_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query();
            }
        });

    }

    private void query() {

        Intent intent = new Intent(this, CardAuthQueryActivity.class);
        if (authReverResponse != null) {
            intent.putExtra(ORDERID, orderId);
            intent.putExtra(ORDERDATE,orderDate);
        }
        startActivity(intent);
    }

    CardAuthDoneReverResponse authReverResponse;

    private void rever() {
        tv_content.setText("");
        CardAuthDoneReverRequest request = new CardAuthDoneReverRequest();
        request.amount = Integer.parseInt(amount);
        request.orderId = orderId;
        request.orderDate = orderDate;
        request.tradeNo = tradeNo;
        request.authCode = authCode;


        UMPay.getInstance().cardPreAuthDoneRever(request, new UMPreAuthDoneReverCallBack() {
            @Override
            public void onReverSuccess(CardAuthDoneReverResponse reverResponse) {
                cancelDialog();
                authReverResponse = reverResponse;
                Log.e("TAG", "onReverSuccess:" + FastJsonUtils.toJson(reverResponse));

                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\n预授权完成撤销成功");
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonReverSuccess：" + FastJsonUtils.toJson(reverResponse));
            }

            @Override
            public void onReverFail(CardAuthDoneReverResponse reverResponse) {

                cancelDialog();
                authReverResponse = reverResponse;
                Log.e("TAG", "onReverFail:" + FastJsonUtils.toJson(reverResponse));
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\n预授权完成撤销失败");
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonReverFail：" + FastJsonUtils.toJson(reverResponse));
            }

            @Override
            public void onReverUnknown(CardAuthDoneReverResponse reverResponse) {

                cancelDialog();
                authReverResponse = reverResponse;
                Log.e("TAG", "onReverUnknown:" + FastJsonUtils.toJson(reverResponse));
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\n预授权完成撤销状态未知");
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonReverUnknown：" + FastJsonUtils.toJson(reverResponse));
            }

            @Override
            public void onProgressUpdate(CardAuthDoneReverResponse response) {

                authReverResponse = response;
                Log.e("TAG", "onProgressUpdate:" + FastJsonUtils.toJson(response));
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonProgressUpdate：" + FastJsonUtils.toJson(response));
                if (UMCardCode.START_DOWNLOAD_KEY == response.code) {
                    progressDialog("开始下载密钥！");
                } else if (UMCardCode.START_READ_CARD == response.code) {
                    progressDialog("密钥下载成功，开始刷卡！");
                } else if (UMCardCode.PASSWORD_KEYBOARD_OPEN == response.code) {
                    progressDialog("刷卡成功，请输入密码");
                } else if (CardAuthDoneReverCode.SEND_AUTH_DONE_REVERS_REQUEST == response.code) {
                    progressDialog("密码输入成功，发起预授权撤销");
                }
            }

            @Override
            public void onReBind(int code, String msg) {
                cancelDialog();
                reBind(code, msg);
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
