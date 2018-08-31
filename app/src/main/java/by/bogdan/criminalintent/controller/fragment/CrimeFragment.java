package by.bogdan.criminalintent.controller.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import by.bogdan.criminalintent.R;
import by.bogdan.criminalintent.model.Crime;
import by.bogdan.criminalintent.model.CrimeLab;
import by.bogdan.criminalintent.utils.PictureUtils;
import by.bogdan.criminalintent.utils.TextChangedWatcher;

import static by.bogdan.criminalintent.utils.DateUtils.fixTime;
import static by.bogdan.criminalintent.utils.DateUtils.get24formatHours;
import static by.bogdan.criminalintent.utils.DateUtils.getFormattedDate;

public class CrimeFragment extends Fragment {

    public static final String EXTRA_CRIME_DELETED =
            "by.bogdan.criminalintent.crime_deleted";
    public static final String EXTRA_CRIME_DELETED_ID =
            "by.bogdan.criminalintent.crime_deleted_id";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_IMAGE = "DialogImage";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_CONTACTS_PERMISSIONS = 3;
    private static final int REQUEST_PHOTO = 4;

    private EditText mTitleField;
    private CheckBox mSolvedCheckBox;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    private Crime mCrime;
    private Button mCallSuspectButton;
    private String mContactPhoneNumber;
    private String mContactId;
    private boolean hasPermissions = true;
    private File mPhotoFile;

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
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        this.mCrime = crimeLab.getByUuid(crimeId);
        this.mPhotoFile = crimeLab.getPhotoFile(mCrime);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);
    }

    private void deleteCrime() {
        Intent intent = new Intent();
        intent.putExtra(CrimeFragment.EXTRA_CRIME_DELETED, true);
        intent.putExtra(CrimeFragment.EXTRA_CRIME_DELETED_ID, this.mCrime.getId());
        getActivity().setResult(Activity.RESULT_OK, intent);

        getActivity().finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime: {
                deleteCrime();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_crime, container, false);
        final Intent selectSuspectIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mSuspectButton = view.findViewById(R.id.suspect_button);
        mSuspectButton.setOnClickListener(button -> {
            startActivityForResult(selectSuspectIntent, REQUEST_CONTACT);
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        mCallSuspectButton = view.findViewById(R.id.call_suspect_button);
        mCallSuspectButton.setOnClickListener(button -> {
            Intent callSuspectIntent = new Intent(Intent.ACTION_DIAL,
                    Uri.parse(String.format("tel:%s", this.mContactPhoneNumber)));
            startActivity(callSuspectIntent);
        });

        if (mCrime.getSuspect() != null) {
            this.setContactPhoneNumberByName(mCrime.getSuspect());
        }

        if (this.mContactPhoneNumber == null || this.mContactPhoneNumber.isEmpty()) {
            mCallSuspectButton.setEnabled(false);
        }

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(selectSuspectIntent,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = view.findViewById(R.id.crime_camera_button);
        mPhotoButton.setOnClickListener(button -> {
            startActivityForResult(captureImage, REQUEST_PHOTO);
        });

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoView = view.findViewById(R.id.crime_photo);
        mPhotoView.setEnabled(this.mCrime.getPhotoFilename() != null);
        mPhotoView.setOnClickListener(photoView -> {
            FragmentManager fragmentManager = getFragmentManager();
            CrimeImageFragment crimeImageFragment = CrimeImageFragment
                    .newInstance(mPhotoFile.getPath());
            crimeImageFragment.show(Objects.requireNonNull(fragmentManager), DIALOG_IMAGE);
        });
        updatePhotoView();

        mReportButton = view.findViewById(R.id.report_button);
        mReportButton.setOnClickListener(button -> {
            Intent i = ShareCompat.IntentBuilder.from(getActivity())
                    .setSubject(getString(R.string.crime_report_subject))
                    .setText(getCrimeReport())
                    .addEmailTo(getString(R.string.send_report))
                    .setType("text/plain")
                    .getIntent();
            startActivity(i);
        });

        mTitleField = view.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());

        mTimeButton = view.findViewById(R.id.crime_time);
        mTimeButton.setOnClickListener((View v) -> {
            FragmentManager fragmentManager = getFragmentManager();
            TimePickerFragment dialog = TimePickerFragment.newInstance(this.mCrime.getMinutes(),
                    get24formatHours(this.mCrime.getDate()));
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
                (charSequence, i, i1, i2) -> mCrime.setTitle(charSequence.toString())
        );

        return view;
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
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
                this.mCrime.setDate(fixTime(hours, this.mCrime.getDate()));
                break;
            case REQUEST_CONTACT: {
                if (data != null) {
                    Uri contactUri = data.getData();
                    String[] queryFields = {ContactsContract.Contacts.DISPLAY_NAME,
                            ContactsContract.Contacts._ID
                    };
                    try (Cursor cursor = getActivity().getContentResolver()
                            .query(contactUri, queryFields, null, null, null)) {
                        if (cursor.getCount() == 0) return;
                        cursor.moveToFirst();
                        String suspect = cursor.getString(0);
                        mCrime.setSuspect(suspect);
                        mSuspectButton.setText(suspect);
                        this.mContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        setContactPhoneNumberById(this.mContactId);
                    }
                }
                break;
            }
            case REQUEST_PHOTO: {
                updatePhotoView();
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CONTACTS_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.setContactPhoneNumberById(this.mContactId);
                } else {
                    Toast.makeText(getActivity(), "Access denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setContactPhoneNumberByName(String contactName) {
        setContactPhoneNumber(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, contactName);
    }

    private void setContactPhoneNumberById(String contactId) {
        setContactPhoneNumber(ContactsContract.CommonDataKinds.Phone.CONTACT_ID, contactId);
    }

    private void setContactPhoneNumber(String field, String value) {
        if (hasPermissions) {
            try (Cursor cursor = getActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    field + " = ?",
                    new String[]{value}, null)) {
                if (cursor.getCount() == 0) return;
                cursor.moveToFirst();
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    this.mContactPhoneNumber = phoneNumber;
                    this.mCallSuspectButton.setEnabled(true);
                }
            }
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.READ_CONTACTS
            }, REQUEST_CONTACTS_PERMISSIONS);
        }
    }

    private void updateDate() {
        this.mDateButton.setText(getFormattedDate(this.mCrime.getDate()));
        String hoursStr = new SimpleDateFormat("HH", Locale.US).format(this.mCrime.getDate());
        String minutesStr = new SimpleDateFormat("mm", Locale.US).format(this.mCrime.getDate());
        this.mTimeButton.setText(this.getString(R.string.time_button_text, hoursStr, minutesStr));
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = new SimpleDateFormat(dateFormat, Locale.US).format(mCrime.getDate());
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        return getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).update(mCrime);
    }
}
