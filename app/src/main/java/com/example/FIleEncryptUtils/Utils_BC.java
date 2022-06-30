package com.example.FIleEncryptUtils;

import android.content.Context;
import android.util.Log;

import com.example.project3.Utils.UserLocalStore;

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

    public void createFileProtected(File originFile, File macFile,byte[] iv, File outputFile,int sizeBlock) throws IOException {
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

        byte[] bufferBlockSize = new byte[16];
        Arrays.fill(bufferBlockSize, (byte) 0);

        for (int k = 0; k < String.valueOf(sizeBlock).getBytes().length; k++) {
            bufferBlockSize[k] = String.valueOf(sizeBlock).getBytes()[k];
        }
        outFileStream.write(bufferBlockSize);
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
    public boolean decrypfileFromFileProtected(File fileProtected, File fileEncrypOrigin,
                                                File fileOutOrigin, String key) throws IOException, NoSuchAlgorithmException, InvalidKeyException, NoSuchProviderException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException {
        boolean flag = false;

        String nameFile = fileProtected.getName();
        Log.i("name file : ", nameFile);
        FileOutputStream fileEncrypOriginStream = new FileOutputStream(fileEncrypOrigin);

        RandomAccessFile fileProtRandomAcc = new RandomAccessFile(fileProtected, "r");
        byte[] iv = new byte[16];
        byte[] macBufer = new byte[88];
        byte[] buferBlockSize = new byte[16];
        fileProtRandomAcc.seek(0);
        fileProtRandomAcc.read(macBufer);
        fileProtRandomAcc.seek(88);
        fileProtRandomAcc.read(iv);
        fileProtRandomAcc.seek(104);
        fileProtRandomAcc.read(buferBlockSize);
        fileProtRandomAcc.seek(120);
        int posByte = 0;
        for(int k =0 ; k<buferBlockSize.length; k++){
             if( buferBlockSize[k] == (byte) 0){
                 posByte = k;
                 break ;
             }
        }
        byte[] buferBlockSize1 = new byte[posByte];
        for (int j =0 ; j <posByte;j++){
             buferBlockSize1[j] = buferBlockSize[j];
        }
        String b = new String(buferBlockSize1, StandardCharsets.UTF_8);
        int sizeBlock= Integer.parseInt(b);
        Log.i("size block decypt", String.valueOf(sizeBlock));
        byte[] buferFileEncyptOrigin = new byte[sizeBlock];
        AES_BC aes_bc = new AES_BC(sizeBlock);

        while (fileProtRandomAcc.read(buferFileEncyptOrigin) != -1) {
            fileEncrypOriginStream.write(buferFileEncyptOrigin);
        }
        fileEncrypOriginStream.close();
        fileProtRandomAcc.close();

        MAC_BC mac_bc = new MAC_BC(sizeBlock);
        SecretKey secretKey = aes_bc.getKey(key);
        boolean check_mac = mac_bc.checkHmac(macBufer, fileEncrypOrigin, secretKey);
        if (check_mac == true) {
            Log.i("check mac", "true");
            aes_bc.decryptFile(secretKey,iv,fileEncrypOrigin,fileOutOrigin);
            Log.i("encyp", "done decrypt file") ;
            flag = true ;
        }
        Log.i("flag", String.valueOf(flag));
        return flag ;
    }
}
