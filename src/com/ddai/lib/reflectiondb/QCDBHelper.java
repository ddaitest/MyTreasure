/**
 * 
 */
package com.ddai.lib.reflectiondb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author daining
 * 
 */
public class QCDBHelper extends SQLiteOpenHelper {

	public QCDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DBString.DB_NAME, factory, DBString.DB_VERSION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		if (SQLiteDatabase.findEditTable("Setting") != null) {
			String dropString = "DROP TABLE IF EXISTS ";
			for (String sql : DBString.TABLES) {
				sql = dropString + sql;
				db.execSQL(sql);
			}
		}
		for (String sql : DBString.CREATE_TABLES) {
			db.execSQL(sql);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// if (oldVersion == 311) {
		// db.execSQL("ALTER TABLE " + DBString.TABLE_ALBUM
		// + " ADD COLUMN addition text;");
		// } else if (oldVersion < 311) {
		final String dropString = "DROP TABLE IF EXISTS ";
		for (String sql : DBString.TABLES) {
			sql = dropString + sql;
			db.execSQL(sql);
		}
		onCreate(db);
		// }

	}
}
