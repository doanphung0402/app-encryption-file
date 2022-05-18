package com.example.FIleEncryptUtils;

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
}
