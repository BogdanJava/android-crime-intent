package by.bogdan.criminalintent.model;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate = new Date();
    private boolean mSolved;

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID uuid) {
        this.mId = uuid;
        this.mDate = new Date();
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
        return obj != null && obj instanceof Crime && this.mId.equals(((Crime) obj).mId);
    }

    public int getHours() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.mDate);
        return calendar.get(Calendar.HOUR);
    }

    public void setHours(int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.mDate);
        calendar.set(Calendar.HOUR, hours);
        this.mDate = calendar.getTime();
    }

    public int getMinutes() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.mDate);
        return calendar.get(Calendar.MINUTE);
    }

    public void setMinutes(int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.mDate);
        calendar.set(Calendar.MINUTE, minutes);
        this.mDate = calendar.getTime();
    }

    public Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.mDate);
        return calendar;
    }
}
