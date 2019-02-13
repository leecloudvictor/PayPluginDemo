package com.umpay.payplugindemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.umpay.bean.PrintBean;
import com.umpay.bean.YHKPrintBean;
import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.CreditPreauthQueryRequest;
import com.umpay.payplugin.bean.CreditPreauthQueryResponse;
import com.umpay.payplugin.callback.UMPreauthQueryCallBack;
import com.umpay.payplugin.code.CreditPreauthQueryCode;
import com.umpay.payplugin.util.FastJsonUtils;
import com.umpay.utils.StringUtil;

import static com.umpay.payplugindemo.CardAuthActivity.ORDERDATE;
import static com.umpay.payplugindemo.CardAuthActivity.ORDERID;

public class CardAuthQueryActivity extends BaseActivity {

    private Button bt;
    private EditText et_date;//订单日期
    private EditText et_orderId;//预授权订单号
    private String orderDate;//订单日期
    private String orderId;//订单
    private TextView tv_content;//显示内容
    private CreditPreauthQueryResponse selectResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_auth_query);
        bt = (Button) findViewById(R.id.bt);
        et_date = (EditText) findViewById(R.id.et_date);
        et_orderId = (EditText) findViewById(R.id.et_orderid);
        tv_content = (TextView) findViewById(R.id.tv_content);
        Intent intent = getIntent();
        if (intent != null) {
            orderDate = intent.getStringExtra(ORDERDATE);
            orderId = intent.getStringExtra(ORDERID);
        }
        if (!StringUtil.isEmpty(orderDate)) {
            et_date.setText(orderDate);
        }
        if (!StringUtil.isEmpty(orderId)) {
            et_orderId.setText(orderId);
        }
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query();
            }
        });
    }

    /**
     * 查询订单的状态
     */
    private void query() {
        tv_content.setText("");
        String orderDate = et_date.getText().toString();
        String orderId = et_orderId.getText().toString();
        if (StringUtil.isEmpty(orderDate)) {
            tv_content.append("\n------------------------------------------------------------");
            tv_content.append("\n请输入订单日期");
            return;
        }
        if (StringUtil.isEmpty(orderId)) {
            tv_content.append("\n------------------------------------------------------------");
            tv_content.append("\n请输入订单号");
            return;

        }

        CreditPreauthQueryRequest request = new CreditPreauthQueryRequest();
        request.orderDate = orderDate;
        request.orderId = orderId;
        progressDialog("正在查询");
        UMPay.getInstance().creditPreauthQuery(request, new UMPreauthQueryCallBack() {
            @Override
            public void onQuerySuccess(CreditPreauthQueryResponse response) {
                //查询成功 但是不代表最终的订单状态，具体的订单状态请处理 返回信息中的 transState
                cancelDialog();
                Log.e("TAG", "onQuerySuccess:" + FastJsonUtils.toJson(response));
                selectResponse = response;
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\n查询成功");
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonQuerySuccess：" + FastJsonUtils.toJson(response));

                //不一定有交易
//                print();

            }

            @Override
            public void onQueryFail(CreditPreauthQueryResponse response) {
                cancelDialog();
                Log.e("TAG", "onQueryFail:" + FastJsonUtils.toJson(response));

                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\n查询失败");
                tv_content.append("\n--------------------------------------------------------------------------------");
                tv_content.append("\nonQueryFail：" + FastJsonUtils.toJson(response));
            }


            @Override
            public void onReBind(int code, String msg) {
                cancelDialog();
                reBind(code, msg);
            }
        });
    }

    private void print() {
        if (selectResponse != null && selectResponse.code == CreditPreauthQueryCode.QUERY_SUCCESS) {
            getPrintContent(selectResponse);
        }
    }

    private void getPrintContent(CreditPreauthQueryResponse bean) {
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
        com.umpay.utils.Print.getInstance(this).printYHK(printBean);

    }


}
