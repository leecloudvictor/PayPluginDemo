package com.umpay.payplugindemo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.RefundRequest;
import com.umpay.payplugin.bean.ScanPayRequest;
import com.umpay.payplugin.bean.ScanPayResponse;
import com.umpay.payplugin.callback.UMScanPayCallback;
import com.umpay.payplugin.code.PrintCode;
import com.umpay.payplugin.code.UMScanCode;
import com.umpay.payplugin.util.FastJsonUtils;
import com.umpay.payplugin.util.UMPayLog;
import com.umpay.utils.DeviceUtil;

import java.text.SimpleDateFormat;
import java.util.List;

public class ScanActivity extends BaseActivity {

    private CompoundBarcodeView barcodeScannerView;
    private Context context;
    private CaptureManager capture;
    private String amount;
    private TextView tv_amount;
    private TextView tv_info;
    private Button refund;
    private EditText code;
    private View payLayout;
    private Button pay;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        setToolBar();
        context = this;
        initView(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (capture != null) {
            capture.onSaveInstanceState(outState);
        }
    }

    /**
     * 设置闪光灯状态，当没有闪光灯的时候返回false，否则返回rue
     *
     * @param isOn
     * @return
     */
    private boolean setTorchState(boolean isOn) {
        if (!hasFlash()) {
            return false;
        }
        if (isOn) {
            barcodeScannerView.setTorchOn();
        } else {
            barcodeScannerView.setTorchOff();
        }
        return true;
    }

    /**
     * Check if the device's camera has a Flashlight.
     *
     * @return true if there is Flashlight, otherwise false.
     */
    private boolean hasFlash() {
        return context.getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    protected void initView(Bundle savedInstanceState) {
        refund = (Button) findViewById(R.id.refund);
        pay = (Button) findViewById(R.id.pay);
        barcodeScannerView = (CompoundBarcodeView) findViewById(R.id.barcode_scanner);
        payLayout = findViewById(R.id.payLayout);
        code = (EditText) findViewById(R.id.code);
        if (DeviceUtil.hasCameraSupport(this)) {
            barcodeScannerView.decodeContinuous(barcodeCallback);
            barcodeScannerView.setStatusText("请将二维码放在取景器内");
            // if the device does not have flashlight in its camera,
            // then remove the switch flashlight button...
            capture = new CaptureManager(this, barcodeScannerView) {
                protected void returnResult(BarcodeResult rawResult) {
                    barcodeCallback.barcodeResult(rawResult);
                }
            };
            capture.initializeFromIntent(getIntent(), savedInstanceState);
            capture.decode();
            payLayout.setVisibility(View.GONE);
        } else {
            barcodeScannerView.setVisibility(View.GONE);

        }

        code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().endsWith("\n")) {
                    String mediaNo = s.toString();
                    mediaNo = mediaNo.replace("\n", "");
                    code.setText("");
                    pay(mediaNo);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        amount = getIntent().getStringExtra("amount");
        orderId = getIntent().getStringExtra("orderId");
        tv_amount = (TextView) findViewById(R.id.amount);
        tv_info = (TextView) findViewById(R.id.info);
        tv_amount.setText("支付金额（分）：" + amount);
        refund.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refund();
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mediaNo = code.getText().toString();
                pay(mediaNo);
            }
        });
    }

    private void pay(String mediaNo) {
        ScanPayRequest request = new ScanPayRequest();
        try {
            request.amount = Integer.parseInt(amount);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "请输入大于等于1的数字", Toast.LENGTH_SHORT).show();
            return;
        }
        request.mediaNo = mediaNo;
        request.goodsDescribe = "商品描述商品描述";
        request.goodsInfo = "商品信息";
        request.orderId = orderId;
        progressDialog("正在支付");
        tv_info.append("\n订单号：" + request.orderId);
        UMPay.getInstance().scanPay(request, umScanPayCallback);
    }


    private void refund() {
        if (scanPayResponse != null) {
            Intent intent = new Intent(this, ScanRevokeActivity.class);
            RefundRequest request = new RefundRequest();
            request.orderDate = scanPayResponse.orderDate;
            request.amount = Integer.parseInt(scanPayResponse.payAmount);
            request.orderId = scanPayResponse.orderId;
            request.tradeNo = scanPayResponse.tradeNo;
            request.payType = scanPayResponse.payType;
            intent.putExtra("request", request);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "请先完成一笔支付之后在发起退款", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (capture != null) {
            capture.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (capture != null) {
            capture.onDestroy();
            capture = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (capture != null) {
            capture.onPause();
        }
    }

    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
    private BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                UMPayLog.e("扫码结果：" + result.getText());
                pay(result.getText());
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> list) {

        }
    };
    private ScanPayResponse scanPayResponse;
    private UMScanPayCallback umScanPayCallback = new UMScanPayCallback() {
        @Override
        public void onReBind(int code, String msg) {
            cancelDialog();
            reBind(code, msg);
        }

        @Override
        public void onPaySuccess(ScanPayResponse response) {
            //支付成功，订单最终状态
            cancelDialog();
            if (response.code == UMScanCode.PAY_SUCCESS) {

                scanPayResponse = response;
                barcodeScannerView.setVisibility(View.INVISIBLE);
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\n支付成功，订单最终状态");
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\nonPaySuccess：" + FastJsonUtils.toJson(response));
            }
            //补充打印信息
            if (response.code == PrintCode.PRINT_ERROR) {
                tv_info.append("\n--------------------------------------------------------------------------------");
                tv_info.append("\nonPaySuccess：" + FastJsonUtils.toJson(response));
            }

        }

        @Override
        public void onPayFail(ScanPayResponse response) {
            //支付失败，订单最终状态

            cancelDialog();
            barcodeScannerView.setVisibility(View.INVISIBLE);
            tv_info.append("\n--------------------------------------------------------------------------------");
            tv_info.append("\n支付失败，订单最终状态");
            tv_info.append("\n--------------------------------------------------------------------------------");
            tv_info.append("\nonPayFail：" + FastJsonUtils.toJson(response));


        }

        @Override
        public void onPayUnknown(ScanPayResponse response) {
            //支付未知，一定要需要继续发起查询操作

            cancelDialog();
            barcodeScannerView.setVisibility(View.INVISIBLE);
            tv_info.append("\n--------------------------------------------------------------------------------");
            tv_info.append("\n");
            tv_info.append(SpannableUtil.getSpannableString("支付未知，需要继续调用扫码付订单状态查询接口来确定订单最终状态"));
            tv_info.append("\n--------------------------------------------------------------------------------");
            tv_info.append("\nonPayUnknown：" + FastJsonUtils.toJson(response));
        }
    };

}
