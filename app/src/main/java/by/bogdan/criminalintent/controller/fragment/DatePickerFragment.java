package by.bogdan.criminalintent.controller.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

import by.bogdan.criminalintent.R;

import static android.app.Activity.RESULT_OK;

public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE =
            "by.bogdan.criminalintent.date";

    private static final String ARG_DATE = "date";

    private DatePicker mDatePicker;
    private Button mOkButton;

    public static DatePickerFragment newInstance(Date date) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_DATE, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void sendResult(int resultCode, Date date) {
        if (this.getTargetFragment() == null) return;
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);

        this.getTargetFragment()
                .onActivityResult(this.getTargetRequestCode(), resultCode, intent);
    }

    private void hideDatePickerDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        Objects.requireNonNull(fragmentManager).beginTransaction()
                .remove(this)
                .commit();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Date date = (Date) Objects.requireNonNull(this.getArguments()).getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        this.mDatePicker = view.findViewById(R.id.dialog_date_date_picker);
        this.mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(view)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    int y = this.mDatePicker.getYear();
                    int m = this.mDatePicker.getMonth();
                    int d = this.mDatePicker.getDayOfMonth();

                    Date pickedDate = new GregorianCalendar(y, m, d).getTime();
                    sendResult(RESULT_OK, pickedDate);
                }).create();
    }

}
