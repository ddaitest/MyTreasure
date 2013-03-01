package com.ddai.lib.reflectiondb;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public enum QCDataBase {

	INSTANCE;

	private static volatile SQLiteDatabase db;
	private static volatile SQLiteDatabase rdb;

	private final static Object _writeLock = new Object();

	private QCDataBase(){

	}

	public static QCDataBase getInstance(QCDBHelper helper) {
		if (db == null || rdb == null) {
			db = (helper.getWritableDatabase());
			rdb = helper.getReadableDatabase();
		}
		return INSTANCE;
	}

	public Cursor rawQuery(String sql, String[] selectionArgs) {
		return rdb.rawQuery(sql, selectionArgs);
	}

	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		return rdb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	}

	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
		return rdb.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
	}

	public Cursor query(boolean distinct, String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {
		return rdb.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
	}

	public long insert(String table, String nullColumnHack, ContentValues values) {
		synchronized (_writeLock) {
			return getDb().insert(table, nullColumnHack, values);
		}
	}

	public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		synchronized (_writeLock) {
			return getDb().update(table, values, whereClause, whereArgs);
		}
	}

	public void execSQL(String sql) {
		synchronized (_writeLock) {
			getDb().execSQL(sql);
		}
	}

	public int delete(String table, String whereClause, String[] whereArgs) {
		synchronized (_writeLock) {
			return getDb().delete(table, whereClause, whereArgs);
		}
	}

	public SQLiteDatabase getDb() {
		return db;
	}
}
