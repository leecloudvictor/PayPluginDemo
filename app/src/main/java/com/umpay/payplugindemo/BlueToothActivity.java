package com.umpay.payplugindemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.umpay.bean.BluetoothDeviceContext;
import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.bean.ConnectBluetoothRequest;
import com.umpay.payplugin.bean.ConnectBluetoothResponse;
import com.umpay.payplugin.callback.UMConnectBluetoothCallBack;
import com.umpay.payplugin.util.FastJsonUtils;
import com.umpay.utils.SharedPreferencesUtil;
import com.umpay.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:$ dxn
 * 时间:2018/8/16 18:34
 * 描述:蓝牙列表的activity
 */

/**
 * MPOS接入指南
 * 1   点击Demo中的蓝牙连接，搜索蓝牙，在搜索到的列表中选择要连接的蓝牙，蓝牙连接成功后，可回到主页面正常操作有关银行卡的部分
 * 在银行卡的回调中如果出现code=60190204，代表蓝牙连接失败，跟根据需求重连或者不重连。
 */
public class BlueToothActivity extends BaseActivity {

    private List<BluetoothDeviceContext> discoveredDevices = new ArrayList<BluetoothDeviceContext>();
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private boolean bStopFlag = false;
    private TextView tv_result;
    private Button btn_search;
    public static String ADDRESS = "ADDRESS";//存在本地的蓝牙地址

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        setToolBar();
        tv_result = (TextView) findViewById(R.id.tv_result);
        btn_search = (Button) findViewById(R.id.btn_search);
        /** 注册一个蓝牙发现监听器 */
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryReciever, filter);

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovery();
            }
        });
    }


    /**
     * 默认蓝牙接收处理器
     */
    private final BroadcastReceiver discoveryReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ifAddressExist(device.getAddress())) {
                    return;
                }

                BluetoothDeviceContext bluetoothDeviceContext = new BluetoothDeviceContext(device.getName() == null ? device.getAddress() : device.getName(), device.getAddress());
                discoveredDevices.add(bluetoothDeviceContext);
            }
        }
    };


    /**
     * 检查蓝牙地址是否已经存在
     */
    private boolean ifAddressExist(String addr) {
        for (BluetoothDeviceContext devcie : discoveredDevices) {
            if (addr.equals(devcie.address))
                return true;
        }
        return false;
    }

    /**
     * 启动蓝牙搜索
     */
    private void startDiscovery() {
        if (bluetoothAdapter.isEnabled()) {
            if (discoveredDevices != null) {
                discoveredDevices.clear();
            }
            bStopFlag = false;
            bluetoothAdapter.startDiscovery();
            progressDialog("正在搜索");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                    }
                    synchronized (this) {
                        if (!bStopFlag) {
                            cancelDialog();
                            bluetoothAdapter.cancelDiscovery();
                            try {
                                selectBtAddrToInit();
                            } catch (Exception e) {
                                showUI("初始化蓝牙初始化失败...");
                                cancelDialog();

                            }
                            bStopFlag = true;
                        }
                    }
                }
            }).start();
        } else {

            showDialog("蓝牙未打开");
        }
    }

    private void showDialog(String s) {
        Toast.makeText(context, s, Toast.LENGTH_LONG).show();
    }

    // 弹出已配对蓝牙对话框,点击链接相应设备
    public void selectBtAddrToInit() {

        if (discoveredDevices.size() == 0) {
            showDialog("获取不到蓝牙设备");
            return;
        }
        /** 收集扫描的蓝牙信息 **/
        int i = 0;
        String[] bluetoothNames = new String[discoveredDevices.size()];
        for (BluetoothDeviceContext device : discoveredDevices) {
            bluetoothNames[i++] = device.name;
        }

        BlueToothAdapter adapter = new BlueToothAdapter(context, discoveredDevices);
        /** 弹出蓝牙对话框 **/
        final AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.activity_bluetooth_title, null);
        builder.setCustomTitle(v);
        builder.setSingleChoiceItems(adapter, 0, new AlertDialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ConnectBluetoothRequest request = new ConnectBluetoothRequest();
                request.address = discoveredDevices.get(which).address;
                SharedPreferencesUtil.put(ADDRESS, request.address);
                connectBlueTooth();

            }
        });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Dialog dialog = builder.create();
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }
                });
                dialog.show();
            }
        });

    }

    private void showUI(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_result.setText(s);
            }

        });
    }

    //断开连接
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
            unregisterReceiver(discoveryReciever);
        }

    }

    /**
     * 连接蓝牙
     */
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
                tv_result.append("\n--------------------------------------------------------------------------------");
                tv_result.append("\n连接成功");
                tv_result.append("\n--------------------------------------------------------------------------------");
                tv_result.append("\nonConnectSuccess：" + FastJsonUtils.toJson(response));


            }

            @Override
            public void onConnectError(ConnectBluetoothResponse response) {
                cancelDialog();
                Log.e("连接失败", "连接失败");
                tv_result.append("\n--------------------------------------------------------------------------------");
                tv_result.append("\n连接失败");
                tv_result.append("\n--------------------------------------------------------------------------------");
                tv_result.append("\nonConnectError：" + FastJsonUtils.toJson(response));


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