package com.bloodcare.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bloodcare.cursor.CustomCursor;
import com.bloodcare.cursor.CustomCursorFactory;
import com.bloodcare.dao.UserDao;


public class CustomSQLiteOpenHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "BloodCareApp.db";
	private static final int DATABASE_VERSION = 2;
	private static CustomSQLiteOpenHelper mInstance = null;

	private CustomSQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, new CustomCursorFactory(), DATABASE_VERSION);
		getWriteDB();
	}

	public static CustomSQLiteOpenHelper getInstance(Context context) {
		if(mInstance == null) {
			mInstance = new CustomSQLiteOpenHelper(context);
		}
		return mInstance;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		if(database == null) {
			return;
		}
		try {
			createTables(database);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		if(database == null) {
			return;
		}
		try {
			Log.w(getClass().getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
			dropTables(database);
			onCreate(database);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public SQLiteDatabase getWriteDB() {
		try {
			return getWritableDatabase();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public SQLiteDatabase getReadDB() {
		try {
			return getReadableDatabase();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void beginTransaction() {
		try {
			getWriteDB().beginTransaction();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void endTransaction(boolean isSuccessful) {
		try {
			SQLiteDatabase database= getWriteDB();
			if(database != null) {
				if(isSuccessful) {
					database.setTransactionSuccessful();
				}
				database.endTransaction();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	//public CustomCursor query(String tableName, String clause) {
	//    if(CommonUtil.isNullOrEmpty(tableName)) {
	//        return null;
	//    }
	//    try {
	//        return (CustomCursor) getReadDB().query(tableName, null, clause, null, null, null, null, null);
	//    } catch(Exception e) {
	//        e.printStackTrace();
	//    }
	//    return null;
	//}

	public CustomCursor query(String tableName, String[] columns, String clause, String[] clauseArg, String sortOrder, String limit) {
		if(CommonUtil.isNullOrEmpty(tableName)) {
			return null;
		}
		try {
			return (CustomCursor) getReadDB().query(tableName, columns, clause, clauseArg, null, null, sortOrder, limit);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public long insert(String tableName, ContentValues values) {
		if(CommonUtil.isNullOrEmpty(tableName) || (values == null)) {
			return 0;
		}
		try {
			return getWriteDB().insert(tableName, null, values);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public long update(String tableName, ContentValues values, String clause, String[] clauseArg) {
		if(CommonUtil.isNullOrEmpty(tableName) || (values == null)) {
			return 0;
		}
		try {
			return getWriteDB().update(tableName, values, clause, clauseArg);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public long delete(String tableName, String clause, String[] clauseArg) {
		if(CommonUtil.isNullOrEmpty(tableName)) {
			return 0;
		}
		try {
			return getWriteDB().delete(tableName, clause, clauseArg);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public CustomCursor rawQuery(String query, String[] arg) {
		if(CommonUtil.isNullOrEmpty(query)) {
			return null;
		}
		try {
			return (CustomCursor) getReadDB().rawQuery(query, arg);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	//public void closeDB() {
	//	try {
	//		close();
	//	} catch(Exception e) {
	//		e.printStackTrace();
	//	}
	//}

	public boolean deleteTables() {
		try {
			getWriteDB().execSQL("delete from " + UserDao.COLLECTION_NAME);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void dropTables(SQLiteDatabase database) {
		if(database == null) {
			return;
		}
		try {
			database.execSQL("DROP TABLE IF EXISTS " + UserDao.COLLECTION_NAME);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void createTables(SQLiteDatabase database) {
		if(database == null) {
			return;
		}
		try {
			database.execSQL(UserDao.getTableSchema());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
