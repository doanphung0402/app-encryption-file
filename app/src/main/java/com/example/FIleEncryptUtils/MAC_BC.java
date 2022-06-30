package com.example.FIleEncryptUtils;

import android.annotation.SuppressLint;
import android.security.keystore.KeyProperties;
import android.util.Log;

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
import java.util.ServiceConfigurationError;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class MAC_BC {
    private int sizeBlock ;
    public MAC_BC(int sizeBlock) {
        this.sizeBlock =sizeBlock ;
    }

    @SuppressLint("NewApi")
    public void encryptFileWithHmac(SecretKey key, File fileHmacInput,
                                    File fileHmacOutput)
            throws GeneralSecurityException, IllegalStateException, IOException {
        Log.i("mac file ",key.toString());
        Mac hmac = Mac.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA512);
        SecretKey secretKey = new SecretKeySpec(key.toString().getBytes(),KeyProperties.KEY_ALGORITHM_HMAC_SHA512);
        hmac.init(secretKey);
        FileInputStream inputStream = new FileInputStream(fileHmacInput);
        FileOutputStream outputStream = new FileOutputStream(fileHmacOutput);
        byte[] buffer = new byte[sizeBlock];
        while ((inputStream.read(buffer)) != -1) {
            hmac.update(buffer);
        }
        byte[] outputBytes = hmac.doFinal();
        String StringHmac = Base64.getEncoder().encodeToString(outputBytes);
        outputStream.write(StringHmac.getBytes());
        inputStream.close();
        outputStream.close();

    }

    @SuppressLint("NewApi")
    public boolean checkHmac(byte[] buferHmac, File originFile, SecretKey key) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        Mac hmac = Mac.getInstance(KeyProperties.KEY_ALGORITHM_HMAC_SHA512);
        SecretKey secretKey = new SecretKeySpec(key.toString().getBytes(),KeyProperties.KEY_ALGORITHM_HMAC_SHA512);
        hmac.init(secretKey);
        Log.i("secret key decrp mac", secretKey.toString());

        FileInputStream inputOriginFile = new FileInputStream(originFile);
        byte[] buferFile = new byte[sizeBlock];
        while (inputOriginFile.read(buferFile) != -1) {
            hmac.update(buferFile);
        }
        String decodeMacBase64 = new String(buferHmac, StandardCharsets.UTF_8).trim();
        String StringHmac =   Base64.getEncoder().encodeToString(hmac.doFinal());
        boolean checkHmac = decodeMacBase64.equals(StringHmac.trim());
        inputOriginFile.close();
        return checkHmac ;
    }

}
