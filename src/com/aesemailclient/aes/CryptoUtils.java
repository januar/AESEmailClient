package com.aesemailclient.aes;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;

public class CryptoUtils {
	
	private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    
    public static String LOG;
    
	public CryptoUtils() {
		// TODO Auto-generated constructor stub
	}
	
	private static byte[] doCrypto(int cipherMode, String key, byte[] text)
	{
		try {
			Key secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(cipherMode, secretKey);
			byte[] result = cipher.doFinal(text);
			
			return result;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG = e.getMessage();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG = e.getMessage();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG = e.getMessage();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG = e.getMessage();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG = e.getMessage();
		}
		return null;
	}
	
	public static String encrypt(String key, String plaintext) {
		try {
			return Base64.encodeToString(doCrypto(Cipher.ENCRYPT_MODE, key, plaintext.getBytes("UTF-8")), Base64.DEFAULT);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG = e.getMessage();
		}
		return "";
	}
	
	public static String decrypt(String key, String ciphertext) {
		byte[] cipher;
		cipher = Base64.decode(ciphertext, Base64.DEFAULT);
		try {
			return new String(doCrypto(Cipher.DECRYPT_MODE, key, cipher), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOG = e.getMessage();
		}
		return "";
	}
}
