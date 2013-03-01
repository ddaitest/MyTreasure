package com.ddai.lib.reflectiondb;

public interface DBString {

	public static String TABLE_TEST = "Test";
	public static String[] CREATE_TABLES = {
			"CREATE TABLE "
					+ TABLE_TEST
					+ " (local_id integer primary key autoincrement,request text,response text,timestamp integer);",
			"CREATE UNIQUE INDEX request_idx ON " + TABLE_TEST + "(request);" };

	public static String[] TABLES = { TABLE_TEST };
	public static String DB_NAME = "QCPictureForAndroid.db";
	public static int DB_VERSION = 2;
}
