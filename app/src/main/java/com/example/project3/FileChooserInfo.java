package com.example.project3;

public class FileChooserInfo {
      String nameFile , urlFile ;
     int iconFile ;

    public FileChooserInfo(String nameFile, String urlFile, int iconFile) {
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

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }

    public int getIconFile() {
        return iconFile;
    }

    public void setIconFile(int iconFile) {
        this.iconFile = iconFile;
    }
}
