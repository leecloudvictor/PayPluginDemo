package com.umpay.payplugindemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.OrderListRequest;
import com.umpay.payplugin.bean.OrderListResponse;
import com.umpay.payplugin.callback.ListCallBack;
import com.umpay.payplugin.util.FastJsonUtils;
import com.umpay.utils.StringUtil;

import java.util.ArrayList;

public class OrderListActivity extends BaseActivity {

    private String orderDate;//订单日期
    private String orderId;//订单号
    private Spinner sp_type;//交易类型
    private Spinner sp_state;//订单状态
    private EditText et_date;
    private EditText et_id;
    private String str_type;
    private String str_state;
    private Button bt_search;
    private EditText et_state;
    ArrayList<OrderType> typeList = new ArrayList<>();
    ArrayList<OrderState> stateList = new ArrayList<>();

    ArrayAdapter<OrderType> typeAdapter;
    ArrayAdapter<OrderState> stateAdapter;
    private TextView tv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        et_date = (EditText) findViewById(R.id.et_date);
        et_id = (EditText) findViewById(R.id.et_orderid);
        sp_type = (Spinner) findViewById(R.id.sp_type);
        sp_state = (Spinner) findViewById(R.id.sp_state);
        bt_search = (Button) findViewById(R.id.bt_search);
        tv_content = (TextView) findViewById(R.id.tv_content);
        et_state = (EditText) findViewById(R.id.et_state);
        typeList.add(new OrderType("ALL", "所有渠道"));
        typeList.add(new OrderType("AL", "支付宝支付"));
        typeList.add(new OrderType("WX", "微信支付"));
        typeList.add(new OrderType("BK", "银行卡支付"));
        typeList.add(new OrderType("YL", "银联二维码支付"));
        typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_type.setAdapter(typeAdapter);
        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OrderType orderType = (OrderType) sp_type.getSelectedItem();
                str_type = orderType.type;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stateList.add(new OrderState("0", "所有状态"));
        stateList.add(new OrderState("1", "支付中"));
        stateList.add(new OrderState("2", "支付成功"));
        stateList.add(new OrderState("3", "支付失败"));
        stateList.add(new OrderState("5", "交易撤销"));
        stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stateList);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_state.setAdapter(stateAdapter);
        sp_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                OrderState orderState = (OrderState) sp_state.getSelectedItem();
                str_state = orderState.state;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getList();
            }
        });
    }


    //获取订单列表
    public void getList() {
        OrderListRequest listRequest = new OrderListRequest();
        if (!("AL".equals(str_type) || "WX".equals(str_type) || "BK".equals(str_type) || "YL".equals(str_type))) {
            str_type = "";
        }
        listRequest.payType = str_type;
        listRequest.pageIndex = 1;
        listRequest.pageSize = 10;
        orderDate = et_date.getText().toString();
        listRequest.orderDate = orderDate;//订单日期
        orderId = et_id.getText().toString();
        listRequest.orderId = orderId;//订单号
        String ss = et_state.getText().toString();
        if (StringUtil.isEmpty(ss)) {
            if (!"0".equals(str_state)) {
                listRequest.orderState = str_state;
            }
        }else{
            listRequest.orderState = ss;
        }

        progressDialog("正在查询");
        UMPay.getInstance().getOrderList(listRequest, new ListCallBack() {
            @Override
            public void onSuccess(OrderListResponse response) {
                cancelDialog();
                Log.e("查询 onSuccess", FastJsonUtils.toJson(response));
                tv_content.setText(FastJsonUtils.toJson(response));
            }

            @Override
            public void onFail(OrderListResponse response) {
                cancelDialog();
                Log.e("查询 onFail", FastJsonUtils.toJson(response));
                tv_content.setText(FastJsonUtils.toJson(response));
            }

            @Override
            public void onError(OrderListResponse response) {
                cancelDialog();
                Log.e("查询 onError", FastJsonUtils.toJson(response));
                tv_content.setText(FastJsonUtils.toJson(response));
            }

            @Override
            public void onReBind(int code, String msg) {
                cancelDialog();
                reBind(code, msg);
            }
        });

    }


    public class OrderType {
        public String typeName;
        public String type;

        public OrderType(String type, String typeName) {
            this.type = type;
            this.typeName = typeName;
        }

        @Override
        public String toString() {
            return typeName;
        }
    }

    public class OrderState {
        public String stateName;
        public String state;

        public OrderState(String state, String stateName) {
            this.state = state;
            this.stateName = stateName;

        }

        @Override
        public String toString() {
            return stateName;
        }
    }
}
