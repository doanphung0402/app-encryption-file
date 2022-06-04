package com.example.project3;

import android.net.Uri;

public class FileChooserInfo {
      String nameFile ;
      Uri urlFile ;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    int iconFile ;

    public FileChooserInfo(String nameFile, Uri urlFile, int iconFile, boolean selected) {
        this.nameFile = nameFile;
        this.urlFile = urlFile;
        this.iconFile = iconFile;
        this.selected = selected;
    }

    boolean selected = false;

    public FileChooserInfo(String nameFile, Uri urlFile, int iconFile) {
        this.nameFile = nameFile;
        this.urlFile = urlFile;
        this.iconFile = iconFile;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public Uri getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(Uri urlFile) {
        this.urlFile = urlFile;
    }

    public int getIconFile() {
        return iconFile;
    }

    public void setIconFile(int iconFile) {
        this.iconFile = iconFile;
    }
}
