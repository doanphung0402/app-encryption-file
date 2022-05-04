package com.example.project3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

public class FileChooserAdapter extends BaseAdapter {


    Context context ;
    int layout ;
    List<FileChooserInfo> listFileInfo;

    public FileChooserAdapter(Context context, int layout, List<FileChooserInfo> listFileInfo) {
        this.context = context;
        this.layout = layout;
        this.listFileInfo = listFileInfo;
    }

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<FileChooserInfo> getListFileInfo() {
        return listFileInfo;
    }

    public void setListFileInfo(List<FileChooserInfo> listFileInfo) {
        this.listFileInfo = listFileInfo;
    }

    @Override
    public int getCount() {
        return listFileInfo.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //anh xa
        convertView = inflater.inflate(layout,null);
        ImageView img_icon_file = (ImageView) convertView.findViewById(R.id.img_icon_file);
        TextView textview_filename = (TextView) convertView.findViewById(R.id.textview_filename);

        //gan gia tri
         textview_filename.setText(listFileInfo.get(position).getNameFile());
         img_icon_file.setImageResource(listFileInfo.get(position).getIconFile());
         return convertView ;
    }
}
