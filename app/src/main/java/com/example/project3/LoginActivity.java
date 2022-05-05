package com.example.project3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dustinredmond.BCrypt;

public class LoginActivity extends AppCompatActivity {
    final String DatabaseName = "project3.db";
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public boolean isValid(String clearTextPassword, String hashedPass) {
        // returns true if password matches hash
        return BCrypt.checkpw(clearTextPassword, hashedPass);
    }

    public void onLoginPage(View view) {
        try {
            EditText emailEt = findViewById(R.id.email);
            EditText passEd = findViewById(R.id.password);
            String email = emailEt.getText().toString();
            String password = passEd.getText().toString();
            Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Điền đầy dủ email và password !", Toast.LENGTH_SHORT).show();
            } else {
                sqLiteDatabase = Database.initDatabase(this, DatabaseName);
                Cursor cursor = sqLiteDatabase.rawQuery("Select * from user_account Where email = ? ", new String[]{email});
                cursor.moveToFirst();
                if (cursor.isNull(0) == true) {
                    Toast.makeText(this, "Email chưa được đăng kí !", Toast.LENGTH_SHORT).show();
                }else{

                    String passEncode = cursor.getString(1).toString();
                    System.out.println("pass:"+passEncode);
                    if(isValid(password,passEncode) == true){
                        Intent intent = new Intent(this, ContainerActivity.class);
                        startActivity(intent);
                        Toast.makeText(this, "welcome friend ! ", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, "Mật khẩu sai, thử lại !", Toast.LENGTH_SHORT).show();
                    }
                }

            } //end if
        }catch(Exception e){
            Toast.makeText(this, "Có lỗi ,thử lại !", Toast.LENGTH_SHORT).show();
        }
    }
}