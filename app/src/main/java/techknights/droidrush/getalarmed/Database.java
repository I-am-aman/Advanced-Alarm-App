package techknights.droidrush.getalarmed;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    public static final String dbName = "Alarm.db";
    public static final String setAlarmTable = "setAlarmTable";
    public static final String col_1 = "requestCode";
    public static final String col_2 = "setTime";
    public static final String col_3 = "alarmName";
    public static final String col_4 = "ringtone";
    public static final String col_5 = "whatsIt";
    public static final String col_6 = "impLevel";

    public static final String mainTable = "mainTable";
    public static final String mainCol_1 = "requestCode";
    public static final String mainCol_2 = "setTime";

    public Database(Context context) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + setAlarmTable +"( requestCode INTEGER PRIMARY KEY, setTime TEXT " +
                ", alarmName TEXT, ringtone TEXT,whatsIt TEXT,impLevel FLOAT)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + mainTable +"( requestCode INTEGER PRIMARY KEY, setTime TEXT )");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+setAlarmTable);
        db.execSQL("DROP TABLE IF EXISTS "+mainTable);
        onCreate(db);
    }

    public boolean alarmInsertData(int code,String time,String name,String ring ,String what,float imp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_1,code);
        contentValues.put(col_2,time);
        contentValues.put(col_3,name);
        contentValues.put(col_4,ring);
        contentValues.put(col_5,what);
        contentValues.put(col_6,imp);
        long res = db.insert(setAlarmTable,null,contentValues);
        if(res == -1)
            return false;
        else
            return true;
    }

    public boolean mainInsertData(int code,String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(mainCol_1,code);
        contentValues.put(mainCol_2,time);
        long res = db.insert(mainTable,null,contentValues);
        if(res == -1)
            return false;
        else
            return true;
    }



    public String alarmGetTime(int reqCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =db.query(setAlarmTable, new String[]{col_1, col_2, col_3,col_4,col_5,col_6}, col_1 + "=?" ,
                new String[]{String.valueOf(reqCode)}, null, null, null, null);
        String time="";
        if (res!=null) {
            res.moveToFirst();
            time = res.getString(res.getColumnIndex(col_2));
        }
        return time;
    }

    public String alarmGetName(int reqCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.query(setAlarmTable, new String[]{col_1, col_2, col_3,col_4,col_5,col_6}, col_1 + "=?" ,
                new String[]{String.valueOf(reqCode)}, null, null, null, null);
        String name=null;
        if (res!=null) {
            res.moveToFirst();
            name = res.getString(res.getColumnIndex(col_3));
        }
        return name;
    }

    public String alarmGetRingtone(int reqCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =db.query(setAlarmTable, new String[]{col_1, col_2, col_3,col_4,col_5,col_6}, col_1 + "=?" ,
                new String[]{String.valueOf(reqCode)}, null, null, null, null);
        String ring=null;
        if (res!=null) {
            res.moveToFirst();
            ring = res.getString(res.getColumnIndex(col_4));
        }
        return ring;
    }

    public String alarmGetWhatsIt(int reqCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.query(setAlarmTable, new String[]{col_1, col_2, col_3,col_4,col_5,col_6}, col_1 + "=?" ,
                new String[]{String.valueOf(reqCode)}, null, null, null, null);
        String what=null;
        if (res!=null) {
            res.moveToFirst();
            what = res.getString(res.getColumnIndex(col_5));
        }
        return what;
    }

    public float alarmGetImp(int reqCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =db.query(setAlarmTable, new String[]{col_1, col_2, col_3,col_4,col_5,col_6}, col_1 + "=?" ,
                new String[]{String.valueOf(reqCode)}, null, null, null, null);
        float imp=0;
        if ( res!=null) {
            res.moveToFirst();
            imp = res.getFloat(res.getColumnIndex(col_6));
        }
        return imp;
    }
}

