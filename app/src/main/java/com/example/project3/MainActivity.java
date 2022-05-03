package com.example.project3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
      final String DatabaseName = "project3.db";
      SQLiteDatabase sqLiteDatabase ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        sqLiteDatabase = Database.initDatabase(this,DatabaseName);
//        Cursor cursor = sqLiteDatabase.rawQuery("Select * from user_account",null);
//        cursor.moveToFirst();
//        Toast.makeText(this,cursor.getString(0), Toast.LENGTH_SHORT).show();

    }
    public void onRegis(View view){
        Intent intent_reg  = new Intent(this,RegActivity.class);
        startActivity(intent_reg);
    }
    public void onLogin(View view){
         Intent intent_login = new Intent(this,LoginActivity.class);
         startActivity(intent_login);
    }
}