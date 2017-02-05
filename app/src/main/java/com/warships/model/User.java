package com.warships.model;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Andrew on 04-Feb-17.
 */

public class User {

    int id;
    public int getId() { return id; }

    String name;
    public String getName() { return name; }

    public static String calcHash(String name, String pass) {

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update((pass + name + "secret").getBytes("UTF-8"));
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            return null;
        }
    }

}
