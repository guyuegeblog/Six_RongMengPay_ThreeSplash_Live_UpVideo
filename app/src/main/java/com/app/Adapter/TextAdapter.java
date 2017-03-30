package com.app.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.third.app.R;

import java.util.List;

/**
 * Created by Administrator on 2016/1/7.
 */
public class TextAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<String> dataList;

    public TextAdapter(List<String> dataList, Activity activity) {
        super();
        this.dataList = dataList;
        inflater = LayoutInflater.from(activity);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Integer.MAX_VALUE;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        try {
            return position;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder ;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.text_item, null);
            holder = new Holder();
            holder.infotextView = (TextView) convertView.findViewById(R.id.texttexttext);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        try {
            holder.infotextView.setText(dataList.get(position%dataList.size()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
        }
        return convertView;
    }

    class Holder {
        TextView infotextView;
    }

    private String getRandomNumber(int number) {
        String Random = "";
        StringBuffer stringBuffer = new StringBuffer();
        int a[] = new int[number];
        for (int i = 0; i < a.length; i++) {
            a[i] = (int) (number * (Math.random()));
            stringBuffer.append(a[i] + 1);
        }
        Random = stringBuffer.toString();
        return Random;
    }
}
