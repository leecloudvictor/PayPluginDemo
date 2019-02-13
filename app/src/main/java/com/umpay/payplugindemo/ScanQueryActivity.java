package com.umpay.payplugindemo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.ScanPayRequest;
import com.umpay.payplugin.bean.ScanPayResponse;
import com.umpay.payplugin.callback.UMScanQueryCallback;
import com.umpay.payplugin.util.FastJsonUtils;

/**
 * 扫码付订单状态查询
 */
public class ScanQueryActivity extends BaseActivity {

    private EditText orderId;
    private String[] array;
    private String payType;
    private TextView tv_info;
    private EditText orderDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_query);
        setToolBar("扫码付订单状态查询");
        orderId = (EditText) findViewById(R.id.orderId);
        orderDate = (EditText) findViewById(R.id.orderDate);
        Spinner type = (Spinner) findViewById(R.id.type);
        array = getResources().getStringArray(R.array.scan);
        tv_info = (TextView) findViewById(R.id.info);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                payType = array[position].split("-")[1];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button query = (Button) findViewById(R.id.query);
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query();
            }
        });
    }

    private void query() {
        tv_info.setText("");
        ScanPayRequest request = new ScanPayRequest();
        request.orderId = orderId.getText().toString().trim();
        request.payType = payType;
        //订单时间 格式yyyyMMdd （由于数据库做的分表，所以需要传这个字段，如果不传就差当月数据）
        request.orderDate = orderDate.getText().toString().trim();
        progressDialog("正在查询");
        UMPay.getInstance().scanQuery(request, new MyQueryCallBack());
    }

    /**
     * 查询回调
     */
    class MyQueryCallBack implements UMScanQueryCallback {
        @Override
        public void onPaySuccess(ScanPayResponse response) {
            cancelDialog();
            tv_info.append("\n--------------------------------------------------------------------------------");
            tv_info.append("\n支付成功，订单最终状态");
            tv_info.append("\n--------------------------------------------------------------------------------");
            //支付成功
            tv_info.append("\nonPaySuccess:" + FastJsonUtils.toJson(response));
        }

        @Override
        public void onPayFail(ScanPayResponse response) {
            cancelDialog();
            //支付失败
            tv_info.append("\n--------------------------------------------------------------------------------");
            tv_info.append("\n支付失败，订单最终状态");
            tv_info.append("\n--------------------------------------------------------------------------------");
            tv_info.append("\nonPayFail:" + FastJsonUtils.toJson(response));
        }

        @Override
        public void onPaying(ScanPayResponse response) {
            cancelDialog();
            //支付中
            tv_info.append("\n--------------------------------------------------------------------------------");
            tv_info.append("\n支付中");
            tv_info.append("\n--------------------------------------------------------------------------------");
            tv_info.append("\nonPaying:" + FastJsonUtils.toJson(response));
        }

        @Override
        public void onQueryError(ScanPayResponse response) {
            cancelDialog();
            //查询失败
            tv_info.append("\n--------------------------------------------------------------------------------");
            tv_info.append("\n");
            tv_info.append(SpannableUtil.getSpannableString("查询失败，还不能确定订单最终状态，可以通过错误提示是否继续调用查询接口"));
            tv_info.append("\n--------------------------------------------------------------------------------");
            tv_info.append("\nonQueryError:" + FastJsonUtils.toJson(response));
        }

        @Override
        public void onReBind(int code, String msg) {
            cancelDialog();
            reBind(code, msg);
        }
    }
}
