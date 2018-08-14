package by.bogdan.criminalintent.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static by.bogdan.criminalintent.dao.CrimeDbSchema.Columns.DATE;
import static by.bogdan.criminalintent.dao.CrimeDbSchema.Columns.SOLVED;
import static by.bogdan.criminalintent.dao.CrimeDbSchema.Columns.SUSPECT;
import static by.bogdan.criminalintent.dao.CrimeDbSchema.Columns.TITLE;
import static by.bogdan.criminalintent.dao.CrimeDbSchema.Columns.UUID;

public class CrimeBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CrimeDbSchema.CrimeTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                UUID + ", " +
                TITLE + ", " +
                DATE + ", " +
                SOLVED + ", " +
                SUSPECT +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
