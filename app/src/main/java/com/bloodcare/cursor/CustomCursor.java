package com.bloodcare.cursor;

import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQuery;

import static com.bloodcare.utility.CommonUtil.isNullOrEmpty;

public class CustomCursor extends SQLiteCursor {

	@SuppressWarnings("deprecation")
	public CustomCursor(SQLiteDatabase db, SQLiteCursorDriver masterQuery, String editTable, SQLiteQuery query) {
		super(db, masterQuery, editTable, query);
	}

	public String getCursorStringValue(String column) {
		if(isNullOrEmpty(column)) {
			return null;
		}
		try {
			int index = getColumnIndex(column);
			if(index < 0) {
				return null;
			}
			return getString(index);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public long getCursorLongValue(String column) {
		if(isNullOrEmpty(column)) {
			return 0;
		}
		try {
			int index = getColumnIndex(column);
			if(index < 0) {
				return 0;
			}
			return getLong(index);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int getCursorIntValue(String column) {
		if(isNullOrEmpty(column)) {
			return 0;
		}
		try {
			int index = getColumnIndex(column);
			if(index < 0) {
				return 0;
			}
			return getInt(index);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public double getCursorDoubleValue(String column) {
		if(isNullOrEmpty(column)) {
			return 0.0;
		}
		try {
			int index = getColumnIndex(column);
			if(index < 0) {
				return 0.0;
			}
			return getDouble(index);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0.0;
	}

	public float getCursorFloatValue(String column) {
		if(isNullOrEmpty(column)) {
			return 0.0f;
		}
		try {
			int index = getColumnIndex(column);
			if(index < 0) {
				return 0.0f;
			}
			return getFloat(index);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0.0f;
	}

	public boolean getCursorBooleanValue(String column) {
		if(isNullOrEmpty(column)) {
			return false;
		}
		try {
			int index = getColumnIndex(column);
			///if(index < 0) {
			//	return false;
			//}
			return (index >= 0 && getInt(index) > 0);
			//int intValue = getInt(index);
			//if(intValue > 0) {
			//	return true;
			//} else {
			//	return false;
			//}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
