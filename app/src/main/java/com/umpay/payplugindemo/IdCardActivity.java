package com.umpay.payplugindemo;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.IdCardRequest;
import com.umpay.payplugin.bean.IdCardResponse;
import com.umpay.payplugin.callback.UMIdCardCallback;
import com.umpay.payplugin.code.IdCardCode;
import com.umpay.payplugin.util.FastJsonUtils;

public class IdCardActivity extends BaseActivity implements View.OnClickListener {

    private Button bt_search;
    private Button bt_send;
    private ScrollView sc;
    private TextView tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id_card);
        bt_search = (Button) findViewById(R.id.bt_search);
        bt_send = (Button) findViewById(R.id.bt_send);
        sc = (ScrollView) findViewById(R.id.sc);
        tv_content = (TextView) findViewById(R.id.tv_content);

        bt_search.setOnClickListener(this);
        bt_send.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_search:
                IdCardRequest idCardRequest = new IdCardRequest();
                idCardRequest.searchTimeOut = 40;
                String json = FastJsonUtils.toJson(idCardRequest);
                UMPay.getInstance().searchIdCard(json, umIdCardCallback);
                stringBuffer.append("\n请帖身份证");
                tv_content.setText(stringBuffer.toString());
                break;
            case R.id.bt_send:
                sendApdu("0084000004");
                sendApdu("00A4040010D1560001018003810000000200000002");
                break;

        }
    }


    /**
     * 发送apdu 指令
     *
     * @param apdu
     */
    public void sendApdu(String apdu) {
        IdCardRequest request = new IdCardRequest();
        request.str_apdu = apdu;
        String jsons = FastJsonUtils.toJson(request);
        UMPay.getInstance().sendApduToIdCard(jsons);
    }

    StringBuffer stringBuffer = new StringBuffer();

    public UMIdCardCallback umIdCardCallback = new UMIdCardCallback() {
        @Override
        public void onReBind(int code, String msg) {
            cancelDialog();
            reBind(code, msg);
        }

        @Override
        public void onSuccess(IdCardResponse idCardResponse) {

            switch (idCardResponse.code) {
                case IdCardCode.IDCARD_SEARCH_SUCCESS:
                    stringBuffer.append("\n搜卡成功");
                    tv_content.setText(stringBuffer.toString());
                    scroll();
                    break;
                case IdCardCode.RESPONSE_SUCCESS:
                    stringBuffer.append("\n卡片返回信息：\n"
                            + "传入：" + idCardResponse.requestApdu
                            + "\n返回：" + idCardResponse.responeData);
                    stringBuffer.append("\n-----------------------------");
                    tv_content.setText(stringBuffer.toString());
                    scroll();
                    break;

            }

        }


        @Override
        public void onError(IdCardResponse idCardResponse) {

            switch (idCardResponse.code) {
                case IdCardCode.IDCARD_SEARCH_ERROR:
                    stringBuffer.append("\n搜卡成功");
                    tv_content.setText(stringBuffer.toString());
                    scroll();
                    break;
                case IdCardCode.BINDER_ERROR:
                    stringBuffer.append("\n绑定失败，请重新绑定");
                    tv_content.setText(stringBuffer.toString());
                    scroll();
                    break;
                case IdCardCode.IDCARD_SEARCH_TIMEOUT:
                    stringBuffer.append("\n寻卡超时，请重新发起搜卡");
                    tv_content.setText(stringBuffer.toString());
                    scroll();
                    break;
                case IdCardCode.REQUEST_APDU_EMPTY:
                    stringBuffer.append("\n发送指令为空，请检查指令");
                    tv_content.setText(stringBuffer.toString());
                    scroll();
                    break;
                case IdCardCode.JSON_ERROR_ONBACK:
                    stringBuffer.append("\n解析POS返回信息异常，请搜卡重试");
                    tv_content.setText(stringBuffer.toString());
                    scroll();
                    break;
                case IdCardCode.RESPONSE_ERROR:
                    stringBuffer.append("\n apdu 指令返回信息异常");
                    stringBuffer.append("\n-----------------------------");
                    tv_content.setText(stringBuffer.toString());
                    scroll();
                    break;
            }
        }
    };

    /**
     * 滑动scrollview
     */
    void scroll() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sc.scrollTo(0, tv_content.getMeasuredHeight());
            }
        }, 30);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMPay.getInstance().stopIdCard();
    }
}
