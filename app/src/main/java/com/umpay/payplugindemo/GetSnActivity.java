package com.umpay.payplugindemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.umpay.payplugin.UMPay;
import com.umpay.utils.StringUtil;

/**
 * 作者:$ dxn
 * 时间:2018/3/8 11:26
 * 描述:获取SNactivity
 */

public class GetSnActivity extends BaseActivity {
    private Button bt_getsn;
    private TextView tv_sn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_sn);
        bt_getsn=(Button)findViewById(R.id.bt_getsn);
        tv_sn=(TextView) findViewById(R.id.tv_sn);
        bt_getsn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (StringUtil.isEmpty(UMPay.getInstance().getPosSn()
                )) {
                    tv_sn.setText("获取失败");
                }else {

                    tv_sn.setText(UMPay.getInstance().getPosSn());
                }
                ;
            }
        });
    }
}
