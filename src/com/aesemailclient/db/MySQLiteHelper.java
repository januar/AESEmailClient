package com.aesemailclient.db;

import java.util.HashMap;
import java.util.Map;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
	public static Map<String, Map<String, String>> DATABASE_ENTITY;
	
	@SuppressWarnings("unused")
	private Context context;
	private static final String DATABASE_NAME = "aesemailclient.db";
	private static final int DATABASE_VERSION = 7;

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		initDatabase();
		this.context = context;
	}

	public MySQLiteHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
		initDatabase();
	}

	public MySQLiteHelper(Context context, String name, CursorFactory factory,
			int version, DatabaseErrorHandler errorHandler) {
		super(context, name, factory, version, errorHandler);
		// TODO Auto-generated constructor stub
		initDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		for (String table_name : DATABASE_ENTITY.keySet()) {
			db.execSQL(DatabaseCreate(table_name));
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.w(MySQLiteHelper.class.getName(),
		        "Upgrading database from version " + oldVersion + " to "
		            + newVersion + ", which will destroy all old data");
		
		for (String table_name : DATABASE_ENTITY.keySet()) {
			db.execSQL("DROP TABLE IF EXISTS " + table_name);
		}
		onCreate(db);
	}
	
	public void drop(SQLiteDatabase db) {
		Log.w(MySQLiteHelper.class.getName(),
		        "Drop table which will destroy all old data");
		
		for (String table_name : DATABASE_ENTITY.keySet()) {
			db.execSQL("DELETE FROM " + table_name);
		}
	}

	private void initDatabase() {
		/* membuat bentuk dari databse yang akan digenerate */
		/* tambahkan map item baru jika ingin menambahkan table baru */

		DATABASE_ENTITY = new HashMap<String, Map<String, String>>();
		// table inbox
		Map<String, String> inboxTable = new HashMap<String, String>();
		inboxTable.put(InboxDataSource.COLUMN_ID, "integer primary key autoincrement");
		inboxTable.put(InboxDataSource.COLUMN_SUBJECT, "text");
		inboxTable.put(InboxDataSource.COLUMN_FROM, "text not null");
		inboxTable.put(InboxDataSource.COLUMN_TO, "text not null");
		inboxTable.put(InboxDataSource.COLUMN_DATE, "text not null");
		inboxTable.put(InboxDataSource.COLUMN_CONTENT, "text");
		inboxTable.put(InboxDataSource.COLUMN_ISDOWNLOAD, "integer");
		inboxTable.put(InboxDataSource.COLUMN_UUID, "integer");
		DATABASE_ENTITY.put(InboxDataSource.TABLE_NAME, inboxTable);

		// table user
		Map<String, String> userTable = new HashMap<String, String>();
		userTable.put(UserDataSource.COLUMN_EMAIL, "text not null");
		userTable.put(UserDataSource.COLUMN_PASSWORD, "text not null");
		userTable.put(UserDataSource.COLUMN_EMAIL_TYPE, "text not null");
		DATABASE_ENTITY.put(UserDataSource.TABLE_NAME, userTable);

		// table sent
		Map<String, String> sentTable = new HashMap<String, String>();
		sentTable.put(SentDataSource.COLUMN_ID, "integer primary key autoincrement");
		sentTable.put(SentDataSource.COLUMN_SUBJECT, "text");
		sentTable.put(SentDataSource.COLUMN_FROM, "text not null");
		sentTable.put(SentDataSource.COLUMN_TO, "text not null");
		sentTable.put(SentDataSource.COLUMN_CONTENT, "text not null");
		sentTable.put(SentDataSource.COLUMN_DATE, "text not null");
		DATABASE_ENTITY.put(SentDataSource.TABLE_NAME, sentTable);
	}
	
	private String DatabaseCreate(String table_name)
	{
		String query = "";
		String column = "";
		Map<String, String> list_column = DATABASE_ENTITY.get(table_name);
		for (String field : list_column.keySet()) {
			column += String.format("%s %s, ", field, list_column.get(field));
		}
		column = column.trim().replaceAll(",$", "");
		query = String.format("create table %s (%s); ", table_name, column);;
		
		return query;
	}

}
