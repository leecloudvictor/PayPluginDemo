package com.umpay.payplugindemo;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.ReOrderInfoRequest;
import com.umpay.payplugin.callback.BasePrintCallback;

public class RePrintActivity extends BaseActivity {
    public String TAG = "RePrintActivity";

    private Button bt_reprint;
    private EditText et_orderdate;
    ;
    private EditText et_orderId;
    private TextView tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_print);
        bt_reprint = (Button) findViewById(R.id.bt_reprint);
        et_orderdate = (EditText) findViewById(R.id.et_orderdate);
        et_orderId = (EditText) findViewById(R.id.et_orderid);
        tv_content = (TextView) findViewById(R.id.tv_content);
        bt_reprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print();
            }
        });
    }


    private void print() {
        String orderDate = et_orderdate.getText().toString().trim();
        String orderId = et_orderId.getText().toString().trim();
        ReOrderInfoRequest reOrderInfoRequest = new ReOrderInfoRequest();
        reOrderInfoRequest.orderDate = orderDate;
        reOrderInfoRequest.orderId = orderId;
        UMPay.getInstance().rePrint(reOrderInfoRequest, new BasePrintCallback() {
            @Override
            public void onStart() throws RemoteException {
                Log.e(TAG, "onStart");
                tv_content.append("onStart()\n");
                tv_content.append("-----------------------------\n");
            }

            @Override
            public void onFinish() throws RemoteException {
                Log.e(TAG, "onFinish");
                tv_content.append("onFinish()\n");
                tv_content.append("-----------------------------\n");
            }

            @Override
            public void onError(int errorcode, String detail) throws RemoteException {
                Log.e(TAG, "onError code:" + errorcode + " detail:" + detail);
                tv_content.append("onError() code:"+errorcode+" detail:"+detail+"\n");
                tv_content.append("-----------------------------\n");
            }

            @Override
            public void onReBind(int code, String msg) {
                Log.e(TAG, "断开连接 重新绑定");
                reBind(code, msg);

            }
        });
    }
}
