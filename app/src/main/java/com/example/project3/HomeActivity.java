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
        ArrayList<Uri> fileChooser = new ArrayList<>();
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
                     fileChooser.add(uri);
                 }

             }else{
                  Uri uri = data.getData();
                  fileChooser.add(uri);
             }
             for (Uri c : fileChooser){
                 Toast.makeText(this, "file choose: "+c , Toast.LENGTH_SHORT).show();
                 System.out.println("file : "+c);
             }
//              Uri uri = data.getData();
//              String nameFile =getFileName(uri,getApplicationContext());
//              String urlFile = uri.getPath();
//              String extentionFile  = getExtensionByStringHandling(nameFile).get();
                listFile =new ArrayList<>();
                for(Uri urlFile : fileChooser){
                     String nameFile = getFileName(urlFile,getApplicationContext());
                     String pathFile = urlFile.getPath();
                     String extentionFile = getExtensionByStringHandling(nameFile).get();
                     listFile.add(new FileChooserInfo(nameFile,))
                }
//              System.out.println("name file "+ nameFile+",extention file : "+extentionFile);
//
//              listFile.add(new FileChooserInfo(nameFile,urlFile,nameImg));
//              fileChooserAdapter = new FileChooserAdapter(this,R.layout.file_chooser_info_item_activity,listFile);
//              lv.setAdapter(fileChooserAdapter);
//
//              Toast.makeText(context, uri.getPath(), Toast.LENGTH_SHORT).show();

         } //end if onActiivity
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
    public int getImageIconFile(String urlFile){


        return 0;

    }

}