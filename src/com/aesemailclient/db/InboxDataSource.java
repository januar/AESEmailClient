package com.aesemailclient.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class InboxDataSource {
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	// column dalam tabel inbox
	private String[] allColumns;

	public static final String TABLE_NAME = "t_inbox";

	// constructor, inisialisasi datasource
	public InboxDataSource(Context context) {
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

	// mengambil seluruh isi table inbox pada sqlite
	public List<InboxEntity> getAll() {
		List<InboxEntity> resultList = new ArrayList<InboxEntity>();

		Cursor cursor = database.query(InboxDataSource.TABLE_NAME, allColumns,
				null, null, null, null, "date DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			InboxEntity result = cursorToEntity(cursor);
			resultList.add(result);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return resultList;
	}

	// mengambil result berdasarkan id
	public InboxEntity getById(int id) {
		InboxEntity result = null;

		Cursor cursor = database.query(InboxDataSource.TABLE_NAME, allColumns,
				allColumns[0] + " = ?", new String[] { String.valueOf(id) },
				null, null, null);

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			result = cursorToEntity(cursor);
		}

		cursor.close();
		return result;
	}
	
	public InboxEntity getWhere(String where, String[] whereArgs) {
		InboxEntity result = null;

		Cursor cursor = database.query(InboxDataSource.TABLE_NAME, allColumns,
				where , whereArgs, null, null, null);

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			result = cursorToEntity(cursor);
		}

		cursor.close();
		return result;
	}
	
	public void update(InboxEntity inbox) {
		ContentValues values = new ContentValues();
		values.put(allColumns[1], inbox.getSubject());
		values.put(allColumns[2], inbox.getFrom());
		values.put(allColumns[3], inbox.getTo());
		values.put(allColumns[4], inbox.getDate());
		values.put(allColumns[5], (inbox.isRead() == true)? 1:0);
		database.update(InboxDataSource.TABLE_NAME, values, allColumns[0] + " = " + inbox.getId(), null);
	}

	// method untuk mengubah object cursor kedalam bentuk Object Result
	private InboxEntity cursorToEntity(Cursor cursor) {
		InboxEntity inbox = new InboxEntity();
		inbox.setId(cursor.getInt(cursor.getColumnIndex(allColumns[0])));
		inbox.setSubject(cursor.getString(cursor.getColumnIndex(allColumns[1])));
		inbox.setFrom(cursor.getString(cursor.getColumnIndex(allColumns[2])));
		inbox.setTo(cursor.getString(cursor.getColumnIndex(allColumns[3])));
		inbox.setDate(cursor.getString(cursor.getColumnIndex(allColumns[4])));
		int read = cursor.getInt(cursor.getColumnIndex(allColumns[5]));
		inbox.setRead((read == 0) ? false : true);
		return inbox;
	}
}
