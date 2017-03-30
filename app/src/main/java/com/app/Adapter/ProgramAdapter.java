package com.app.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.Model.ProgramInfo;
import com.third.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/7.
 */
public class ProgramAdapter extends BaseAdapter {
    private List<ProgramInfo> list = new ArrayList<>();
    private LayoutInflater inflater;

    public ProgramAdapter(Activity context) {
        this.inflater = LayoutInflater.from(context);
    }

    public List<ProgramInfo> getList() {
        return list;
    }

    public void setList(List<ProgramInfo> list) {
        this.list = list;
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
        ThreeViewHolder indexViewHolder = null;
        if (convertView == null) {
            indexViewHolder = new ThreeViewHolder();
            convertView = inflater.inflate(R.layout.video_list_item, null);
            indexViewHolder.time = (TextView) convertView.findViewById(R.id.time);
            indexViewHolder.video_name = (TextView) convertView.findViewById(R.id.video_name);
            convertView.setTag(indexViewHolder);
        } else {
            indexViewHolder = (ThreeViewHolder) convertView.getTag();
        }
        setDataToUI(convertView, indexViewHolder, parent, position);
        return convertView;
    }

    private void setDataToUI(View convertView, ThreeViewHolder indexViewHolder, ViewGroup parent, int position) {

        final ProgramInfo info = list.get(position);
        indexViewHolder.time.setText(info.getTime());
        indexViewHolder.video_name.setText(info.getInfo());
    }


    public class ThreeViewHolder {
        TextView time;
        TextView video_name;
    }
}
