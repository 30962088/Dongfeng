package com.media.dongfeng.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	public static String hexdigest(String string) {
	    if(string == null){
	        return null;
	    }
		String s = null;
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(string.getBytes());
			byte tmp[] = md.digest();
			char str[] = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			s = new String(str);
		}
		catch (Exception e) {
		}
		return s;
	}
	
	public static String hexdigest(byte[] data) {
	    if(data == null){
	        return null;
	    }
        String s = null;
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            byte tmp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);
        }
        catch (Exception e) {
        }
        return s;
    }
	
	public static String hexdigest(MessageDigest md) {
        if(md == null){
            return null;
        }
        String s = null;
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            byte tmp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);
        }
        catch (Exception e) {
            
        }
        return s;
    }
	
	public static String getMD5(File file){
		
		FileInputStream fis = null;
		
		try {
			
			MessageDigest md = MessageDigest.getInstance("MD5");
			
			fis = new FileInputStream(file);
			
			byte[] buffer = new byte[8192];
			
			int length = -1;
			
			while ((length = fis.read(buffer)) != -1) {
				md.update(buffer, 0, length);
			}
			
			return bytesToString(md.digest());
			
		} catch (IOException ex) {
			return null;
		} catch (NoSuchAlgorithmException ex) {
			return null;
		} finally {
			try {
				if(fis!=null){
					fis.close();
				}
			} catch (IOException ex) {
			}
		}
	}
	
	public static String bytesToString(byte[] data) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f'};
        char[] temp = new char[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            byte b = data[i];
            temp[i * 2] = hexDigits[b >>> 4 & 0x0f];
            temp[i * 2 + 1] = hexDigits[b & 0x0f];
        }
        return new String(temp);

    }
}