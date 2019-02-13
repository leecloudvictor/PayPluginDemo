package com.umpay.payplugindemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.callback.UMBindCallBack;


/**
 * Created by tianxiaoyang on 2016/10/24.
 */
public class BaseActivity extends AppCompatActivity {


    protected BaseActivity context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }

    private AlertDialog dialog;


    protected void progressDialog(String title) {
        if (dialog == null) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setCancelable(false)
                    .setView(R.layout.custom_view)
                    .show();
        } else {
            dialog.setTitle(title);
            if (!dialog.isShowing() && !isFinishing()) {
                dialog.show();
            }
        }
    }

    protected void cancelDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    protected void setToolBar() {
        setToolBar("支付插件");
    }

    protected void setToolBar(String string) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle(string);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }


    //简写
    protected void reBind(int code, String msg) {
        new AlertDialog.Builder(this).setTitle("提示").setMessage(msg + "(" + code + ")")
                .setNegativeButton("重新绑定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UMPay.getInstance().bind(getApplicationContext(), new UMBindCallBack() {
                            @Override
                            public void bindException(Exception ee) {
                                Log.e("123", ee.getMessage());
                            }

                            @Override
                            public void bindSuccess() {
                                Log.e("123", "绑定成功");
                            }

                            @Override
                            public void bindDisconnected() {
                                Log.e("123", "断开绑定");
                            }
                        });
                        finish();

                    }
                }).setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).create().show();
    }
}

