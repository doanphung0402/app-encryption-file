package com.example.project3;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;


import com.example.FIleEncryptUtils.AES_BC;
import com.example.FIleEncryptUtils.KeyStoreUtils;
import com.example.FIleEncryptUtils.MAC_BC;
import com.example.FIleEncryptUtils.Utils_BC;
import com.example.project3.UtilsEncypt.EncryptFile;
import com.obsez.android.lib.filechooser.ChooserDialog;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class EncryptFileActivity extends AppCompatActivity {
    ArrayList<FileChooserInfo> listFile = new ArrayList<>();
    ArrayList<Uri> fileChooser = new ArrayList<>();

    ListView listView;
    FileChooserAdapter fileChooserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        checkAndRequestPermissions();
        setContentView(R.layout.activity_encrypt);
        listView = (ListView) findViewById(R.id.list_view_file);
        Button btn_continue = (Button) findViewById(R.id.continue_btn_encrypt);
        if(listFile.isEmpty()){
             btn_continue.setEnabled(false);
        }
        Button btn_chooser_file = (Button) findViewById(R.id.btn_choose_file);

        btn_chooser_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Log.i("encrypt file","encypt file");
                 openDialogChooserFileMode();

            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(EncryptFileActivity.this);
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

    public void onActivityResult(int requestcode, int resultCode, Intent data) {
        ListView lv = (ListView) findViewById(R.id.list_view_file);

        super.onActivityResult(requestcode, resultCode, data);
        if (requestcode == requestcode && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            if (null != data.getClipData()) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    fileChooser.add(uri);
                }
            } else {
                Uri uri = data.getData();
                fileChooser.add(uri);
            }
            for (Uri urlFile : fileChooser) {
                String nameFile = getFileName(urlFile, getApplicationContext());
                Uri pathFile = urlFile;
                String extentionFile = getExtensionByStringHandling(nameFile).get();
                int iconFile = getImageIconFile(extentionFile);
                listFile.add(new FileChooserInfo(nameFile, pathFile, iconFile));
            }
            fileChooser.clear();
            fileChooserAdapter = new FileChooserAdapter(this, R.layout.file_chooser_info_item_activity, listFile);
            lv.setAdapter(fileChooserAdapter);
        } //end if onActiivity
    }
    private void openDialogChooserFileMode(){
        final Dialog dialog = new Dialog(this) ;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_choose_file_mode);

        Window window = dialog.getWindow();
        if(window==null){
            return ;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windownAtt = window.getAttributes();
        windownAtt.gravity = Gravity.CENTER;
        window.setAttributes(windownAtt);
        dialog.show();

        Button btn_cancer_dialog = dialog.findViewById(R.id.btn_cancer_dialog);
        Button btn_continue_dialog = dialog.findViewById(R.id.btn_continue_dialog);

        RadioButton r_a = dialog.findViewById(R.id.radio_a);  // ma hoa file
        RadioButton r_b = dialog.findViewById(R.id.radio_b);



         btn_cancer_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("cancer btn","huy");
                dialog.dismiss();
            }
        });

         btn_continue_dialog.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 dialog.dismiss();
                 if(r_a.isChecked()){
                     Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                     intent.setType(("*/*"));
                     intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                     startActivityForResult(intent, requestcode);
                 }else{
                     new ChooserDialog(EncryptFileActivity.this)
                             .withFilter(true, false)
                             .withStartFile(Environment.getExternalStorageDirectory().getPath())
                             // to handle the result(s)
                             .withChosenListener(new ChooserDialog.Result() {
                                 @Override
                                 public void onChoosePath(String path, File pathFile) {
                                     File[] listFile = pathFile.listFiles();
                                     for(int k = 0; k<listFile.length ; k++){
                                          fileChooser.add(Uri.parse(listFile[k].getPath()));
                                     }
                                 }
                             })
                             .build()
                             .show();
                 }
             }
         });
    }

    private void checkAndRequestPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(permission);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 1);
        }
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri, Context context) {
        String res = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    res = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
            if (res == null) {
                res = uri.getPath();
                int cutt = res.lastIndexOf('/');
                if (cutt != -1) {
                    res = res.substring(cutt + 1);

                }
            }
        }

        return res;
    }

    @SuppressLint("Range")


    public int getImageIconFile(String extentionFile) {
        int nameImg;
        switch (extentionFile) {
            case "pdf":
                nameImg = R.drawable.pdf_image;
                break;
            case "docx":
                nameImg = R.drawable.word_image;
                break;
            case "mp4":
                nameImg = R.drawable.mp4_image;
                break;
            case "jpg":
                nameImg = R.drawable.jpg_image;
                break;
            case "mp3":
                nameImg = R.drawable.mp3_icon;
                break;
            case "txt":
                nameImg = R.drawable.text_image;
                break;
            case "rar":
                nameImg = R.drawable.rar_icon;
                break;
            case "pptx":
                nameImg = R.drawable.pptx_image;
                break;
            default:
                nameImg = R.drawable.unknown_image;
        }

        return nameImg;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void encriptFile(View view) {
      String EXTERNAL_PATH = Environment.getExternalStorageDirectory().getPath();

           EncryptFile encrypt = new EncryptFile(listFile,getApplicationContext());
           encrypt.encriptFile();
           listFile.clear();
           finish();
           startActivity(getIntent());
           Log.i("encrypt","EncryptFileActivity done!");
        }
    }

