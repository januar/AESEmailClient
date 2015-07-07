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

	private static final String DATABASE_NAME = "aesemailclient.db";
	private static final int DATABASE_VERSION = 0;

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		initDatabase();
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
		db.execSQL(DatabaseCreate());
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

	private void initDatabase() {
		/* membuat bentuk dari databse yang akan digenerate */
		/* tambahkan map item baru jika ingin menambahkan table baru */

		DATABASE_ENTITY = new HashMap<String, Map<String, String>>();
		// table inbox
		Map<String, String> inboxTable = new HashMap<String, String>();
		inboxTable.put("id_inbox", "integer primary key autoincrement");
		inboxTable.put("subject", "text");
		inboxTable.put("from", "text not null");
		inboxTable.put("to", "text not null");
		inboxTable.put("date", "text not null");
		inboxTable.put("is_read", "integer");
		DATABASE_ENTITY.put("t_inbox", inboxTable);

		// table user
		Map<String, String> userTable = new HashMap<String, String>();
		userTable.put("email", "text not null");
		userTable.put("password", "text not null");
		userTable.put("email_type", "text not null");
		DATABASE_ENTITY.put("t_user", userTable);

		// table sent
		Map<String, String> sentTable = new HashMap<String, String>();
		sentTable.put("id_sent", "integer primary key autoincrement");
		inboxTable.put("subjec", "text");
		inboxTable.put("from", "text not null");
		inboxTable.put("to", "text not null");
		inboxTable.put("date", "text not null");
		DATABASE_ENTITY.put("t_sent", sentTable);
	}
	
	private String DatabaseCreate()
	{
		String query = "";
		
		for (String table_name : DATABASE_ENTITY.keySet()) {
			String column = "";
			Map<String, String> list_column = DATABASE_ENTITY.get(table_name);
			for (String field : list_column.keySet()) {
				column += String.format("%s %s, ", field, list_column.get(field));
			}
			column = column.trim().replaceAll(",$", "");
			String create_query = String.format("create table %s (%s); ", table_name, column);
			query += create_query;
		}
		
		return query;
	}

}
