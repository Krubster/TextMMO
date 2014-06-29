package ru.alastar.game.security;


import java.security.MessageDigest;

public class Crypt
{   
    public static String encrypt(String string)
    {       
       try
    {
           byte[] bytesOfMessage = string.getBytes("UTF-8");
           MessageDigest md = MessageDigest.getInstance("MD5");
           byte[] thedigest = md.digest(bytesOfMessage);
           return new String(thedigest ,"UTF-8");
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return null;
    }
}
