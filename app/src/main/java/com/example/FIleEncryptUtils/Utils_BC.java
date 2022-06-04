package com.example.FIleEncryptUtils;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Utils_BC {
    private int sizeBlock;

    public Utils_BC(int sizeBlock) {
        super();
        this.sizeBlock = sizeBlock;
    }

    public Utils_BC() {
    }

    public void createFileProtected(File originFile, File macFile,byte[] iv, File outputFile) throws IOException {
        FileOutputStream outFileStream = new FileOutputStream(outputFile);
        FileInputStream inputHmacFile = new FileInputStream(macFile);
        FileInputStream inputOriginFile = new FileInputStream(originFile);
        byte[] buferOriginFile = new byte[sizeBlock];
        byte[] buferHmacFile = new byte[sizeBlock];

        int i;

        while ((i = inputHmacFile.read(buferHmacFile)) != -1) {

            outFileStream.write(buferHmacFile, 0, i);  //88 byte

        }
        outFileStream.write(iv); // 16 byte
        int k;
        while ((k = inputOriginFile.read(buferOriginFile)) != -1) {
            outFileStream.write(buferOriginFile, 0, k);

        }
        outFileStream.close();
        inputHmacFile.close();
        inputOriginFile.close();
        macFile.delete(); originFile.delete();
    }

    //
    public boolean decrypfileFromFileProtected(File fileProtected,File fileEncrypOrigin,
                                            File fileOutOrigin) throws IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        boolean flag = false;
        AES_BC aes_bc = new AES_BC();
        String nameFile = fileProtected.getName();
        Log.i("name file : ", nameFile);
        FileOutputStream fileEncrypOriginStream = new FileOutputStream(fileEncrypOrigin);
        byte[] buferFileEncyptOrigin = new byte[1024];
        RandomAccessFile fileProtRandomAcc = new RandomAccessFile(fileProtected, "r");
        byte[] iv = new byte[16];
        byte[] macBufer = new byte[88];
        fileProtRandomAcc.seek(0);
        fileProtRandomAcc.read(macBufer);
        fileProtRandomAcc.seek(88);
        fileProtRandomAcc.read(iv);
        fileProtRandomAcc.seek(104);
        while (fileProtRandomAcc.read(buferFileEncyptOrigin) != -1) {
            fileEncrypOriginStream.write(buferFileEncyptOrigin);
        }
        fileEncrypOriginStream.close();
        fileProtRandomAcc.close();


        MAC_BC mac_bc = new MAC_BC();
        SecretKey secretKey = aes_bc.getKey("KS_"+nameFile);
        boolean check_mac = mac_bc.checkHmac(macBufer, fileEncrypOrigin, secretKey);
        if (check_mac == true) {
            aes_bc.decryptFile(secretKey,iv,fileEncrypOrigin,fileOutOrigin);
            Log.i("encyp", "done decrypt file") ;
            flag = true ;
        }
        Log.i("flag", String.valueOf(flag));
        return flag ;
    }
}
