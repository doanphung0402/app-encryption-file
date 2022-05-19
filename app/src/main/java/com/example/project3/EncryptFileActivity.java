package com.example.project3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EncryptFileActivity extends AppCompatActivity {
    ArrayList<FileChooserInfo> listFile =new ArrayList<>() ;
    ArrayList<Uri> fileChooser = new ArrayList<>();
    Context context ;
    ListView listView ;
    FileChooserAdapter fileChooserAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);
        listView =(ListView) findViewById(R.id.list_view_file);
        Button btn_continue = (Button) findViewById(R.id.continue_btn_encrypt);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlertDialog.Builder dialog  = new AlertDialog.Builder(EncryptFileActivity.this);
                dialog.setTitle("Xác nhận ");
                dialog.setMessage("Bạn có đồng ý xóa không ? ");
                dialog.setPositiveButton("Đồng ý ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listFile.remove(position);
                        fileChooserAdapter.notifyDataSetChanged();
                    }
                });
                dialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
                return false;
            }
        });
    }
    @Override
    public void onContentChanged() {
        super.onContentChanged();

        View empty = findViewById(R.id.empty);
        ListView list = (ListView) findViewById(R.id.list_view_file);
        list.setEmptyView(empty);
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


                for(Uri urlFile : fileChooser){
                     String nameFile = getFileName(urlFile,getApplicationContext());
                     String pathFile = urlFile.getPath();
                     String extentionFile = getExtensionByStringHandling(nameFile).get();
                     int iconFile = getImageIconFile(extentionFile);
                     listFile.add(new FileChooserInfo(nameFile,pathFile,iconFile));
                }
             fileChooser.clear();
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
                     nameImg = R.drawable.pdf_image;
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
                 case "rar" :
                     nameImg = R.drawable.rar_icon ;
                     break;
                 case "pptx" :
                     nameImg = R.drawable.pptx_image ;
                     break;
                 default:
                     nameImg = R.drawable.unknown_image ;
             }

        return nameImg;

    }
    public void encriptFile(View view){
        int size = listFile.size();
//        Toast.makeText(this,"hello"+size, Toast.LENGTH_SHORT).show();
        List<Uri> uriFileErrorExcute = new ArrayList<>();
        Uri uriFile = Uri.parse(listFile.get(0).getUrlFile());
        Log.i("size uri list : ", String.valueOf(listFile.size()));
        File originFile = new File(String.valueOf(getExternalFilesDir());
        Log.i("uri file origin  ",originFile.getPath());
        Log.i("uri file: ", String.valueOf(uriFile));
        if (originFile.exists()==true){
            ParcelFileDescriptor pfd;
            try {
                pfd =getContentResolver().openFileDescriptor(Uri.parse(uriFile.getPath()), "r");
                FileInputStream fileInputStream =
                        new FileInputStream(pfd.getFileDescriptor());
                FileOutputStream fileOutputStream = new FileOutputStream(originFile);

               byte[] bufferInputFile =new byte[64];
               while(fileInputStream.read(bufferInputFile)!=-1){
                    fileOutputStream.write(bufferInputFile);
               }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            uriFileErrorExcute.add(uriFile);
            Toast.makeText(this, "no", Toast.LENGTH_SHORT).show();
        }
    }




}