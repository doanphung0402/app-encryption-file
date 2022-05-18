package com.example.FIleEncryptUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class Utils_BC {
    private int sizeBlock;

    public Utils_BC(int sizeBlock) {
        super();
        this.sizeBlock = sizeBlock;
    }

    public Utils_BC() {
    }

    public void createFileProtected(File originFile, File macFile, File outputFile) throws IOException {
        long sizeOriginFile = (long) originFile.length();
        long sizeMacFile = (long) macFile.length();
        FileOutputStream outFileStream = new FileOutputStream(outputFile);
        FileInputStream inputHmacFile = new FileInputStream(macFile);
        FileInputStream inputOriginFile = new FileInputStream(originFile);
        Utils_Function utils = new Utils_Function();

        byte[] buferOriginFile = new byte[sizeBlock];
        byte[] buferHmacFile = new byte[sizeBlock];

        byte[] buferMetaDataNumberBlockFileMac = new byte[32]; // 256
        byte[] buferMetaDataNumberLeftOverBlock = new byte[32];

        long numberBlockMacFile = utils.getIntPartCeil(sizeMacFile, sizeBlock);
        long numberByteLeftOverBlock = utils.getByteLeftOverBlock(sizeMacFile, sizeBlock);

        String stringNumberBlockMacFile = Long.toString(numberBlockMacFile);
        String stringNumberByteLeftOverBlock = Long.toString(numberByteLeftOverBlock);
        Arrays.fill(buferMetaDataNumberBlockFileMac, (byte) 0);
        Arrays.fill(buferMetaDataNumberLeftOverBlock, (byte) 0);

        for (int k = 0; k < stringNumberBlockMacFile.getBytes().length; k++) {
            buferMetaDataNumberBlockFileMac[k] = stringNumberBlockMacFile.getBytes()[k];
        }
        for (int k = 0; k < stringNumberByteLeftOverBlock.getBytes().length; k++) {
            buferMetaDataNumberLeftOverBlock[k] = stringNumberByteLeftOverBlock.getBytes()[k];
        }

        outFileStream.write(buferMetaDataNumberBlockFileMac);
        outFileStream.write(buferMetaDataNumberLeftOverBlock);
        int i;

        while ((i = inputHmacFile.read(buferHmacFile)) != -1) {

            outFileStream.write(buferHmacFile, 0, i);

        }
        int k;
        while ((k = inputOriginFile.read(buferOriginFile)) != -1) {
            outFileStream.write(buferOriginFile, 0, k);

        }
        outFileStream.close();
        inputHmacFile.close();
        inputOriginFile.close();

    }

    //
    public void encrypfileFromFileProtected(File fileProtected, File fileOutMac, File fileOutOrigin)
            throws IOException {
        FileInputStream fileProtectedInputStream = new FileInputStream(fileProtected);
        FileOutputStream fileMacOutStream = new FileOutputStream(fileOutMac);
        FileOutputStream fileOriginOutStream = new FileOutputStream(fileOutOrigin);

        byte[] buferFileProtectedOutMacStream = new byte[sizeBlock];
        byte[] buferFileProtectedOutOriginFileStream = new byte[sizeBlock];

        byte[] buferMetaDataNumberBlockFileMac = new byte[32]; // 256
        byte[] buferMetaDataNumberLeftOverBlock = new byte[32];

        fileProtectedInputStream.read(buferMetaDataNumberBlockFileMac);
        fileProtectedInputStream.read(buferMetaDataNumberLeftOverBlock);


        String NumberBlockFileMac = new String(buferMetaDataNumberBlockFileMac, StandardCharsets.UTF_8);
        String BytesLeftOverBlock = new String(buferMetaDataNumberLeftOverBlock, StandardCharsets.UTF_8);

        long blockSizeMac = Long.parseLong(NumberBlockFileMac.trim());
        long bytesOverLeft = Long.parseLong(BytesLeftOverBlock.trim());

//
        int i = 0;
        int k;
        while (i < blockSizeMac) {
            k = fileProtectedInputStream.read(buferFileProtectedOutMacStream);
            fileMacOutStream.write(buferFileProtectedOutMacStream, 0, k);
            i++;
        }
        byte[] buferOverLeftBytesFromFileMac =new byte[(int) bytesOverLeft];
        fileProtectedInputStream.read(buferOverLeftBytesFromFileMac);
        fileMacOutStream.write(buferOverLeftBytesFromFileMac);
        fileMacOutStream.close();

        int a;
        while ((a = fileProtectedInputStream.read(buferFileProtectedOutOriginFileStream)) != -1) {
            fileOriginOutStream.write(buferFileProtectedOutOriginFileStream, 0, a);
        }
        fileOriginOutStream.close();
        fileProtectedInputStream.close();
    }

}
