package com.umpay.payplugindemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.umpay.payplugin.UMPay;
import com.umpay.payplugin.callback.BasePrintCallback;
import com.umpay.payplugin.handle.PrintUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 打印
 */
public class PrintActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PrintActivity";
    private Button bt_text, bt_two, bt_one, bt_line;
    //    private String imagePATH;
    private ImageView iv;
    private TextView tv_error;
    private Spinner spinner, location_spinner;
    private ArrayAdapter<String> arr_adapter;
    private ArrayAdapter<String> loction_adapter;
    private List<String> data_list;
    private List<String> location_list;
    private String fontType = "simsun.ttc";
    private int location = 1;
    private EditText et_space, et_two, et_one, bt_text_size, edt_text, et_height;
    private Button bt_content;
    int space = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        setToolBar();
        findView();
        init();
        saveBitmap();
    }

    private void init() {
        //数据
        data_list = new ArrayList<>();
        data_list.add("simsun.ttc");
        data_list.add("Roboto-ThinItalic.ttf");
        data_list.add("Roboto-Thin.ttf");
        data_list.add("Roboto-Regular.ttf");
        data_list.add("Roboto-LightItalic.ttf");
        data_list.add("Roboto-Light.ttf");
        data_list.add("Roboto-Italic.ttf");
        data_list.add("RobotoCondensed-Regular.ttf");
        data_list.add("RobotoCondensed-Italic.ttf");
        data_list.add("RobotoCondensed-BoldItalic.ttf");
        data_list.add("RobotoCondensed-Bold.ttf");
        data_list.add("Roboto-BoldItalic.ttf");
        data_list.add("Roboto-Bold.ttf");
        data_list.add("Padauk-bookbold.ttf");
        data_list.add("Padauk-book.ttf");
        data_list.add("NotoSansThaiUI-Regular.ttf");
        data_list.add("NotoSansThaiUI-Bold.ttf");
        data_list.add("NotoSansThai-Regular.ttf");
        data_list.add("NotoSansThai-Bold.ttf");
        data_list.add("NotoSansTeluguUI-Regular.ttf");
        data_list.add("NotoSansTeluguUI-Bold.ttf");
        data_list.add("NotoSansTelugu-Regular.ttf");

        data_list.add("NotoSansTelugu-Bold.ttf");
        data_list.add("NotoSansTamilUI-Regular.ttf");
        data_list.add("NotoSansTamilUI-Bold.ttf");
        data_list.add("NotoSansTamil-Regular.ttf");
        data_list.add("NotoSansTamil-Bold.ttf");
        data_list.add("NotoSansSymbols-Regular.ttf");

        data_list.add("NotoSansMalayalamUI-Regular.ttf");
        data_list.add("NotoSansMalayalamUI-Bold.ttf");
        data_list.add("NotoSansMalayalam-Regular.ttf");
        data_list.add("NotoSansMalayalam-Bold.ttf");
        data_list.add("NotoSansLaoUI-Regular.ttf");
        data_list.add("NotoSansLaoUI-Bold.ttf");

        data_list.add("NotoSansLao-Regular.ttf");
        data_list.add("NotoSansLao-Bold.ttf");
        data_list.add("NotoSansKhmerUI-Bold.ttf");
        data_list.add("NotoSansKhmer-Regular.ttf");
        data_list.add("NotoSansKhmer-Bold.ttf");
        data_list.add("NotoSansKannadaUI-Regular.ttf");

        data_list.add("NotoSansKannadaUI-Bold.ttf");
        data_list.add("NotoSansKannada-Regular.ttf");
        data_list.add("MTLmr3m.ttf");
        data_list.add("DroidSerif-Regular.ttf");
        data_list.add("DroidSerif-Italic.ttf");
        data_list.add("DroidSerif-Bold.ttf");
        data_list.add("DroidSansMono.ttf");

        data_list.add("DroidSansGeorgian.ttf");
        data_list.add("DroidSansFallback.ttf");
        data_list.add("DroidSansEthiopic-Regular.ttf");
        data_list.add("DroidSansArmenian.ttf");
        data_list.add("DroidNaskhUI-Regular.ttf");
        data_list.add("DroidNaskh-Regular.ttf");


        //适配器
        arr_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                fontType = (String) spinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
        location_list = new ArrayList<>();
        location_list.add("left");
        location_list.add("center");
        location_list.add("right");
        //适配器
        loction_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, location_list);
        //设置样式
        loction_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        location_spinner.setAdapter(loction_adapter);

        location_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                location = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub

            }
        });
    }

    public void saveBitmap() {

        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
        Log.e(TAG, "保存图片");
//        String path = Environment.getExternalStorageDirectory().getPath() + "/wk/" + "jpg1.bmp";
//            File f = new File(Environment.getExternalStorageDirectory()+"/wk/","jpg1.bmp");
//        File sdCardDir = Environment.getExternalStorageDirectory();
//        File sdFile = new File(sdCardDir, "jpg1.bmp");
//        imagePATH = sdFile.getAbsolutePath();
//        Log.e("TAG", "imagePATH=" + imagePATH);
//            if (f.exists()) {
//                f.delete();
//            }
//        try {
//            FileOutputStream out = new FileOutputStream(sdFile);
//            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
//            out.flush();
//            out.close();
//            Log.i(TAG, "已经保存");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private void findView() {
        et_space = (EditText) findViewById(R.id.et_space);
        et_two = (EditText) findViewById(R.id.et_two);
        et_one = (EditText) findViewById(R.id.et_size);
        et_height = (EditText) findViewById(R.id.et_height);
        spinner = (Spinner) findViewById(R.id.spinner2);
        location_spinner = (Spinner) findViewById(R.id.location_spinner);
        bt_text = (Button) findViewById(R.id.bt_text);
        bt_line = (Button) findViewById(R.id.bt_line);
        bt_one = (Button) findViewById(R.id.bt_one);
        bt_two = (Button) findViewById(R.id.bt_two);
        bt_content = (Button) findViewById(R.id.bt_content);
        bt_text_size = (EditText) findViewById(R.id.bt_text_size);
        edt_text = (EditText) findViewById(R.id.edt_text);
        iv = (ImageView) findViewById(R.id.iv);
        tv_error = (TextView) findViewById(R.id.tv_error);
        bt_two.setOnClickListener(this);
        bt_one.setOnClickListener(this);
        bt_text.setOnClickListener(this);
        bt_line.setOnClickListener(this);
        bt_content.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_text:
                //打印文字
                print(printText());
                break;
            case R.id.bt_line:
                //分割线
                print(printline());
                break;
            case R.id.bt_one:
                //一维码
                print(printone());
                break;
            case R.id.bt_two:
                //二维码
                print(printTwo());
                break;
            case R.id.bt_content:
                //打印复杂内容
                JSONObject jsonObject = printContent();
                print(jsonObject);
                break;
        }


    }

    /**
     * 打印二维码
     *
     * @return
     */
    private JSONObject printTwo() {
        String a = et_two.getText().toString();
        int TWO = 6;
        if (a == null || a.equals("")) {
            TWO = 6;
        } else {
            TWO = Integer.parseInt(a);
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("spos", new JSONArray().put(PrintUtils.setTwoDimension("www.baidu.com", location, TWO)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;

    }

    /**
     * 打印一维码
     *
     * @return
     */
    private JSONObject printone() {
        JSONObject jsonObject = new JSONObject();
        String width = et_one.getText().toString();
        String height = et_height.getText().toString();
        int oneWidth = 3;
        int oneHeight = 3;
        if (width == null || width.equals("")) {
            oneWidth = 3;
        } else {
            oneWidth = Integer.parseInt(width);
        }
        if (height == null || height.equals("")) {
            oneHeight = 3;
        } else {
            oneHeight = Integer.parseInt(height);
        }
        try {
            jsonObject.put("spos", new JSONArray().put(PrintUtils.setOneDimension("www.baidu.com", location, oneHeight, oneWidth)));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * 打印分割线
     *
     * @return
     */
    private JSONObject printline() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("spos", new JSONArray().put(PrintUtils.setLine()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * 打印文字
     *
     * @return
     */
    private JSONObject printText() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        String text = edt_text.getText().toString();
        int textSize = 2;
        if (text.equals("")) {
            text = "打印测试打印测试";
        }

        if (!bt_text_size.getText().toString().equals("")) {
            textSize = Integer.valueOf(bt_text_size.getText().toString());
        }
        try {
            //打印文字
            jsonArray.put(PrintUtils.setStringContent(text, location, textSize));
            jsonObject.put("spos", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

    }

    /**
     * 复杂内容
     *
     * @return
     */

    private JSONObject printContent() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        try {
            //打印文字
            jsonArray.put(PrintUtils.setStringContent("打印测试--1", 1, 1));
//            jsonArray.put(PrintUtils.setbitmap(1));
            jsonArray.put(PrintUtils.setStringContent("打印测试--2", 1, 2));
            jsonArray.put(PrintUtils.setStringContent("下面是分割线--", 1, 3));
            //打印空行
            jsonArray.put(PrintUtils.setStringContent("--------------------------------", 2, 4));
            jsonArray.put(PrintUtils.setStringContent("打印测试--4", 1, 5));
            jsonArray.put(PrintUtils.setStringContent("打印测试--5", 1, 6));
            jsonArray.put(PrintUtils.setStringContent("", 1, 6));
            jsonArray.put(PrintUtils.setOneDimension("WWW.BAIDU.COM", 2, 2, 2));
            jsonArray.put(PrintUtils.setTwoDimension("123456789", 2, 6));
            jsonObject.put("spos", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;

    }

    public void print(JSONObject jsonObject) {

        String a = et_space.getText().toString();
        if (a == null || a.equals("")) {
            space = 4;
        } else {
            space = Integer.parseInt(a);
        }

        String ss=  jsonObject.toString();
        UMPay.getInstance().print(ss, "", fontType, space, new BasePrintCallback() {
                    @Override
                    public void onReBind(int code, String msg) {
                        cancelDialog();
                        reBind(code, msg);
                    }

                    @Override
                    public void onStart() {
                        Log.e(TAG, "onStart");
                        tv_error.setText("");
                    }

                    @Override
                    public void onFinish() {
                        Log.e(TAG, "onFinish");
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.e(TAG, "onError code=" + i + "msg=" + s);
                        tv_error.setText(s);
                    }

                }
        );
    }

}
