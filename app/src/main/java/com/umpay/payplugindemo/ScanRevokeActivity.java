package com.umpay.payplugindemo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.RefundRequest;
import com.umpay.payplugin.bean.RefundResponse;
import com.umpay.payplugin.callback.UMScanRefundCallback;
import com.umpay.payplugin.util.FastJsonUtils;
import com.umpay.payplugin.util.UMPayLog;

/**
 * 扫码付撤销
 */
public class ScanRevokeActivity extends BaseActivity {

    private TextView tv_info;
    private EditText amount;
    private EditText orderId;
    private EditText orderDate;
    private String[] array;
    private String payType;
    private RefundRequest request;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_revoke);
        setToolBar("扫码付撤销");
        tv_info = (TextView) findViewById(R.id.info);
        findViewById(R.id.refund).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refund();
            }
        });
        request = (RefundRequest) getIntent().getSerializableExtra("request");
        amount = (EditText) findViewById(R.id.amount);
        orderId = (EditText) findViewById(R.id.orderId);
        orderDate = (EditText) findViewById(R.id.orderDate);
        Spinner type = (Spinner) findViewById(R.id.type);
        array = getResources().getStringArray(R.array.scan);
        if (request != null) {
            amount.setText(String.valueOf(request.amount));
            orderId.setText(request.orderId);
            orderDate.setText(request.orderDate);
            for (int i = 0; i < array.length; i++) {
                String str = array[i].split("-")[1];
                if (str.equals(request.payType)) {
                    type.setSelection(i);
                    break;
                }
            }
        }


        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                payType = array[position].split("-")[1];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void refund() {
        RefundRequest refundRequest = new RefundRequest();
        try{
            refundRequest.amount = Integer.parseInt(amount.getText().toString().trim());
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "请输入正确的金额", Toast.LENGTH_SHORT).show(); ;
            return;
        }
        refundRequest.orderId = orderId.getText().toString().trim();
        refundRequest.orderDate = orderDate.getText().toString().trim();
        refundRequest.payType = payType;
        refund(refundRequest);
    }

    private void refund(RefundRequest request) {
        if (request != null) {
            tv_info.setText("");
            tv_info.setText("订单号：" + request.orderId);
            UMPayLog.e("RefundRequest:" + request.toString());
            progressDialog("开始撤销");
            UMPay.getInstance().scanRevoke(request, new UMScanRefundCallback() {
                @Override
                public void onReBind(int code, String msg) {
                    cancelDialog();
                    reBind(code, msg);
                }

                @Override
                public void onRefundSuccess(RefundResponse response) {
                    cancelDialog();
                    tv_info.append("\n--------------------------------------------------------------------------------");
                    tv_info.append("\n撤销成功，订单最终状态");
                    tv_info.append("\n--------------------------------------------------------------------------------");
                    tv_info.append("\nonRefundSuccess：" + FastJsonUtils.toJson(response));
                }

                @Override
                public void onRefundFail(RefundResponse response) {
                    cancelDialog();
                    tv_info.append("\n--------------------------------------------------------------------------------");
                    tv_info.append("\n撤销失败，订单最终状态");
                    tv_info.append("\n--------------------------------------------------------------------------------");
                    tv_info.append("\nonRefundFail：" + FastJsonUtils.toJson(response));
                }

                @Override
                public void onRefundUnknown(RefundResponse response) {
                    cancelDialog();
                    tv_info.append("\n--------------------------------------------------------------------------------");
                    tv_info.append("\n");
                    tv_info.append(SpannableUtil.getSpannableString("撤销状态未知，(有可能已经撤销成功)必须调用撤销状态查询接口来确定是否撤销成功"));
                    tv_info.append("\n--------------------------------------------------------------------------------");
                    tv_info.append("\nonRefundUnknown：" + FastJsonUtils.toJson(response));
                }
            });
        } else {
            Toast.makeText(this, "ScanPayResponse对象为空，不能撤销", Toast.LENGTH_SHORT).show();
        }

    }

}
