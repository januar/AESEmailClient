package com.aesemailclient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.util.Log;

public class CacheToFile {
	public static final String DATE_OLD = "date_old.txt";
	public static final String DATE_LAST = "date_last.txt";
	public static final String TAG = "CacheToFile";
	public static final int READ_BLOCK_SIZE = 100;
	
	public CacheToFile() {
		// TODO Auto-generated constructor stub
	}
	
	public static void Write(Context context, String filename, String content) {
		try {
			FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			OutputStreamWriter outputWriter = new OutputStreamWriter(fos);
			outputWriter.write(content);
			outputWriter.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
	}
	
	public static String Read(Context context, String filename) {
		String s = "";
		try {
			FileInputStream fis = context.openFileInput(filename);
			InputStreamReader inputReader = new InputStreamReader(fis);
			char[] inputBuffer = new char[READ_BLOCK_SIZE];
			int charRead;
			while ((charRead=inputReader.read(inputBuffer))>0) {
				String readstring = String.copyValueOf(inputBuffer,0,charRead);
				s += readstring;
			}
			inputReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
		return s;
	}

}
