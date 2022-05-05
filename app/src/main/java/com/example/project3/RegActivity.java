package com.example.project3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dustinredmond.BCrypt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class RegActivity extends AppCompatActivity {
    final String DatabaseName = "project3.db";
    SQLiteDatabase sqLiteDatabase ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
    }
    private String generateHashedPass(String pass) {
        // hash a plaintext password using the typical log rounds (10)
        return BCrypt.hashpw(pass, BCrypt.gensalt());
    }

    public void onRegisNow(View view){
        sqLiteDatabase = Database.initDatabase(this,DatabaseName);
        Cursor cursor = sqLiteDatabase.rawQuery("Select * from user_account",null);
        cursor.moveToFirst();

        EditText emaiEt =  findViewById(R.id.email_reg);
        EditText passworldEt =  findViewById(R.id.password_reg);
        EditText repeat_passworldEt =  findViewById(R.id.password_repeat);
        String email = emaiEt.getText().toString();
        String password = passworldEt.getText().toString();
        String passwordEncode = generateHashedPass(password);
        String repeat_pass = repeat_passworldEt.getText().toString();

        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);

        if(email.isEmpty()){
            Toast.makeText(this, "email không được để trống", Toast.LENGTH_SHORT).show();
            return ;
        } else if (password.isEmpty()){
            Toast.makeText(this, "password bắt buộc", Toast.LENGTH_SHORT).show();
            return ;
        }else if(repeat_pass.isEmpty()){
            Toast.makeText(this, "Vui lòng nhập lại password", Toast.LENGTH_SHORT).show();
            return ;
        }else if(password.equals(repeat_pass)==false){
            Toast.makeText(this, "Password nhập sai !", Toast.LENGTH_SHORT).show();
            return ;
        }else if(matcher.find()==false){
            Toast.makeText(this, "Email không hợp lệ !", Toast.LENGTH_SHORT).show();
            return ;
        }else if(password.length()<8){
            Toast.makeText(this, "password ít nhất 8 kí tự !", Toast.LENGTH_SHORT).show();
            return ;
        }

        for (int i =0 ; i< cursor.getCount(); i++){
             cursor.moveToPosition(i);
             String emailFindInDb = cursor.getString(0);
             if(emailFindInDb.equals(email)==true){
                 Toast.makeText(this, "Email đã được đăng kí !", Toast.LENGTH_SHORT).show();
                 return ;
             }
        } //end for

            ContentValues contentValues = new ContentValues();
            contentValues.put("email",email);
            contentValues.put("password",passwordEncode);
            sqLiteDatabase.insert("user_account",null,contentValues);
            Intent intent = new Intent(this, EncryptFileActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();
        }
    }
