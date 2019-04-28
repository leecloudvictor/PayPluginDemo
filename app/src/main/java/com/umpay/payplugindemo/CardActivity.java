package com.umpay.payplugindemo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.umpay.bean.PrintBean;
import com.umpay.bean.YHKPrintBean;
import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.BankCardPayRequest;
import com.umpay.payplugin.bean.BankCardPayResponse;
import com.umpay.payplugin.bean.ConnectBluetoothRequest;
import com.umpay.payplugin.bean.ConnectBluetoothResponse;
import com.umpay.payplugin.bean.RefundRequest;
import com.umpay.payplugin.callback.UMBankCardPayCallback;
import com.umpay.payplugin.callback.UMCancelCardCallback;
import com.umpay.payplugin.callback.UMConnectBluetoothCallBack;
import com.umpay.payplugin.code.UMCardCode;
import com.umpay.payplugin.code.UMConnectBluetoothCode;
import com.umpay.payplugin.util.FastJsonUtils;
import com.umpay.utils.Print;
import com.umpay.utils.SharedPreferencesUtil;
import com.umpay.utils.StringUtil;

import static com.umpay.payplugin.code.UMCardCode.CARD_PAY_SUCCESS;

/**
 * 银行卡支付。
 */
public class CardActivity extends BaseActivity {

    private TextView tv_info;
    private String amount;
    private Button refund;
    private Button bt_print;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        setToolBar();
        amount = getIntent().getStringExtra("amount");

        orderId = getIntent().getStringExtra("orderId");
        tv_info = (TextView) findViewById(R.id.info);
        refund = (Button) findViewById(R.id.refund);
        bt_print = (Button) findViewById(R.id.bt_print);
        findViewById(R.id.search_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        refund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refund();
            }
        });
        bt_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print();
            }
        });


    }

    private void print() {
        if (bankCardPayResponse != null && bankCardPayResponse.code == CARD_PAY_SUCCESS) {
            getPrintContent(bankCardPayResponse);
        }
    }

    private void getPrintContent(BankCardPayResponse bean) {
        YHKPrintBean printBean = new YHKPrintBean();
        printBean.type = PrintBean.YHK_S;
        printBean.date = bean.orderDate;
        printBean.time = bean.platTime;
        printBean.operator = "联动优势";
        printBean.payAmount = StringUtil.convertCent2Dollar(String.valueOf(bean.amount));
        printBean.state = 1;
        printBean.storeNO = bean.unionPayPosId;
        printBean.tradeNo = bean.tradeNo;
        printBean.tradeType = PrintBean.CONSUME;
        printBean.bankName = bean.bankName;
        printBean.subPayType = bean.payType;
        printBean.account = bean.account + printBean.getSubPayType();
        printBean.unionPayMerId = bean.unionPayMerId;
        printBean.cardExpiryDate = bean.cardExpiryDate;
        printBean.uPayTrace = bean.uPayTrace;
        printBean.batchId = bean.batchId;
        printBean.paySeq = bean.paySeq;
        printBean.authCode = bean.authCode;
        Print.getInstance(this).printYHK(printBean);

    }


    private void refund() {
        if (bankCardPayResponse != null) {
            Intent intent = new Intent(this, BankRevokeActivity.class);
            RefundRequest request = new RefundRequest();
            request.orderDate = bankCardPayResponse.orderDate;
            request.orderId = bankCardPayResponse.orderId;
            try {
                request.amount = Integer.parseInt(bankCardPayResponse.amount);
            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), "请输入大于等于1的数字", Toast.LENGTH_SHORT).show();
                return;
            }
            request.tradeNo = bankCardPayResponse.tradeNo;
            request.payType = bankCardPayResponse.payType;
            intent.putExtra("request", request);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "请先完成一笔支付之后在发起退款", Toast.LENGTH_SHORT).show();
        }
    }

    public BankCardPayResponse bankCardPayResponse;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            UMPay.getInstance().stopSearchCard(new UMCancelCardCallback() {
                @Override
                public void onReBind(int code, String msg) {
                }

                @Override
                public void onCancelSuccess() {
                    Log.e("取消读卡", "取消读卡成功");
                }

                @Override
                public void onCancelFail(String s) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void search() {
        bankCardPayResponse=null;
        tv_info.setText("");
        BankCardPayRequest payRequest = new BankCardPayRequest();
        try {
            payRequest.amount = Integer.parseInt(amount);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "请输入大于等于1的数字", Toast.LENGTH_SHORT).show();
            return;
        }
        payRequest.orderId = orderId;
        //这个值可以不传
        tv_info.append("开始支付，订单号：" + payRequest.orderId);
        //开启dialog
        progressDialog("开始搜卡");
        UMPay.getInstance().cardPay(payRequest, new UMBankCardPayCallback() {

            @Override
            public void onReBind(int code, String msg) {
                cancelDialog();
                reBind(code, msg);
            }

            @Override
            public void onPaySuccess(BankCardPayResponse response) {
                cancelDialog();
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n支付成功，订单最终状态");
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\nonPaySuccess：" + FastJsonUtils.toJson(response));
                bankCardPayResponse = response;
            }

            @Override
            public void onPayFail(BankCardPayResponse response) {
                cancelDialog();
                bankCardPayResponse = null;
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n支付失败，订单最终状态");
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\nonPayFail：" + FastJsonUtils.toJson(response));
                if (response.code == UMConnectBluetoothCode.CONNECT_ERROR) {
                    reConnect(response.message);
                }
            }

            @Override
            public void onPayUnknown(BankCardPayResponse response) {
                cancelDialog();
                bankCardPayResponse = null;
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n");
                tv_info.append(SpannableUtil.getSpannableString("支付未知，需要继续调用银行卡订单状态查询接口来确定订单最终状态"));
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\nonPayUnknown：" + FastJsonUtils.toJson(response));
            }

            @Override
            public void onProgressUpdate(BankCardPayResponse response) {

                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\nonProgressUpdate：" + FastJsonUtils.toJson(response));
                if (UMCardCode.START_DOWNLOAD_KEY == response.code) {
                    progressDialog("开始下载密钥！");
                } else if (UMCardCode.START_READ_CARD == response.code) {
                    progressDialog("密钥下载成功，开始刷卡！");
                } else if (UMCardCode.PASSWORD_KEYBOARD_OPEN == response.code) {
                    progressDialog("刷卡成功，请输入密码");
                } else if (UMCardCode.SEND_PAY_REQUEST == response.code) {
                    progressDialog("密码输入成功，发起支付");
                }
            }
        });
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
                search();

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
