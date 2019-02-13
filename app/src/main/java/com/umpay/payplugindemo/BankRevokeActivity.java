package com.umpay.payplugindemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.ConnectBluetoothRequest;
import com.umpay.payplugin.bean.ConnectBluetoothResponse;
import com.umpay.payplugin.bean.RefundRequest;
import com.umpay.payplugin.bean.RefundResponse;
import com.umpay.payplugin.callback.UMConnectBluetoothCallBack;
import com.umpay.payplugin.callback.UMRefundCallback;
import com.umpay.payplugin.code.UMConnectBluetoothCode;
import com.umpay.payplugin.code.UMRefundCode;
import com.umpay.payplugin.util.FastJsonUtils;
import com.umpay.payplugin.util.UMPayLog;
import com.umpay.utils.SharedPreferencesUtil;
import com.umpay.utils.StringUtil;

public class BankRevokeActivity extends BaseActivity {

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
        setContentView(R.layout.activity_bank_revoke);
        setToolBar("银行卡撤销");
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
        array = getResources().getStringArray(R.array.bank);
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
            UMPay.getInstance().bankRevoke(request, new UMRefundCallback() {
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

                    if (response.code == UMConnectBluetoothCode.CONNECT_ERROR) {
                        reConnect(response.message);
                    }
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

                @Override
                public void onProgressUpdate(RefundResponse response) {
                    tv_info.append("\n--------------------------------------------------------------------------------");
                    tv_info.append("\nonProgressUpdate：" + FastJsonUtils.toJson(response));
                    if (UMRefundCode.START_DOWNLOAD_KEY == response.code) {
                        progressDialog("开始下载密钥！");
                    } else if (UMRefundCode.START_READ_CARD == response.code) {
                        progressDialog("密钥下载成功，开始刷卡！");
                    } else if (UMRefundCode.PASSWORD_KEYBOARD_OPEN == response.code) {
                        progressDialog("刷卡成功，请输入密码");
                    } else if (UMRefundCode.SEND_REFUND_REQUEST == response.code) {
                        progressDialog("密码输入成功，发起撤销");
                    }
                }
            });
        } else {
            Toast.makeText(this, "ScanPayResponse对象为空，不能撤销", Toast.LENGTH_SHORT).show();
        }

    }




    //简写
    protected void reConnect(String msg) {
        new AlertDialog.Builder(this).setTitle("提示").setMessage(msg)
                .setNegativeButton("重新连接MPOS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        connectBlueTooth();

                    }
                }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                finish();
                dialog.dismiss();
            }
        }).create().show();
    }

    private void connectBlueTooth() {
        final ConnectBluetoothRequest request = new ConnectBluetoothRequest();
        String address = SharedPreferencesUtil.getString(BlueToothActivity.ADDRESS);
        if (!StringUtil.isEmpty(address)) {
            request.address = address;
        }
        progressDialog("正在连接");
        UMPay.getInstance().connectBluetooth(request, new UMConnectBluetoothCallBack() {
            @Override

            public void onConnectSuccess(ConnectBluetoothResponse response) {
                Log.e("连接成功", "连接成功");
                cancelDialog();
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n连接成功");
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\nonConnectSuccess：" + FastJsonUtils.toJson(response));
                refund();

            }

            @Override
            public void onConnectError(ConnectBluetoothResponse response) {
                cancelDialog();
                Log.e("连接失败", "连接失败");
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n连接失败");
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\nonConnectError：" + FastJsonUtils.toJson(response));

            }

            @Override
            public void onReBind(int code, String msg) {
                cancelDialog();
                Log.e("重新绑定", "重新绑定");
                reBind(code, msg);
            }
        });
    }
}

