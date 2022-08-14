package com.example.project3;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;


import com.dustinredmond.BCrypt;
import com.example.FIleEncryptUtils.AES_BC;
import com.example.FIleEncryptUtils.MAC_BC;
import com.example.FIleEncryptUtils.Utils_BC;
import com.example.project3.Utils.User;
import com.example.project3.Utils.UserLocalStore;
import com.obsez.android.lib.filechooser.ChooserDialog;



import java.io.File;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

public class EncryptFileActivity extends AppCompatActivity {
    ArrayList<FileChooserInfo> listFile = new ArrayList<>();
    ArrayList<Uri> fileChooser = new ArrayList<>();
    UserLocalStore userLocalStore;
    ListView listView;
    private static final String INTERNAL_PATH = Environment.getDataDirectory().getPath();
    private static final String EXTERNAL_PATH = Environment.getExternalStorageDirectory().getPath();
    FileChooserAdapter fileChooserAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        checkAndRequestPermissions();
        setContentView(R.layout.activity_encrypt);
        listView = (ListView) findViewById(R.id.list_view_file);
        Button btn_continue = (Button) findViewById(R.id.continue_btn_encrypt);
        Button btn_chooser_file = (Button) findViewById(R.id.btn_choose_file);
        userLocalStore = new UserLocalStore(this);
        btn_chooser_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("encrypt file", "encypt file");
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
        Button btn_encrypt = findViewById(R.id.continue_btn_encrypt);
        super.onActivityResult(requestcode, resultCode, data);
        if (requestcode == requestcode && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            if (null != data.getClipData()) {

                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    String uriString = UriUtils.getPathFromUri(getApplicationContext(),uri);
                    fileChooser.add(Uri.parse(uriString));
                }
            } else {
                Uri uri = data.getData();
                String uriString = UriUtils.getPathFromUri(getApplicationContext(),uri);
                fileChooser.add(Uri.parse(uriString));
            }
            if(!fileChooser.isEmpty()){
                btn_encrypt.setVisibility(View.VISIBLE);
            }
            for (Uri urlFile : fileChooser) {
                String nameFile = getFileNameByUri(urlFile);
                Log.i("name file line 158",nameFile);
                Uri pathFile = urlFile;
                String extentionFile = getExtensionByStringHandling(nameFile).get();
                int iconFile = getImageIconFile(extentionFile);
                listFile.add(new FileChooserInfo(nameFile, pathFile, iconFile));
            }
            fileChooser.clear();
            fileChooserAdapter = new FileChooserAdapter(this, R.layout.file_chooser_info_item_activity, listFile);
            lv.setAdapter(fileChooserAdapter);
            fileChooser.clear();
        } //end if onActiivity
    }


    private void logout() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Uri> encriptFile(ArrayList<FileChooserInfo> listFile) {

        String pathfile = EXTERNAL_PATH + "/Android/data/com.example.project3";
        File intermediateFolder = new File(pathfile, "folder.intermediate");
        if (!intermediateFolder.exists()) {
            intermediateFolder.mkdir();
        }
        File file = new File(EXTERNAL_PATH + "/Android/data/com.example.project3", "file_encript");
        if (!file.exists()) {
            file.mkdir();
        }
        int position = 0;
        ArrayList<Uri> ErrorUriFile = new ArrayList<>();

        while (position < listFile.size()) {
            Log.i("list size", String.valueOf(listFile.size()));
            Uri uriFile = listFile.get(position).getUrlFile();
            File fileOrigin = new File(String.valueOf(uriFile));

            int sizeBlock = 102400;
            AES_BC aes_bc = new AES_BC((int) sizeBlock);
            if (fileOrigin.exists()) {
                Log.i("uri file :", fileOrigin.getAbsolutePath());
                String fileNameOrigin = listFile.get(position).getNameFile();
                try {
                    File encrypCBCFile = new File(pathfile + "/folder.intermediate", fileNameOrigin + ".encrypt");
                    File hMacFile = new File(pathfile + "/folder.intermediate", fileNameOrigin + ".hmac");
                    File encryptFileTotal = new File(EXTERNAL_PATH + "/Android/data/com.example.project3/file_encript",
                            fileNameOrigin);
                    if (!encryptFileTotal.exists()) {
                        Log.i("file created", "file_encrypt total created !");
                        encryptFileTotal.createNewFile();
                    }
                    if (!hMacFile.exists()) {
                        Log.i("file created", "file hmac created !");
                        hMacFile.createNewFile();
                    }
                    if (!encrypCBCFile.exists()) {
                        Log.i("file created", "file encrypt intermediate created !");
                        encrypCBCFile.createNewFile();
                    }
                    userLocalStore = new UserLocalStore(getApplicationContext());
                    String key = userLocalStore.getUser().username+fileOrigin.getName()+"_KEY";
                    Log.i("secret key encrypt",key);
                    aes_bc.createKey(key);
                    SecretKey secretKey = aes_bc.getKey(key);
                    if (secretKey == null) {
                        Log.i("key", "key not found");
                        ErrorUriFile.add(uriFile);
                    }
                    MAC_BC mac_bc = new MAC_BC(sizeBlock);
                    byte[] iv = aes_bc.encriptFile(secretKey, fileOrigin, encrypCBCFile);
                    mac_bc.encryptFileWithHmac(secretKey, encrypCBCFile, hMacFile);
                    Utils_BC utils_bc = new Utils_BC();
                    utils_bc.createFileProtected(encrypCBCFile, hMacFile, iv, encryptFileTotal,sizeBlock);
                    encrypCBCFile.delete();      hMacFile.delete() ;
                    Log.i("done", "done encrypt!");
                } catch (Exception e) {
                    ErrorUriFile.add(uriFile);
                    e.printStackTrace();
                }
            } else {
                ErrorUriFile.add(uriFile); // insert uri not exist
            }
            boolean stDelete = fileOrigin.delete();

            Log.i("size error file", String.valueOf(ErrorUriFile.size()));
            Log.e("status delete", String.valueOf(stDelete));
            position = position +1 ;
        }

        Toast.makeText(this, "Mã hóa hoàn tất ", Toast.LENGTH_SHORT).show();
        return ErrorUriFile ;

    }


    private void openLoading(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_reloading);
        Window window = dialog.getWindow();
        if (window == null) {
            logout();
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windownAtt = window.getAttributes();
        windownAtt.gravity = Gravity.CENTER;
        window.setAttributes(windownAtt);
        dialog.show();
    }

    private void openDialogAuthPass() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_auth_password);
        Window window = dialog.getWindow();
        if (window == null) {
            logout();
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windownAtt = window.getAttributes();
        windownAtt.gravity = Gravity.CENTER;
        window.setAttributes(windownAtt);
        dialog.show();

        EditText passAuthEt = (EditText) dialog.findViewById(R.id.password_auth);
        Button btn_auth = (Button) dialog.findViewById(R.id.btn_continue_dialog_auth);
        User user = userLocalStore.getUser();
        btn_auth.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                String passAuth = passAuthEt.getText().toString();
                if (passAuth.isEmpty()) {
                    Toast.makeText(EncryptFileActivity.this, "Nhập mật khẩu", Toast.LENGTH_SHORT).show();
                } else {
                    String passwordEncode = user.getPassword();
                    boolean checkLogin = userLocalStore.checkLoggedIn();
                    if (checkLogin == true && checkPass(passAuth, passwordEncode) == true) {
                        //encrypt file
                        Log.i("size list file", String.valueOf(listFile.size()));
                        ArrayList<Uri> listFileError =new ArrayList<Uri>();
                        listFileError = encriptFile(listFile); //encrypt file,
                        listFile.clear();
                        if (listFileError.isEmpty()){
                            Toast.makeText(getApplicationContext(),"Mã hóa thành công",Toast.LENGTH_SHORT).show();
                        }else{
                            for (Uri urlFile : listFileError) {
                                String nameFile = getFileName(urlFile, getApplicationContext());
                                Uri pathFile = urlFile;
                                String extentionFile = getExtensionByStringHandling(nameFile).get();
                                int iconFile = getImageIconFile(extentionFile);
                                listFile.add(new FileChooserInfo(nameFile, pathFile, iconFile));
                            }
                            listFileError.clear();
                        }
                        startActivity(getIntent());
                    } else {
                        Toast.makeText(getApplicationContext(), "Có lỗi", Toast.LENGTH_SHORT).show();
                        listFile.clear();
                        logout();
                    }
                }
            }
        });
    }
    public boolean checkPass(String clearTextPassword, String hashedPass) {
        return BCrypt.checkpw(clearTextPassword, hashedPass);
    }
    private void openDialogChooserFileMode() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_choose_file_mode);

        Window window = dialog.getWindow();
        if (window == null) {
            return ;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
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
                dialog.dismiss();
            }
        });

        btn_continue_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (r_a.isChecked() == true) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType(("*/*"));
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    startActivityForResult(intent, requestcode);
                } else {
                    new ChooserDialog(EncryptFileActivity.this)
                            .withFilter(true, false)
                            .withStartFile(Environment.getExternalStorageDirectory().getPath())

                            .withChosenListener(new ChooserDialog.Result() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onChoosePath(String path, File pathFile) {
                                    File file = new File(path + "/");
                                    File[] fileList = file.listFiles();
                                    ListView lv = (ListView) findViewById(R.id.list_view_file);
                                    for (int k = 0; k < fileList.length; k++) {
                                        fileChooser.add(Uri.parse(fileList[k].getAbsolutePath()));
                                    }
                                    if(!fileChooser.isEmpty()){
                                        Button btn_encypt = findViewById(R.id.continue_btn_encrypt);
                                        btn_encypt.setVisibility(View.VISIBLE);
                                    }
                                    for (Uri urlFile : fileChooser) {
                                        Log.i("uri file", String.valueOf(urlFile));
                                        String nameFile = getFileNameByUri(urlFile);
                                        Log.i("name file", nameFile);
                                        String extentionFile = getExtensionByStringHandling(nameFile).get();
                                        int iconFile = getImageIconFile(extentionFile);
                                        listFile.add(new FileChooserInfo(nameFile, urlFile, iconFile));
                                    }
                                    fileChooserAdapter = new FileChooserAdapter(getApplicationContext(), R.layout.file_chooser_info_item_activity, listFile);
                                    lv.setAdapter(fileChooserAdapter);
                                    fileChooser.clear();
                                }
                            })
                            .build()
                            .show();
                }
            }
        });
    }
    public String getFileNameByUri(Uri uriFile) {
        String uriFileString = String.valueOf(uriFile);
        int totalCharacters = 0;
        return uriFileString.substring(uriFileString.lastIndexOf("/") + 1);
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
        if (listFile.isEmpty()) {
            Toast.makeText(this, "Danh sách mã hóa trống", Toast.LENGTH_SHORT).show();
        } else {
            openDialogAuthPass();
        }
    }
}
