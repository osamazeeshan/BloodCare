package com.bloodcare.cursor;

import android.database.Cursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

public class CustomCursorFactory implements SQLiteDatabase.CursorFactory {

	@Override
	public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
		return new CustomCursor(db, masterQuery, editTable, query);
	}

}
