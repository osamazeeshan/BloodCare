package com.bloodcare.dao;

import android.content.ContentValues;
import android.content.Context;

import com.bloodcare.cursor.CustomCursor;
import com.bloodcare.utility.CustomSQLiteOpenHelper;

/**
 * Created by osamazeeshan on 29/08/2018.
 */

public abstract class BaseDao {

    public static final String COLUMN_ID = "_id";

    protected CustomSQLiteOpenHelper mDbHelper;
    public Context mContext = null;

    public static final int SQL_INSERT = 0;
    public static final int SQL_UPDATE = 1;
    public static final int SQL_DELETE = 2;

    protected abstract String getCollectionName();


    public BaseDao(Context context) {
        mContext = context;
        mDbHelper = CustomSQLiteOpenHelper.getInstance(context);
    }

    public long insert(ContentValues values) {
        return mDbHelper.insert(getCollectionName(), values);
    }

    public long updateByClause(ContentValues values, String clause, String[] clauseArg) {
        return mDbHelper.update(getCollectionName(), values, clause, clauseArg);
    }

    public CustomCursor findByClause(String clause, String[] clauseArg) {
        return mDbHelper.query(getCollectionName(), null, clause, clauseArg, null, null);
    }

}
