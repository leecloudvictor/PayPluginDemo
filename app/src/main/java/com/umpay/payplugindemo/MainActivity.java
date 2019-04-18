package com.umpay.payplugindemo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.callback.UMBindCallBack;
import com.umpay.payplugin.util.UMPayLog;
import com.umpay.utils.StringUtil;

public class MainActivity extends BaseActivity {

    private EditText edit_amount;
    private EditText orderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolBar();


        edit_amount = (EditText) findViewById(R.id.edit_amount);
        orderId = (EditText) findViewById(R.id.orderId);


        findViewById(R.id.scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });
        findViewById(R.id.card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                card();
            }
        });
        findViewById(R.id.scanQuery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQuery();
            }
        });

        findViewById(R.id.scanRevoke).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanRevoke();
            }
        });

        findViewById(R.id.scanRefund).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanRefund();
            }
        });
        findViewById(R.id.bankRevoke).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bankRevoke();
            }
        });
        findViewById(R.id.bankRefund).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bankRefund();
            }
        });
        findViewById(R.id.cardQuery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardQuery();
            }
        });


        findViewById(R.id.refundStateQuery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refundStateQuery();
            }
        });
        findViewById(R.id.bt_M1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                M1();
            }
        });

        findViewById(R.id.bt_cardAuth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardAuth();
            }
        });

        findViewById(R.id.bt_cardAuth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardAuth();
            }
        });

        findViewById(R.id.bt_nprint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nPrint();
            }
        });
        findViewById(R.id.bt_getinfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo();
            }
        });
        findViewById(R.id.bt_getsn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSn();
            }
        });
        findViewById(R.id.bt_getCompany).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCompany();
            }
        });
        findViewById(R.id.memberCard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(MemberShipCardActivity.class);
            }
        });
        findViewById(R.id.blueconnect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(BlueToothActivity.class);
            }
        });
        findViewById(R.id.bt_orderlist).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(OrderListActivity.class);
            }
        });
        findViewById(R.id.bt_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(GetOutOrderIdActivity.class);
            }
        });
        findViewById(R.id.bt_BalanceInquiry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(BalanceInquiryActivity.class);
            }
        });
        findViewById(R.id.bt_reprint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity(RePrintActivity.class);
            }
        });

        UMPay.getInstance().debug(true);
        UMPay.getInstance().bind(getApplicationContext(), new UMBindCallBack() {
            @Override
            public void bindException(Exception e) {
                UMPayLog.e("绑定失败！" + e.getMessage());
            }

            @Override
            public void bindSuccess() {
                //只有绑定成功之后才可以做后续交易
                UMPayLog.e("绑定成功！");
            }

            @Override
            public void bindDisconnected() {
                UMPayLog.e("断开绑定！");
            }
        });

        //获取应用的appid和应用签名的sha1值
        String appId = UMPay.getInstance().getAppId();
        String sha1 = UMPay.getInstance().getSha1();
        UMPayLog.e("appId:" + appId + "\n sha1:" + sha1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        String orderIdStr = StringUtil.createOrderId();
        orderId.setText(orderIdStr);
    }

    /**
     * 银行卡退款（扫码付想隔天的交易想退款应该调用这个接口）
     */
    private void bankRefund() {
        openActivity(BankRefundActivity.class);
    }

    /**
     * 银行卡撤销（扫码付想当天的交易想退款应该调用这个接口）
     */
    private void bankRevoke() {
        openActivity(BankRevokeActivity.class);
    }

    /**
     * 扫码付退款（扫码付想隔天的交易想退款应该调用这个接口）
     */
    private void scanRefund() {
        openActivity(ScanRefundActivity.class);
    }

    /**
     * 扫码付撤销（扫码付想当天的交易想退款应该调用这个接口）
     */
    private void scanRevoke() {
        openActivity(ScanRevokeActivity.class);
    }

    /**
     * 获取商户信息
     */
    private void getInfo() {
        openActivity(MerInfoActivity.class);
    }

    /**
     * 获取SN
     */
    private void getSn() {
        openActivity(GetSnActivity.class);
    }

    /**
     * 获取厂商名称
     */
    private void getCompany() {
        openActivity(GetPosCompanyActivity.class);
    }


    /**
     * 预授权相关操作
     */
    private void cardAuth() {
        openActivity(CardAuthActivity.class);
    }


    /**
     * 扫码付支付状态查询
     */
    private void scanQuery() {
        openActivity(ScanQueryActivity.class);
    }

    /**
     * m1卡操作
     */
    private void M1() {
        openActivity(M1CardActivity.class);
    }


    /**
     * 退款状态查询
     */
    private void refundStateQuery() {
        openActivity(RefundStateQueryActivity.class);
    }

    /**
     * 银行卡支付状态查询
     */
    private void cardQuery() {
        openActivity(CardPayQueryActivity.class);
    }

    /**
     * 新打印方法
     */
    private void nPrint() {
        openActivity(NPrintActivity.class);
    }

    /**
     * 银行卡支付
     */
    private void card() {
        String amount = edit_amount.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            Toast.makeText(this, "请先输入金额！", Toast.LENGTH_SHORT).show();
            return;
        }

        //订单id (推荐接入方后台生成，保证自己平台唯一,最大长度32位)，下方为测试所用
        String orderIdStr = orderId.getText().toString().trim();
        if (StringUtil.isEmpty(orderIdStr)) {
            Toast.makeText(this, "请输入订单号！", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, CardActivity.class);
        intent.putExtra("amount", amount);
        intent.putExtra("orderId", orderIdStr);
        startActivity(intent);
    }


    /**
     * 扫码付，支持微信，支付宝，银联二维码
     */
    private void scan() {
        String amount = edit_amount.getText().toString();
        if (TextUtils.isEmpty(amount)) {
            Toast.makeText(this, "请先输入金额！", Toast.LENGTH_SHORT).show();
            return;
        }
        //订单id (推荐接入方后台生成，保证自己平台唯一,最大长度32位)，下方为测试所用
        String orderIdStr = orderId.getText().toString().trim();
        if (StringUtil.isEmpty(orderIdStr)) {
            Toast.makeText(this, "请输入订单号！", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra("amount", amount);
        intent.putExtra("orderId", orderIdStr);
        startActivity(intent);
    }

    /**
     * 打开Activity
     */
    private void openActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁的时候需要解除绑定
        UMPay.getInstance().unBind();
    }

}
