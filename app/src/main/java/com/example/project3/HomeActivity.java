package com.example.project3;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HomeActivity extends AppCompatActivity {
    ImageView imgview = (ImageView) findViewById(R.id.img_openfile);

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imgview.getVisibility() == View.VISIBLE) {
            outState.putBoolean("reply_visible", true);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

    }
    int requestcode = 1;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)

    public void  onActivityResult(int requestcode, int resultCode, Intent data){
        ListView lv = (ListView) findViewById(R.id.list_view_file);
        ArrayList<FileChooserInfo> listFile ;
        ArrayList<String> fileChooser = new ArrayList<>();
        FileChooserAdapter fileChooserAdapter ;
        Context context = getApplicationContext();
        super.onActivityResult(requestcode,resultCode,data);
         if(requestcode == requestcode && resultCode == Activity.RESULT_OK){
              if(data == null){
                   return ;

              }
             if(null != data.getClipData()){
                 for (int i =0 ; i<data.getClipData().getItemCount(); i++){
                     Uri uri = data.getClipData().getItemAt(i).getUri();
                     fileChooser.add(uri.getPath());
                 }
             }
              Uri uri = data.getData();
              String nameFile =getFileName(uri,getApplicationContext());
              String urlFile = uri.getPath();
              String extentionFile  = getExtensionByStringHandling(nameFile).get();

              int nameImg ;
             switch(extentionFile) {
                 case "pdf":
                     nameImg = R.drawable.dpf_image ;
                     break;
                 case "docx" :
                     nameImg = R.drawable.word_image ;
                     break;
                 case "mp4" :
                     nameImg = R.drawable.mp4_image ;break;
                 case "jpg" :
                     nameImg = R.drawable.jpg_image ;break;
                 case "mp3" :
                     nameImg = R.drawable.mp3_icon ;
                     break;
                 case "txt" :
                     nameImg = R.drawable.text_image ;
                     break;

                 default:
                     nameImg = R.drawable.unknown_image ;
             }
              System.out.println("name file "+ nameFile+",extention file : "+extentionFile);
              listFile =new ArrayList<>();
              listFile.add(new FileChooserInfo(nameFile,urlFile,nameImg));
              fileChooserAdapter = new FileChooserAdapter(this,R.layout.file_chooser_info_item_activity,listFile);
              lv.setAdapter(fileChooserAdapter);

              Toast.makeText(context, uri.getPath(), Toast.LENGTH_SHORT).show();

         }
    }
    public void openFileChooser(View view){
         Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
         intent.setType(("*/*"));
         intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
         startActivityForResult(intent,requestcode);
    }



    @SuppressLint("Range")
    public String getFileName(Uri uri , Context context){
         String res = null ;
         if(uri.getScheme().equals("content")){
             Cursor cursor = context.getContentResolver().query(uri , null ,null,null,null );
             try {
                 if(cursor!=null && cursor.moveToFirst()){
                      res =cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                 }
             }finally {
                 cursor.close();
             }
              if(res ==null){
                   res =uri.getPath();
                   int cutt = res.lastIndexOf('/');
                   if(cutt != -1){
                        res = res.substring(cutt +1);

                   }
              }
         }

                 return res;
    }

    public



}