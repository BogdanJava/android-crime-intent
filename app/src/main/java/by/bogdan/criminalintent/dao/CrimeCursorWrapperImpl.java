package by.bogdan.criminalintent.dao;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;

import by.bogdan.criminalintent.model.Crime;

import static by.bogdan.criminalintent.dao.CrimeDbSchema.Columns.DATE;
import static by.bogdan.criminalintent.dao.CrimeDbSchema.Columns.SOLVED;
import static by.bogdan.criminalintent.dao.CrimeDbSchema.Columns.SUSPECT;
import static by.bogdan.criminalintent.dao.CrimeDbSchema.Columns.TITLE;
import static by.bogdan.criminalintent.dao.CrimeDbSchema.Columns.UUID;

public class CrimeCursorWrapperImpl extends CursorWrapper {

    public CrimeCursorWrapperImpl(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(UUID));
        String title = getString(getColumnIndex(TITLE));
        long date = getLong(getColumnIndex(DATE));
        int isSolved = getInt(getColumnIndex(SOLVED));
        String suspect = getString(getColumnIndex(SUSPECT));

        Crime crime = new Crime(java.util.UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSuspect(suspect);
        return crime;
    }

}
