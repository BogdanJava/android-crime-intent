package by.bogdan.criminalintent.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import by.bogdan.criminalintent.dao.CrimeBaseHelper;
import by.bogdan.criminalintent.dao.CrimeCursorWrapperImpl;
import by.bogdan.criminalintent.dao.CrimeRepository;

import static by.bogdan.criminalintent.dao.CrimeDbSchema.Columns.DATE;
import static by.bogdan.criminalintent.dao.CrimeDbSchema.Columns.SOLVED;
import static by.bogdan.criminalintent.dao.CrimeDbSchema.Columns.SUSPECT;
import static by.bogdan.criminalintent.dao.CrimeDbSchema.Columns.TITLE;
import static by.bogdan.criminalintent.dao.CrimeDbSchema.Columns.UUID;
import static by.bogdan.criminalintent.dao.CrimeDbSchema.CrimeTable.NAME;

public class CrimeLab implements CrimeRepository {

    private static CrimeLab sCrimeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context) {
        this.mContext = context.getApplicationContext();
        this.mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return CrimeLab.sCrimeLab;
    }

    private static ContentValues getContentValues(Crime c) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(UUID, c.getId().toString());
        contentValues.put(SOLVED, c.isSolved());
        contentValues.put(DATE, c.getDate().getTime());
        contentValues.put(TITLE, c.getTitle());
        contentValues.put(SUSPECT, c.getSuspect());
        return contentValues;
    }

    @Override
    public void insert(Crime crime) {
        mDatabase.insert(NAME, null, getContentValues(crime));
    }

    @Override
    public void update(Crime crime) {
        String uuid = crime.getId().toString();
        ContentValues contentValues = getContentValues(crime);
        mDatabase.update(NAME, contentValues, UUID + " = ?", new String[]{uuid});
    }

    @Override
    public List<Crime> getAll() {
        List<Crime> crimes = new ArrayList<>();
        try (CrimeCursorWrapperImpl cursor = queryCrimes(null, null)) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                crimes.add(cursor.getCrime());
                cursor.moveToNext();
            }
        }
        return crimes;
    }

    @Override
    public Crime getByUuid(java.util.UUID uuid) {
        try (CrimeCursorWrapperImpl cursor = queryCrimes(UUID + " = ?", new String[]{uuid.toString()})) {
            if (cursor.getCount() == 0) return null;
            cursor.moveToFirst();
            return cursor.getCrime();
        }
    }

    @Override
    public boolean delete(java.util.UUID uuid) {
        return mDatabase.delete(NAME, UUID + " = ?", new String[]{uuid.toString()}) != 0;
    }

    private CrimeCursorWrapperImpl queryCrimes(String whereClause, String[] whereArgs) {
        return new CrimeCursorWrapperImpl(
                mDatabase.query(NAME, null, whereClause, whereArgs, null, null, null)
        );
    }
}
