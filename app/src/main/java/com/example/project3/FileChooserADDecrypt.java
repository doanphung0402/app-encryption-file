package com.example.project3;

import android.content.Context;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

public class FileChooserADDecrypt extends BaseAdapter {

    public ArrayList<Integer>listFileEncrypt ;

    public FileChooserADDecrypt(ArrayList<Integer> listFileEncrypt, Context context, int layout, List<FileChooserInfo> listFileInfo) {
        this.listFileEncrypt = listFileEncrypt;
        this.context = context;
        this.layout = layout;
        this.listFileInfo = listFileInfo;
    }

    Context context ;
    int layout ;
    List<FileChooserInfo> listFileInfo;

    public FileChooserADDecrypt(Context context, int layout, List<FileChooserInfo> listFileInfo) {
        this.context = context;
        this.layout = layout;
        this.listFileInfo = listFileInfo;
    }

    public FileChooserADDecrypt() {

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
    public int getItemViewType(int position) {

        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //anh xa
        convertView = inflater.inflate(layout,null);
        Log.v("ConvertView", String.valueOf(position));
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_meat);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value=String.valueOf(position);
                Intent i = new Intent("file-position");
                i.putExtra("position",value);
                LocalBroadcastManager.getInstance(context).sendBroadcast(i);
            }
        });

        ImageView img_icon_file = (ImageView) convertView.findViewById(R.id.img_icon_file1);
        TextView textview_filename = (TextView) convertView.findViewById(R.id.textview_filename1);
        TextView textview_uri = convertView.findViewById(R.id.textview_uri1);

        //gan gia tri
        String fileName = listFileInfo.get(position).getNameFile();
        textview_filename.setText(fileName.substring(fileName.indexOf("/")+1));
        img_icon_file.setImageResource(listFileInfo.get(position).getIconFile());
        textview_uri.setText(listFileInfo.get(position).getUrlFile().getPath());
        return convertView ;
    }
}
