package com.example.FIleEncryptUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils_Function {
    public long getIntPartCeil(long sizeFile , long blockSize) {
        long numberBlockIntPart = (long) sizeFile/blockSize ;
        return numberBlockIntPart ;
    }
    public long getByteLeftOverBlock(long sizeFile , long blockSize ) {
        long numberBlockIntPart = getIntPartCeil(sizeFile ,blockSize);
        long  byteLeftOver = sizeFile - blockSize * numberBlockIntPart ;
        return byteLeftOver;
    }
    public void emptyFile(File file) throws IOException {
        new FileOutputStream(file.getPath()).close();
    }
}
