package com.umpay.payplugindemo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.BalanceInquiryRespnse;
import com.umpay.payplugin.callback.UMBalanceInquiryCallback;
import com.umpay.payplugin.code.UMCardCode;
import com.umpay.payplugin.util.FastJsonUtils;

/**
 * 银行卡余额查询
 */
public class BalanceInquiryActivity extends BaseActivity {

    private TextView tv_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_inquiry);
        setToolBar();
        tv_info = (TextView) findViewById(R.id.info);

        findViewById(R.id.search_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
    }

    private void search() {
        //开启dialog
        progressDialog("开始搜卡");

        UMPay.getInstance().balanceInquiry(new UMBalanceInquiryCallback() {
            @Override
            public void onReBind(int code, String msg) {
                cancelDialog();
                reBind(code, msg);
            }

            @Override
            public void onSuccess(BalanceInquiryRespnse response) {
                cancelDialog();
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n余额查询成功");
                tv_info.append("\nonSuccess：" + FastJsonUtils.toJson(response));
            }

            @Override
            public void onFail(BalanceInquiryRespnse response) {
                cancelDialog();
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n查询失败");
                tv_info.append("\nonFail：" + FastJsonUtils.toJson(response));
            }

            @Override
            public void onProgressUpdate(BalanceInquiryRespnse response) {
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\nonProgressUpdate：" + FastJsonUtils.toJson(response));
                if (UMCardCode.START_DOWNLOAD_KEY == response.code) {
                    progressDialog("开始下载密钥！");
                } else if (UMCardCode.START_READ_CARD == response.code) {
                    progressDialog("密钥下载成功，开始刷卡！");
                } else if (UMCardCode.PASSWORD_KEYBOARD_OPEN == response.code) {
                    progressDialog("刷卡成功，请输入密码");
                } else if (UMCardCode.SEND_BALANCEINQUIRY_REQUEST == response.code) {
                    progressDialog("密码输入成功，发起余额查询");
                }
            }
        });
    }
}
