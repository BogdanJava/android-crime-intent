package by.bogdan.criminalintent.controller.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import by.bogdan.criminalintent.R;
import by.bogdan.criminalintent.model.Crime;
import by.bogdan.criminalintent.model.CrimeLab;
import by.bogdan.criminalintent.utils.TextChangedWatcher;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) Objects.requireNonNull(getArguments()).getSerializable(ARG_CRIME_ID);
        this.mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_crime, container, false);
        mTitleField = view.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());

        mTimeButton = view.findViewById(R.id.crime_time);
        mTimeButton.setOnClickListener((View v) -> {
            FragmentManager fragmentManager = getFragmentManager();
            TimePickerFragment dialog = TimePickerFragment.newInstance(this.mCrime.getMinutes(),
                    this.mCrime.getHours());
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
            dialog.show(Objects.requireNonNull(fragmentManager), DIALOG_TIME);
        });

        mDateButton = view.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener((View v) -> {
            FragmentManager fragmentManager = getFragmentManager();
            DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
            dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
            dialog.show(Objects.requireNonNull(fragmentManager), DIALOG_DATE);
        });

        mSolvedCheckBox = view.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(
                (buttonView, isChecked) -> mCrime.setSolved(isChecked));

        mTitleField.addTextChangedListener((TextChangedWatcher)
                (charSequence, i, i1, i2) -> {
                    mCrime.setTitle(charSequence.toString());
                }
        );

        return view;
    }

    private String getFormattedDate(Date date) {
        final SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.US);
        return df.format(date);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_DATE:
                Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                this.mCrime.setDate(date);
                break;
            case REQUEST_TIME:
                int minutes = data.getIntExtra(TimePickerFragment.EXTRA_MINUTES, 0);
                int hours = data.getIntExtra(TimePickerFragment.EXTRA_HOURS, 0);
                this.mCrime.setHours(hours);
                this.mCrime.setMinutes(minutes);
                break;
            default:
        }
        this.updateDate();
    }

    private void updateDate() {
        this.mDateButton.setText(getFormattedDate(this.mCrime.getDate()));
        String hoursStr = String.valueOf(this.mCrime.getHours());
        String minutesStr = String.valueOf(this.mCrime.getMinutes());
        if (this.mCrime.getHours() < 10) hoursStr = "0" + hoursStr;
        if (this.mCrime.getMinutes() < 10) minutesStr = "0" + minutesStr;
        this.mTimeButton.setText(this.getString(R.string.time_button_text, hoursStr, minutesStr));
    }

}
