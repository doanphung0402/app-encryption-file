package com.example.project3.Exception;

public class AuthenticationException extends  RuntimeException{
     public AuthenticationException(String message){
         super(message);
     }
}
