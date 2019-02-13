package com.umpay.payplugindemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.umpay.bean.BluetoothDeviceContext;

import java.util.List;

/**
 * 作者:$ dxn
 * 时间:2018/8/20 14:59
 * 描述:
 */

public class BlueToothAdapter extends BaseAdapter {

    public List<BluetoothDeviceContext> list;
    LayoutInflater inflater;

    public BlueToothAdapter(Context context, List<BluetoothDeviceContext> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.adapter_bluetooth_item, null);
            holder.tv_address = (TextView) convertView.findViewById(R.id.tv_address);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_address.setText(list.get(position).name);
        return convertView;
    }

    public static class ViewHolder {
        public TextView tv_address;

    }
}
