package com.example.project3;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.FIleEncryptUtils.AES_BC;
import com.example.FIleEncryptUtils.MAC_BC;
import com.example.FIleEncryptUtils.Utils_BC;
import com.example.project3.Utils.UserLocalStore;

import java.io.File;
import java.util.ArrayList;

import javax.crypto.SecretKey;

public class ProgressLoadingAsyncTask extends AsyncTask<Void, Integer, Void> {
     Activity contextParent ;
     ArrayList<FileChooserInfo> listFile =new ArrayList<>();
     public ProgressLoadingAsyncTask(Activity contextParent,ArrayList<FileChooserInfo> listFile){
          this.contextParent = contextParent ;
          this.listFile  = listFile ;
     }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onProgressUpdate(Integer... values) {
         super.onProgressUpdate(values);
         Log.i("dung luong list1", String.valueOf(this.listFile.size()));
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        ProgressBar progressBar = contextParent.findViewById(R.id.progressBarCircle);
        progressBar.setVisibility(View.VISIBLE);
    }



    }

