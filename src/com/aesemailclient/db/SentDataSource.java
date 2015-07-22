package com.aesemailclient.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SentDataSource {

	public static final String COLUMN_ID = "id_sent";
	public static final String COLUMN_SUBJECT = "subject";
	public static final String COLUMN_FROM = "from_address";
	public static final String COLUMN_TO = "to_address";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_DATE = "date";
	public static final String TABLE_NAME = "t_sent";

	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	// column dalam tabel sent
	private String[] allColumns;

	public SentDataSource(Context context) {
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

	// mengambil seluruh isi table inbox pada sqlite
	public List<SentEntity> getAll() {
		List<SentEntity> resultList = new ArrayList<SentEntity>();

		Cursor cursor = database.query(SentDataSource.TABLE_NAME, allColumns,
				null, null, null, null, "datetime(date) DESC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			SentEntity result = cursorToEntity(cursor);
			resultList.add(result);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return resultList;
	}

	// mengambil result berdasarkan id
	public SentEntity getById(int id) {
		SentEntity result = null;

		Cursor cursor = database.query(SentDataSource.TABLE_NAME, allColumns,
				COLUMN_ID + " = ?", new String[] { String.valueOf(id) }, null,
				null, null);

		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			result = cursorToEntity(cursor);
		}

		cursor.close();
		return result;
	}

	public SentEntity save(SentEntity sent) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_SUBJECT, sent.getSubject());
		values.put(COLUMN_FROM, sent.getFrom());
		values.put(COLUMN_TO, sent.getTo());
		values.put(COLUMN_DATE, sent.getDate());
		values.put(COLUMN_CONTENT, sent.getContent());
		long insertId = database.insert(SentDataSource.TABLE_NAME, null,
				values);
		Cursor cursor = database.query(SentDataSource.TABLE_NAME, allColumns,
				COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		SentEntity newResult = cursorToEntity(cursor);
		cursor.close();
		return newResult;
	}

	public void update(SentEntity sent) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_SUBJECT, sent.getSubject());
		values.put(COLUMN_FROM, sent.getFrom());
		values.put(COLUMN_TO, sent.getTo());
		values.put(COLUMN_DATE, sent.getDate());
		values.put(COLUMN_CONTENT, sent.getContent());
		database.update(SentDataSource.TABLE_NAME, values,
				SentDataSource.COLUMN_ID + " = " + sent.getId(), null);
	}

	// method untuk mengubah object cursor kedalam bentuk Object Result
	private SentEntity cursorToEntity(Cursor cursor) {
		SentEntity sent = new SentEntity();
		sent.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
		sent.setSubject(cursor.getString(cursor.getColumnIndex(COLUMN_SUBJECT)));
		sent.setFrom(cursor.getString(cursor.getColumnIndex(COLUMN_FROM)));
		sent.setTo(cursor.getString(cursor.getColumnIndex(COLUMN_TO)));
		sent.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
		sent.setDate(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
		return sent;
	}

}
