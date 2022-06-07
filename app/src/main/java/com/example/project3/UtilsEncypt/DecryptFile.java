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
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DecryptFile {
    public static final String EXTERNAL_PATH = Environment.getExternalStorageDirectory().getPath();
    public ArrayList<FileChooserInfo> listFileDecrypt;
    public Context context;

    public DecryptFile(ArrayList<FileChooserInfo> listFile, Context context) {
        this.listFileDecrypt = listFile;
        this.context = context;
    }

    public ArrayList<FileChooserInfo> getListFile() {
        return listFileDecrypt;
    }

    public void setListFile(ArrayList<FileChooserInfo> listFile) {
        this.listFileDecrypt = listFile;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void decryptFile() {
        Log.i("Decrypt file", String.valueOf(listFileDecrypt.size()));
        AES_BC aes_bc = new AES_BC();
        File fileDecryptFolder = new File(EXTERNAL_PATH, "Project3_File");
        if (!fileDecryptFolder.exists()) {
            fileDecryptFolder.mkdir();
        }
        int position = 0;
        while (position < listFileDecrypt.size()) {
            Uri uriFile = listFileDecrypt.get(position).getUrlFile();
            Log.i("uri file ", String.valueOf(uriFile));
            ArrayList<Uri> ErrorUriFile = new ArrayList<>();
//            File fileOrigin = new File(UriUtils.getPathFromUri(context, uriFile));
            File fileOrigin = new File(String.valueOf(uriFile));
            File fileStepDecrypt = new File(EXTERNAL_PATH + "/Android/data/com.example.project3", "file_step_decrypt.step");
            if (fileOrigin.exists()) {
                Log.i("uri file ", fileOrigin.getAbsolutePath());

                String nameFile = listFileDecrypt.get(position).getNameFile();
                Log.i("name file ", nameFile);
                String fileNameOrigin = nameFile.substring(nameFile.indexOf("/") + 1);
                Log.i("name file ", fileNameOrigin);
                try {

                    if (!fileStepDecrypt.exists()) {
                        fileStepDecrypt.createNewFile();
                        Log.i("file", "File step decrypt created ");
                    }
                    File fileOutDecrypt = new File(EXTERNAL_PATH + "/Project3_File", fileNameOrigin);
                    if (!fileOutDecrypt.exists()) {
                        fileOutDecrypt.createNewFile();
                        Log.i("file", "File output created ");
                    } else {
                        ErrorUriFile.add(uriFile);
                        break;
                    }

                    String key = "project3_key";

                    SecretKey secretKey = aes_bc.getKey(key);
                    Log.i("key decrypt", secretKey.toString());
                    if (secretKey == null) {
                        Log.i("key", "key not found");
                        ErrorUriFile.add(uriFile);
                        break;
                    }
                    Utils_BC utils_bc = new Utils_BC(1024);
                    boolean rsDecryptFile = utils_bc.decrypfileFromFileProtected(fileOrigin, fileStepDecrypt, fileOutDecrypt);
                    if (rsDecryptFile == false) {
                        ErrorUriFile.add(uriFile);
                        break;
                    }
                    Log.i("done","done decrypt");

                } catch (Exception e) {

                    e.printStackTrace();
                }
                fileOrigin.delete();
                fileStepDecrypt.delete();
            } else {
                Log.e("file", "file not exist");
                ErrorUriFile.add(uriFile); // insert uri not exist
            }

            Log.e("error uri", String.valueOf(ErrorUriFile.size()));
            position = position + 1;
        }
    }
}


