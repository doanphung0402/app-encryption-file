package com.example.FIleEncryptUtils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MAC_BC {
    public MAC_BC() {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void encryptFileWithHmac(SecretKeySpec key, File fileHmacInput,
                                    File fileHmacOutput)
            throws GeneralSecurityException, IllegalStateException, IOException {
        Mac hmac = Mac.getInstance("HMacSHA512", "BCFIPS");
        hmac.init(key);
        FileInputStream inputStream = new FileInputStream(fileHmacInput);
        FileOutputStream outputStream = new FileOutputStream(fileHmacOutput);
        byte[] buffer = new byte[1024];
        while ((inputStream.read(buffer)) != -1) {
            hmac.update(buffer);
            byte[] outputBytes = hmac.doFinal();
            String StringHmac =   Base64.getEncoder().encodeToString(outputBytes);

            outputStream.write(StringHmac.getBytes());
        }
        inputStream.close();
        outputStream.close();

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean checkHmac(File HmacFile, File originFile, SecretKeySpec key ) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException {
        Mac hmac = Mac.getInstance("HMacSHA512", "BCFIPS");
        hmac.init(key);
        FileInputStream inputHmacFile = new FileInputStream(HmacFile);
        FileInputStream inputOriginFile = new FileInputStream(originFile);
        byte[] bufferHmac = new byte[88];  //133
        byte[] buferFile = new byte[1024];
        boolean flag = true ;
        int i =0 ;
        while ((inputHmacFile.read(bufferHmac)) != -1 && (inputOriginFile.read(buferFile)) != -1) {
            String decodeMacBase64 = new String(bufferHmac, StandardCharsets.UTF_8);
            hmac.update(buferFile);
            String StringHmac =   Base64.getEncoder().encodeToString(hmac.doFinal());
            boolean checkHmac = decodeMacBase64.equals(StringHmac);
            if(!checkHmac) {
                flag = false;
                System.out.println(i);
                break ;

            }
        }

        inputHmacFile.close();
        inputOriginFile.close();
        return flag ;
    }

}
