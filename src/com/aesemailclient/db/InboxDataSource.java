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
	public static final String COLUMN_ID = "id_inbox";
	public static final String COLUMN_SUBJECT = "subject";
	public static final String COLUMN_FROM = "from_add";
	public static final String COLUMN_TO = "to_add";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_ISREAD = "is_read";
	
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
				null, null, null, null, "datetime(date) DESC");

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
				COLUMN_ID + " = ?", new String[] { String.valueOf(id) },
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
				where, whereArgs, null, null, null);

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			result = cursorToEntity(cursor);
		}

		cursor.close();
		return result;
	}

	// method untuk menyimpan InboxEntity
	// object Result di convert ke dalam bentuk yang dapat dikenali oleh SQLLite
	// helper
	public InboxEntity save(InboxEntity inbox) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_SUBJECT, inbox.getSubject());
		values.put(COLUMN_FROM, inbox.getFrom());
		values.put(COLUMN_TO, inbox.getTo());
		values.put(COLUMN_DATE, inbox.getDate());
		values.put(COLUMN_CONTENT, inbox.getContent());
		values.put(COLUMN_ISREAD, (inbox.isRead() == true) ? 1 : 0);
		long insertId = database.insert(InboxDataSource.TABLE_NAME, null,
				values);
		Cursor cursor = database.query(InboxDataSource.TABLE_NAME, allColumns,
				COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		InboxEntity newResult = cursorToEntity(cursor);
		cursor.close();
		return newResult;
	}

	public void update(InboxEntity inbox) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_SUBJECT, inbox.getSubject());
		values.put(COLUMN_FROM, inbox.getFrom());
		values.put(COLUMN_TO, inbox.getTo());
		values.put(COLUMN_DATE, inbox.getDate());
		values.put(COLUMN_CONTENT, inbox.getContent());
		values.put(COLUMN_ISREAD, (inbox.isRead() == true) ? 1 : 0);
		database.update(InboxDataSource.TABLE_NAME, values, allColumns[0]
				+ " = " + inbox.getId(), null);
	}

	// method untuk mengubah object cursor kedalam bentuk Object Result
	private InboxEntity cursorToEntity(Cursor cursor) {
		InboxEntity inbox = new InboxEntity();
		inbox.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
		inbox.setSubject(cursor.getString(cursor.getColumnIndex(COLUMN_SUBJECT)));
		inbox.setFrom(cursor.getString(cursor.getColumnIndex(COLUMN_FROM)));
		inbox.setTo(cursor.getString(cursor.getColumnIndex(COLUMN_TO)));
		inbox.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
		inbox.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
		int read = cursor.getInt(cursor.getColumnIndex(COLUMN_ISREAD));
		inbox.setRead((read == 0) ? false : true);
		return inbox;
	}
}
