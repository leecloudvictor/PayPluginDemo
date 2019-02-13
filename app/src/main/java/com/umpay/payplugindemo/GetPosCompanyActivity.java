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
 * 时间:2018/3/8 13:47
 * 描述:获取厂商名称
 */

public class GetPosCompanyActivity extends BaseActivity {
    private Button bt_getCompany;
    private TextView tv_company;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_company);
        bt_getCompany = (Button) findViewById(R.id.bt_getCompany);
        tv_company = (TextView) findViewById(R.id.tv_company);
        bt_getCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isEmpty(UMPay.getInstance().getPosCompany())) {
                    tv_company.setText("获取失败");
                } else {
                    tv_company.setText(UMPay.getInstance().getPosCompany());
                }
            }
        });
    }
}
