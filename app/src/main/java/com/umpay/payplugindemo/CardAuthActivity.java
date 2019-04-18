package com.umpay.payplugindemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.umpay.bean.PrintBean;
import com.umpay.bean.YHKPrintBean;
import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.AuthRequest;
import com.umpay.payplugin.bean.CardAuthResponse;
import com.umpay.payplugin.callback.UMAuthPayCallBack;
import com.umpay.payplugin.callback.UMCancelCardCallback;
import com.umpay.payplugin.code.CardPreAuthCode;
import com.umpay.payplugin.code.UMCardCode;
import com.umpay.payplugin.util.FastJsonUtils;
import com.umpay.utils.Print;
import com.umpay.utils.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 银行卡预授权
 */
public class CardAuthActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 预授权
     */
    private Button bt_cardAuth;
    /**
     * 预授权撤销
     */
    private Button bt_card_rever;
    /**
     * 预授权完成
     */
    private Button bt_card_done;
    /**
     * 预授权查询
     */
    private Button bt_auth_select;
    /**
     * 打印小票
     */
    private Button bt_print;
    /**
     * 预授权无卡完成
     */
    private Button bt_card_done_nocard;
    private TextView tv_content;
    private ScrollView scrollView;
    CardAuthResponse preAuthResponse;
    /**
     * 授权码标识
     */
    public static final String AUTHCODE = "authCode";
    /**
     * 授权金额
     */
    public static final String AMOUNT = "amount";
    /**
     * 预授权订单号
     */
    public static final String TRADENO = "tradeNo";
    /**
     * 预授权订单号
     */
    public static final String ORDERID = "orderId";
    /**
     * 订单日期
     */
    public static final String ORDERDATE = "orderDate";
    /**
     * 预授权卡号
     */
    public static final String ACCOUNT = "account";
    private String selectId;
    private EditText et_amount;
    private EditText et_orderid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_auth);
        bt_cardAuth = (Button) findViewById(R.id.bt_card_auth);
        bt_card_rever = (Button) findViewById(R.id.bt_card_rever);
        bt_card_done = (Button) findViewById(R.id.bt_card_done);
        bt_card_done_nocard = (Button) findViewById(R.id.bt_card_done_nocard);
        bt_auth_select = (Button) findViewById(R.id.bt_auth_select);
        bt_print = (Button) findViewById(R.id.bt_print);
        tv_content = (TextView) findViewById(R.id.tv_content);
        scrollView = (ScrollView) findViewById(R.id.sc);
        et_amount = (EditText) findViewById(R.id.et_amount);
        bt_cardAuth.setOnClickListener(this);
        bt_card_rever.setOnClickListener(this);
        bt_card_done.setOnClickListener(this);
        bt_card_done_nocard.setOnClickListener(this);
        bt_auth_select.setOnClickListener(this);
        bt_print.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bt_card_auth:
                //预授权
                cardAuth();
                break;
            case R.id.bt_card_rever:
                reverActivity();
                break;
            case R.id.bt_card_done:
                //预授权完成
                authDone();
                break;
            case R.id.bt_card_done_nocard:
                //预授权无卡完成
                authDoneActivity();
                break;
            case R.id.bt_auth_select:
                authSelect();
                break;
            case R.id.bt_print:
                print();
                break;
        }
    }

    private void print() {

        if (preAuthResponse != null && preAuthResponse.code == CardPreAuthCode.CARD_AUTH_SUCCESS) {

            getPrintContent(preAuthResponse);

        } else {
            Toast.makeText(context, "请先完成一笔预授权交易", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 打印预授权小票
     *
     * @param bean
     */
    private void getPrintContent(CardAuthResponse bean) {
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
        printBean.account = bean.account;
        printBean.unionPayMerId = bean.unionPayMerId;
        printBean.cardExpiryDate = bean.cardExpiryDate;
        printBean.uPayTrace = bean.uPayTrace;
        printBean.batchId = bean.batchId;
        printBean.paySeq = bean.paySeq;
        printBean.authCode = bean.authCode;
        printBean.orderId = bean.orderId;
        printBean.orderDate = bean.orderDate;
        Print.getInstance(this).printAuth(printBean);


    }


    /**
     * 预授权完成 有卡
     */
    void authDone() {
        if(preAuthResponse != null && preAuthResponse.code == CardPreAuthCode.CARD_AUTH_SUCCESS){
        Intent intent = new Intent(this, CreditPreauthDoneActivity.class);
        intent.putExtra(AUTHCODE, preAuthResponse.authCode);
        intent.putExtra(TRADENO, preAuthResponse.tradeNo);
        intent.putExtra(ORDERID, preAuthResponse.orderId);
        intent.putExtra(ORDERDATE, preAuthResponse.orderDate);
        intent.putExtra(AMOUNT,String .valueOf(preAuthResponse.amount));
        startActivity(intent);
        }else{
            Toast.makeText(CardAuthActivity.this, "请先完成预授权", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 预授权完成  无卡
     */
    public void authDoneActivity() {
        if (preAuthResponse != null && preAuthResponse.code == CardPreAuthCode.CARD_AUTH_SUCCESS) {
            Intent intent = new Intent(this, PreauthDoneActivity.class);
            intent.putExtra(AUTHCODE, preAuthResponse.authCode);
            intent.putExtra(TRADENO, preAuthResponse.tradeNo);
            intent.putExtra(ORDERID, preAuthResponse.orderId);
            intent.putExtra(ORDERDATE, preAuthResponse.orderDate);
            intent.putExtra(AMOUNT, String.valueOf(preAuthResponse.amount));
            intent.putExtra(ACCOUNT, preAuthResponse.account);
            startActivity(intent);
        } else {
            Toast.makeText(CardAuthActivity.this, "请先完成预授权", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 预授权查询界面
     */
    void authSelect() {
        Intent intent = new Intent(this, CardAuthQueryActivity.class);
        if (preAuthResponse != null) {
            intent.putExtra(ORDERID, selectId);

            intent.putExtra(ORDERDATE, preAuthResponse.orderDate);
        }
        startActivity(intent);

    }


    /**
     * 跳转到预授权撤销
     */
    void reverActivity() {

        if (preAuthResponse != null && preAuthResponse.code == CardPreAuthCode.CARD_AUTH_SUCCESS) {
            Intent intent = new Intent(CardAuthActivity.this, CardAuthReverActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(AMOUNT, String.valueOf(preAuthResponse.amount));
            bundle.putString(ORDERID, preAuthResponse.orderId);
            bundle.putString(AUTHCODE, preAuthResponse.authCode);
            bundle.putString(ORDERDATE, preAuthResponse.orderDate);
            bundle.putString(TRADENO, preAuthResponse.tradeNo);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Toast.makeText(CardAuthActivity.this, "请先完成预授权", Toast.LENGTH_SHORT).show();
        }

    }

    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyMMddHHmmssSSS");

    /**
     * 预授权接口
     */
    private void cardAuth() {
        tv_content.setText("");
        String amount = et_amount.getText().toString();
        if (StringUtil.isEmpty(amount)) {
            Toast.makeText(this, "请输入预授权金额", Toast.LENGTH_SHORT).show();
            return;
        }

        final AuthRequest authRequest = new AuthRequest();
        authRequest.orderId = "0101" + dateTimeFormat.format(new Date());
        selectId = authRequest.orderId;
        //剔除金额前面的0
        int am;
        try{
             am = Integer.parseInt(amount);
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "请输入大于等于1的数字", Toast.LENGTH_SHORT).show();
            return;
        }
        authRequest.amount = am ;
        UMPay.getInstance().cardPreAuth(authRequest, new UMAuthPayCallBack() {
            @Override
            public void onReBind(int code, String msg) {
                cancelDialog();
                reBind(code, msg);
            }

            @Override
            public void onAuthSuccess(CardAuthResponse authResponse) {
                cancelDialog();
                preAuthResponse = authResponse;
                Log.e("TAG", "authsuccess:" + FastJsonUtils.toJson(authResponse));

                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\n预授权成功");
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonAuthSuccess：" + FastJsonUtils.toJson(authResponse));
            }

            @Override
            public void onAuthFail(CardAuthResponse authResponse) {
                cancelDialog();
                preAuthResponse = authResponse;
                Log.e("TAG", "onAuthFail:" + FastJsonUtils.toJson(authResponse));


                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\n预授权失败");
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonAuthFail：" + FastJsonUtils.toJson(authResponse));

            }

            @Override
            public void onAuthUnKnown(CardAuthResponse authResponse) {
                cancelDialog();
                preAuthResponse = authResponse;
                Log.e("TAG", "onAuthUnKnown:" + FastJsonUtils.toJson(authResponse));

                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\n预授权状态未知");
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonAuthUnKnown：" + FastJsonUtils.toJson(authResponse));
            }

            @Override
            public void onProgressUpdate(CardAuthResponse authResponse) {
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonProgressUpdate：" + FastJsonUtils.toJson(authResponse));
                if (UMCardCode.START_DOWNLOAD_KEY == authResponse.code) {
                    progressDialog("开始下载密钥！");
                } else if (UMCardCode.START_READ_CARD == authResponse.code) {
                    progressDialog("密钥下载成功，开始刷卡！");
                } else if (UMCardCode.PASSWORD_KEYBOARD_OPEN == authResponse.code) {
                    progressDialog("刷卡成功，请输入密码");
                } else if (CardPreAuthCode.SEND_AUTH_REQUEST == authResponse.code) {
                    progressDialog("密码输入成功，发起预授权");
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
