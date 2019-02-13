package com.umpay.payplugindemo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.RefundQueryRequest;
import com.umpay.payplugin.bean.RefundResponse;
import com.umpay.payplugin.callback.UMRefundStateQueryCallback;
import com.umpay.payplugin.util.FastJsonUtils;

/**
 * 退款状态查询
 */
public class RefundStateQueryActivity extends BaseActivity {
    private EditText orderId;
    private EditText orderDate;
    private TextView tv_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_state_query);
        setToolBar("退款状态查询");


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
        RefundQueryRequest request = new RefundQueryRequest();
        request.orderId = orderId.getText().toString().trim();
        //订单时间 格式yyyyMMdd （由于数据库做的分表，所以需要传这个字段，如果不传就差当月数据）
        request.orderDate = orderDate.getText().toString().trim();
        progressDialog("正在查询退款状态");
        tv_info.setText("正在查询退款状态");
        UMPay.getInstance().refundQuery(request, new UMRefundStateQueryCallback() {
            @Override
            public void onReBind(int code, String msg) {
                cancelDialog();
                reBind(code, msg);
            }

            @Override
            public void onRefundSuccess(RefundResponse response) {
                cancelDialog();
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n退款成功，订单最终状态");
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\nonRefundSuccess：" + FastJsonUtils.toJson(response));
            }

            @Override
            public void onRefundFail(RefundResponse response) {
                cancelDialog();
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n退款失败，订单最终状态");
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\nonRefundFail：" + FastJsonUtils.toJson(response));
            }

            @Override
            public void onQueryError(RefundResponse response) {
                cancelDialog();
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n");
                tv_info.append(SpannableUtil.getSpannableString("查询失败，还不能确定订单最终状态，可以通过错误提示是否继续发起查询"));
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\nonQueryError：" + FastJsonUtils.toJson(response));
            }
        });
    }

}
