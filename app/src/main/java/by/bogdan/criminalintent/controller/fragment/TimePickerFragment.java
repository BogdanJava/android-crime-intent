package by.bogdan.criminalintent.controller.fragment;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Objects;

import by.bogdan.criminalintent.R;

import static android.app.Activity.RESULT_OK;

public class TimePickerFragment extends DialogFragment {
    public static final String EXTRA_MINUTES =
            "by.bogdan.criminalintent.minutes";
    public static final String EXTRA_HOURS =
            "by.bogdan.criminalintent.hours";
    private static final String ARG_TIME = "time";
    private TimePicker mTimePicker;

    public static TimePickerFragment newInstance(int minutes, int hours) {
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_MINUTES, minutes);
        bundle.putInt(EXTRA_HOURS, hours);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void sendResult(int resultCode, int minutes, int hours) {
        if (this.getTargetFragment() == null) return;
        Intent intent = new Intent();
        intent.putExtra(EXTRA_HOURS, hours);
        intent.putExtra(EXTRA_MINUTES, minutes);

        this.getTargetFragment()
                .onActivityResult(this.getTargetRequestCode(), resultCode, intent);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = this.getArguments();
        args = args != null ? args : new Bundle();
        int hours = args.getInt(EXTRA_HOURS, 0);
        int minutes = args.getInt(EXTRA_MINUTES, 0);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        this.mTimePicker = view.findViewById(R.id.dialog_time_time_picker);
        this.mTimePicker.setHour(hours);
        this.mTimePicker.setMinute(minutes);
        return new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(view)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        int hour = this.mTimePicker.getHour();
                        int minute = this.mTimePicker.getMinute();

                        sendResult(RESULT_OK, minute, hour);
                    }
                }).create();

    }
}
