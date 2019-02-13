package com.umpay.payplugindemo;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.M1Response;
import com.umpay.payplugin.callback.UMM1CardCallBack;
import com.umpay.payplugin.code.M1CardCode;
import com.umpay.payplugin.util.FastJsonUtils;

/**
 * M1卡操作
 */
public class M1CardActivity extends BaseActivity {

    private Button bt_search;
    private TextView info;
    private String serialNum;
    private ScrollView sc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m1_card);
        setToolBar("M1卡");
        sc = (ScrollView) findViewById(R.id.sc);
        info = (TextView) findViewById(R.id.info);

        bt_search = (Button) findViewById(R.id.bt_search);
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        findViewById(R.id.bt_auth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth();
            }
        });
        findViewById(R.id.bt_readCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readCard();
            }
        });
        findViewById(R.id.bt_writECard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeCard();
            }
        });
    }

    private void writeCard() {
        if (isSearch) {
            info.append("\n开始写卡");
            UMPay.getInstance().m1Cardwrite(13, "ffffffffffffffffffffffffffffffff");
        } else {
            info.append("\n请先寻卡验证密钥之后写卡");
        }

    }

    private void readCard() {
        if (isSearch) {
            info.append("\n开始读卡");
            UMPay.getInstance().m1CardRead(13);
        } else {
            info.append("\n请先寻卡验证密钥之后读卡");
        }
    }
    
    private void auth() {
        //搜卡成功之后才能验证
        if (isSearch) {
            info.append("\n开始验证密钥");
            UMPay.getInstance().m1CardAuth("A", 13, "FFFFFFFFFFFF", serialNum);
        } else {
            info.append("\n请先寻卡之后在验证密钥");
        }
    }

    private void search() {
        info.setText("");
        info.append("\n开始搜卡");
        UMPay.getInstance().m1CardSearch(10, new MyListener());
    }

    private boolean isSearch = false;

    public class MyListener implements UMM1CardCallBack {

        @Override
        public void onSuccess(M1Response m1Response) {
            Log.e("TAG","onSuccess:"+ FastJsonUtils.toJson(m1Response));
            switch (m1Response.code) {

                case M1CardCode.SEARCH_SUCCESS:
                    isSearch = true;
                    //搜卡成功
                    info.append("\ncode:" + m1Response.code + "  搜卡成功,卡序列号为：" + m1Response.serialNum);
                    M1CardActivity.this.serialNum = m1Response.serialNum;
                    break;
                case M1CardCode.AUTH_SUCCES:
                    //授权成功
                    info.append("\n code:" + m1Response.code + "  块" + m1Response.operaBlk + "授权成功！");
                    break;
                case M1CardCode.WRITE_SUCCES:
                    //写入成功
                    info.append("\n code:" + m1Response.code + "  块" + m1Response.operaBlk + "写卡成功！");
                    break;
                case M1CardCode.READ_SUCCES:
                    //读成功
                    info.append("\n code:" + m1Response.code + "  块" + m1Response.operaBlk + "读成功！ 内容：" + m1Response.readInfo);

                    break;
            }
            scroll();
        }

        @Override
        public void onError(M1Response m1Response) {
            Log.e("TAG","onError:"+ FastJsonUtils.toJson(m1Response));
            info.append("\n操作失败：code:" + m1Response.code + "---失败信息：" + m1Response.message);
            scroll();
        }


        @Override
        public void onReBind(int code, String msg) {
            cancelDialog();
            reBind(code, msg);
        }
    }

    public void scroll() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sc.smoothScrollTo(0, info.getMeasuredHeight());
            }
        }, 30);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMPay.getInstance().m1CardStop();
    }
}
