package com.umpay.payplugindemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.OutOrderQequest;
import com.umpay.payplugin.bean.OutOrderResponse;
import com.umpay.payplugin.callback.OutOrderCallBack;
import com.umpay.payplugin.util.FastJsonUtils;

public class GetOutOrderIdActivity extends BaseActivity {

    private EditText et_orderId;
    private Button bt_search;
    private TextView tv_content;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_out_order_id);
        et_orderId = (EditText) findViewById(R.id.et_orderid);
        bt_search = (Button) findViewById(R.id.bt_search);
        tv_content = (TextView) findViewById(R.id.tv_content);

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });


    }

    void search() {
        orderId = et_orderId.getText().toString();
        OutOrderQequest outOrderQequest = new OutOrderQequest();
        outOrderQequest.orderId = orderId;
        outOrderQequest.accessTypePre = "5";//订单POS使用类型 固定值
        progressDialog("正在查询");
        UMPay.getInstance().getOutOrderId(outOrderQequest, new OutOrderCallBack() {
            @Override
            public void onSuccess(OutOrderResponse response) {
                cancelDialog();
                tv_content.setText(FastJsonUtils.toJson(response));

            }

            @Override
            public void onFail(OutOrderResponse response) {

                cancelDialog();
                tv_content.setText(FastJsonUtils.toJson(response));

            }

            @Override
            public void onError(OutOrderResponse response) {
                cancelDialog();
                tv_content.setText(FastJsonUtils.toJson(response));
            }

            @Override
            public void onReBind(int code, String msg) {
                reBind(code, msg);
            }
        });
    }

}
