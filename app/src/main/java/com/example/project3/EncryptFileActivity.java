package com.example.project3;

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
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Optional;

public class EncryptFileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);

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

                listFile =new ArrayList<>();
                for(Uri urlFile : fileChooser){
                     String nameFile = getFileName(urlFile,getApplicationContext());
                     String pathFile = urlFile.getPath();
                     String extentionFile = getExtensionByStringHandling(nameFile).get();
                     int iconFile = getImageIconFile(extentionFile);
                     listFile.add(new FileChooserInfo(nameFile,pathFile,iconFile));
                }

              fileChooserAdapter = new FileChooserAdapter(this,R.layout.file_chooser_info_item_activity,listFile);
              lv.setAdapter(fileChooserAdapter);
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
    public int getImageIconFile(String extentionFile){
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

        return nameImg;

    }

}