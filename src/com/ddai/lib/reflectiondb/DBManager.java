package com.ddai.lib.reflectiondb;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.ddait.StoreMessage;

public enum DBManager {

	INSTANCE;

	static QCDataBase db;
	static QCDBHelper dbHelper;
	static Context context;

	private DBManager() {

	}

	public static synchronized DBManager getInstance(Context _context) {
		if ((dbHelper == null) || (db == null)) {
			dbHelper = new QCDBHelper(_context, DBString.DB_NAME, null,
					DBString.DB_VERSION);
			db = QCDataBase.getInstance(dbHelper);
		}
		context = _context;
		return INSTANCE;
	}

	public StoreMessage getSM(String query) {
		Cursor c = db.query(DBString.TABLE_TEST, null, "request=?",
				new String[] { query }, null, null, null);
		StoreMessage model = null;
		if (c.getCount() > 0) {
			c.moveToFirst();
			model = new StoreMessage();
			ReflectUtils.readObject(model, c);
		}
		c.close();
		return model;
	}

	public void saveSM(StoreMessage model) {
		replaceEntity(model);
	}
	public ArrayList<StoreMessage> getSMS() {
		ArrayList<StoreMessage> r = new ArrayList<StoreMessage>();
		Cursor c = db.query(DBString.TABLE_TEST, null, null, null, null, null,
				null);
		if (c.getCount() > 0) {
			StoreMessage sm;
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				sm = new StoreMessage();
				ReflectUtils.readObject(sm, c);
				r.add(sm);
			}
		}
		c.close();
		return r;
	}


	private boolean replaceEntity(Object object) {
		String tableName = ReflectUtils.getTableName(object.getClass());
		if (tableName != null) {
			ContentValues cv = ReflectUtils.getContentValuesFromObject(object);
			db.getDb().replace(tableName, null, cv);
		}
		return true;
	}

	private boolean saveEntity(Object object) {
		String tableName = ReflectUtils.getTableName(object.getClass());
		if (tableName != null) {
			ContentValues cv = ReflectUtils.getContentValuesFromObject(object);
			db.insert(tableName, null, cv);
		}
		return true;
	}
}
