package com.example.project3.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class UserLocalStore {
     public static final String SP_NAME ="userDetails";
     public static final String USER_AUTH ="userAuth";
     SharedPreferences userLocalDb ;
     public UserLocalStore (Context context){
          userLocalDb = context.getSharedPreferences(SP_NAME,0 );
     }
     public void storeUserData(User user){
          SharedPreferences.Editor spEditor = userLocalDb.edit();
          spEditor.putString("username", user.getUsername());
          spEditor.putString("password",user.getPassword());
          spEditor.commit();
     }
     public User getUser(){
          String email = userLocalDb.getString("username","");
          String password = userLocalDb.getString("password","");
          User user = new User(email,password);
          return user ;
     }

     public void  setAuthDialog(){
          SharedPreferences.Editor spEditor = userLocalDb.edit();
          spEditor.putBoolean("auth_dialog",true);
          spEditor.commit();
     }
     public boolean getAuthDialog(){
          return userLocalDb.getBoolean("auth_dialog",false);
     }
     public void clearUserDetails(){
          SharedPreferences.Editor spEditor = userLocalDb.edit();
          spEditor.clear();
          spEditor.commit();
     }
     public void setUserLoggedIn(){
          SharedPreferences.Editor spEditor = userLocalDb.edit();
          spEditor.putBoolean("login",true);
          spEditor.commit();
     }
     public boolean checkLoggedIn(){
           boolean flag =false ;
           flag = userLocalDb.getBoolean("login",false);
           return flag;
     }
}
