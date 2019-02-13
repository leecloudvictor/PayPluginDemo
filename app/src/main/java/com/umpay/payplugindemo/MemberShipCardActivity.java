package com.umpay.payplugindemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.MemberShipCardRequest;
import com.umpay.payplugin.bean.MemberShipCardResponse;
import com.umpay.payplugin.callback.UMCancelCardCallback;
import com.umpay.payplugin.callback.UMMemberShipCardCallback;
import com.umpay.payplugin.util.FastJsonUtils;
import com.umpay.utils.StringUtil;

/**
 * 作者:$ dxn
 * 时间:2018/7/23 15:20
 * 描述:读会员卡
 */

public class MemberShipCardActivity extends BaseActivity {
    private Button bt_readCard;
    private TextView info;
    private EditText edt_track;
    private EditText edt_start;
    private EditText edt_end;
    private ScrollView sc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mscard);
        setToolBar("会员卡");
        sc = (ScrollView) findViewById(R.id.sc);
        info = (TextView) findViewById(R.id.tip);
        edt_track = (EditText) findViewById(R.id.edt_track);
        edt_start = (EditText) findViewById(R.id.edt_start);
        edt_end = (EditText) findViewById(R.id.edt_end);
        bt_readCard = (Button) findViewById(R.id.bt_readCard);
        bt_readCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

    }

    //从0开始
    private void search() {
        final MemberShipCardRequest request = new MemberShipCardRequest();

        if (StringUtil.isEmpty(edt_track.getText().toString())) {
            Toast.makeText(this, "请输入磁道类型", Toast.LENGTH_LONG).show();
            return;
        }

        request.trackType = Integer.valueOf(edt_track.getText().toString());

//        progressDialog("开始搜卡");
        UMPay.getInstance().readMemberShipCard(FastJsonUtils.toJson(request), new UMMemberShipCardCallback() {
            @Override
            public void onReBind(int code, String msg) {
                cancelDialog();
                reBind(code, msg);
            }

            @Override
            public void onReadSuccess(MemberShipCardResponse response) {
                cancelDialog();
                info.setText(FastJsonUtils.toJson(response));
            }

            @Override
            public void onReadFail(MemberShipCardResponse response) {
                cancelDialog();
                Log.e("onReadFail", FastJsonUtils.toJson(response));
                info.setText(FastJsonUtils.toJson(response));
            }

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            UMPay.getInstance().stopSearchCard(new UMCancelCardCallback() {
                @Override
                public void onReBind(int code, String msg) {
//                    reBind(code, msg);
                }

                @Override
                public void onCancelSuccess() {
                }

                @Override
                public void onCancelFail(String s) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
