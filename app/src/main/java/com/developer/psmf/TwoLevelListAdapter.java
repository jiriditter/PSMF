package com.developer.psmf;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.List;

public class TwoLevelListAdapter extends BaseExpandableListAdapter {
    private Context context;
    List<String[]> data;
    String[] headers;
    ImageView ivGroupIndicator;

    public TwoLevelListAdapter(Context context, List<String[]> data, String[] headers) {
        this.context = context;
        this.data = data;
        this.headers = headers;
    }

    @Override
    public int getGroupCount() {
        return headers.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Log.d("PSMF", "klik:: groupPosition=" + groupPosition);
        String[] children = data.get(groupPosition);
        return children.length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headers[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String[] children = data.get(groupPosition);
        return children[childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.elv_second_row, null);
        TextView text = (TextView) convertView.findViewById(R.id.eventsListEventRowText);
        String groupText = getGroup(groupPosition).toString();
        text.setText(groupText);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.elv_third_row, null);
        TextView text = (TextView) convertView.findViewById(R.id.eventsListEventRowText);
        String[] childArray = data.get(groupPosition);
        String txt = childArray[childPosition];
        text.setText(txt);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Klik: " + getChild(groupPosition, childPosition), Toast.LENGTH_SHORT).show();
                HTTP_teamDetails wwwTD = new HTTP_teamDetails(context);
                    String s = (String)getChild(groupPosition, childPosition);
                    s = Uri.encode(s);
                    //URLEncoder.encode(s, "utf-8");
                    wwwTD.execute(s);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
