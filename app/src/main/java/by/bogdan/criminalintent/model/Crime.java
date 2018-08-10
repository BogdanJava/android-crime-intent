package by.bogdan.criminalintent.model;

import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID mId = UUID.randomUUID();
    private String mTitle;
    private Date mDate = new Date();
    private boolean mSolved;

    public Crime() {
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Crime)) return false;
        return this.mId.equals(((Crime) obj).mId);
    }
}
