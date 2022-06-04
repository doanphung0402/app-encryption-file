package com.example.project3.UtilsEncypt;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.FIleEncryptUtils.AES_BC;
import com.example.FIleEncryptUtils.KeyStoreUtils;
import com.example.FIleEncryptUtils.MAC_BC;
import com.example.FIleEncryptUtils.Utils_BC;
import com.example.FIleEncryptUtils.Utils_Function;
import com.example.project3.EncryptFileActivity;
import com.example.project3.FileChooserInfo;
import com.example.project3.UriUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.Security;
import java.util.ArrayList;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptFile {
    private static final String INTERNAL_PATH = Environment.getDataDirectory().getPath();
    private static final String EXTERNAL_PATH = Environment.getExternalStorageDirectory().getPath();
    public ArrayList<FileChooserInfo> listFile;
    public Context context;

    public EncryptFile(ArrayList<FileChooserInfo> listFile, Context context) {
        this.listFile = listFile;
        this.context = context;
    }

    public ArrayList<FileChooserInfo> getListFile() {
        return listFile;
    }

    public void setListFile(ArrayList<FileChooserInfo> listFile) {
        this.listFile = listFile;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public void encriptFile() {
        AES_BC aes_bc = new AES_BC();
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
        while (position < listFile.size()) {
            Log.i("list size", String.valueOf(listFile.size()));
            Uri uriFile = listFile.get(position).getUrlFile();
            ArrayList<Uri> ErrorUriFile = new ArrayList<>();
            File fileOrigin = new File(UriUtils.getPathFromUri(context, uriFile));
            if (fileOrigin.exists()) {
                Log.i("uri file :", fileOrigin.getAbsolutePath());
                String fileNameOrigin = listFile.get(position).getNameFile();
                String extendtionFile = EncryptFileActivity.getExtensionByStringHandling(fileNameOrigin).get();
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
                    String key = "KS_" + fileNameOrigin;
                    aes_bc.createKey(key);
                    SecretKey secretKey = aes_bc.getKey(key);
                    if (secretKey == null) {
                        Log.i("key", "key not found");
                        ErrorUriFile.add(uriFile);
                    }
                    MAC_BC mac_bc = new MAC_BC();
                    byte[] iv = aes_bc.encriptFile(secretKey, fileOrigin, encrypCBCFile);
                    mac_bc.encryptFileWithHmac(secretKey, encrypCBCFile, hMacFile);
                    Utils_BC utils_bc = new Utils_BC(1024);
                    utils_bc.createFileProtected(encrypCBCFile, hMacFile, iv, encryptFileTotal);
                    boolean statusDelete =  fileOrigin.delete();   encrypCBCFile.delete();      hMacFile.delete() ;
                    Log.i("done", "done encrypt!"+ statusDelete);
                } catch (Exception e) {
                    ErrorUriFile.add(uriFile);
                    e.printStackTrace();
                }
            } else {
                ErrorUriFile.add(uriFile); // insert uri not exist
            }
            fileOrigin.delete();
            Log.e("error uri", String.valueOf(ErrorUriFile.size()));
            position = position +1 ;
        }

    }


}


