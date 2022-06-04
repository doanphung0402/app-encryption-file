package com.example.FIleEncryptUtils;

import android.content.Context;
import android.util.Log;

import java.security.KeyStore;

import javax.crypto.spec.SecretKeySpec;

public class KeyStoreUtils {
    public Context context;

    public KeyStoreUtils(Context context) {
        this.context = context;
    }

    public void saveKey(SecretKeySpec secretKeySpec, String password,String alias) {
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(password.toCharArray());
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKeySpec);
            ks.setEntry(alias, secretKeyEntry, protectionParam);
        } catch (Exception e) {
            Log.e("save key", "Error Save Key" + e.getMessage());
        }
    }

    public SecretKeySpec getKeyFromKs(String password, String alias) {
        SecretKeySpec secretKeySpec = null;
        try {
            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyStore.ProtectionParameter protectionParam = new KeyStore.PasswordProtection(password.toCharArray());
            KeyStore.SecretKeyEntry entry = (KeyStore.SecretKeyEntry) ks.getEntry(alias, protectionParam);
             secretKeySpec = (SecretKeySpec) entry.getSecretKey();
        } catch (Exception e) {
            Log.e("save key", "Error Save Key" + e.getMessage());
        }
        return secretKeySpec ;
    }
}

