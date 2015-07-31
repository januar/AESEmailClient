package com.aesemailclient.db;

import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class UserDataSource {

	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_PASSWORD = "password";
	public static final String COLUMN_EMAIL_TYPE = "email_type";
	public static final String TABLE_NAME = "t_user";

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	// column dalam tabel user
	private String[] allColumns;

	public UserDataSource(Context context) {
		// TODO Auto-generated constructor stub
		dbHelper = new MySQLiteHelper(context);
		Map<String, String> column = MySQLiteHelper.DATABASE_ENTITY
				.get(TABLE_NAME);
		allColumns = new String[column.size()];
		int i = 0;
		for (String field : column.keySet()) {
			allColumns[i] = field;
			i++;
		}
	}

	// method untuk membuka koneksi ke database
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	// method untuk menutup koneksi ke database
	public void close() {
		dbHelper.close();
	}
	
	public UserEntity getUser() {
		UserEntity result = null;

		Cursor cursor = database.query(UserDataSource.TABLE_NAME, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			result = cursorToEntity(cursor);
		}

		cursor.close();
		return result;
	}
	
	public void delete() {
		/*database.execSQL("DELETE FROM " + TABLE_NAME);*/
		dbHelper.drop(database);
	}
	
	public void save(UserEntity user) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_EMAIL, user.getEmail());
		values.put(COLUMN_PASSWORD, user.getPassword());
		values.put(COLUMN_EMAIL_TYPE, user.getEmailType());
		database.insert(UserDataSource.TABLE_NAME, null,
				values);
		/*Cursor cursor = database.query(UserDataSource.TABLE_NAME, allColumns,
				COLUMN_EMAIL + " = " + user.getEmail(), null, null, null, null);
		cursor.moveToFirst();
		UserEntity newResult = cursorToEntity(cursor);
		cursor.close();
		return newResult;*/
	}

	// method untuk mengubah object cursor kedalam bentuk Object Result
	private UserEntity cursorToEntity(Cursor cursor) {
		UserEntity user = new UserEntity();
		user.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
		user.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)));
		user.setEmailType(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL_TYPE)));
		return user;
	}

}
