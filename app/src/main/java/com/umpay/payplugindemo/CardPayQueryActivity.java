package com.umpay.payplugindemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.BankCardPayResponse;
import com.umpay.payplugin.bean.CardPayQueryRequest;
import com.umpay.payplugin.callback.UMCardPayQueryCallback;
import com.umpay.payplugin.util.FastJsonUtils;

/**
 * 银行卡订单状态查询
 */
public class CardPayQueryActivity extends BaseActivity {
    private EditText orderId;
    private EditText orderDate;
    private TextView tv_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_pay_query);
        setToolBar("银行卡订单状态查询");

        orderId = (EditText) findViewById(R.id.orderId);
        orderDate = (EditText) findViewById(R.id.orderDate);
        tv_info = (TextView) findViewById(R.id.info);
        Button query = (Button) findViewById(R.id.query);
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query();
            }
        });
    }

    private void query() {
        CardPayQueryRequest request = new CardPayQueryRequest();
        request.orderId = orderId.getText().toString().trim();
        //订单时间 格式yyyyMMdd （由于数据库做的分表，所以需要传这个字段，如果不传就差当月数据）
        request.orderDate = orderDate.getText().toString().trim();
        progressDialog("正在查询银行卡支付状态");
        tv_info.setText("正在查询银行卡支付状态");
        UMPay.getInstance().cardQuery(request,new UMCardPayQueryCallback(){

            @Override
            public void onReBind(int code, String msg) {
                cancelDialog();
                reBind(code, msg);
            }

            @Override
            public void onPaySuccess(BankCardPayResponse response) {
                cancelDialog();
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n支付成功，订单最终状态");
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n" + "onPaySuccess："+ FastJsonUtils.toJson(response));
            }

            @Override
            public void onPayFail(BankCardPayResponse response) {
                cancelDialog();
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n支付失败，订单最终状态");
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n" + "onPayFail："+ FastJsonUtils.toJson(response));
            }

            @Override
            public void onPaying(BankCardPayResponse response) {
                cancelDialog();
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n正在支付");
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n" + "onPaying："+ FastJsonUtils.toJson(response));
            }

            @Override
            public void onQueryError(BankCardPayResponse response) {
                cancelDialog();
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n");
                tv_info.append(SpannableUtil.getSpannableString("查询失败，还不能确定订单最终状态，可以通过错误提示是否继续调用查询接口"));
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n" + "onQueryError："+ FastJsonUtils.toJson(response));
            }
        });
    }

}
