package by.bogdan.criminalintent.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import by.bogdan.criminalintent.dao.CrimeBaseHelper;
import by.bogdan.criminalintent.dao.CrimeRepository;

import static by.bogdan.criminalintent.dao.CrimeDbSchema.Columns.DATE;
import static by.bogdan.criminalintent.dao.CrimeDbSchema.Columns.SOLVED;
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
        return contentValues;
    }

    public List<Crime> getCrimes() {
        return new ArrayList<>();
    }

    public Crime getCrime(UUID id) {
        return null;
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

    private Cursor queryCrimes(String whereClause, String[] whereArgs) {
        return mDatabase.query(NAME, null, whereClause, whereArgs, null, null, null);
    }
}
