package com.umpay.payplugindemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.BackShopInfo;
import com.umpay.payplugin.callback.UMMerInfoCallBack;
import com.umpay.payplugin.util.FastJsonUtils;

public class MerInfoActivity extends BaseActivity {

    private Button bt_get;
    private TextView tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mer_info);
        bt_get = (Button) findViewById(R.id.bt_get);
        tv_content = (TextView) findViewById(R.id.tv_content);
        bt_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo();
            }
        });
    }

    private void getInfo() {

        UMPay.getInstance().getMerInfo(new UMMerInfoCallBack() {
            @Override
            public void onSuccess(BackShopInfo info) {

                Log.e("TAG","onSuccess:"+FastJsonUtils.toJson(info));
                tv_content.setText(FastJsonUtils.toJson(info));
            }

            @Override
            public void onError(BackShopInfo info) {

                Log.e("TAG","onError:"+FastJsonUtils.toJson(info));
                tv_content.setText(FastJsonUtils.toJson(info));
            }

            @Override
            public void onReBind(int code, String msg) {
                Log.e("TAG","onReBind  code:"+code+" msg:"+msg);
                reBind(code, msg);
            }
        });
    }
}
