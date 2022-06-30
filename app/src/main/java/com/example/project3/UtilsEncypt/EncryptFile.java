package com.example.project3.UtilsEncypt;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
import com.example.project3.Utils.UserLocalStore;

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
    UserLocalStore userLocalStore  ;
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




}


