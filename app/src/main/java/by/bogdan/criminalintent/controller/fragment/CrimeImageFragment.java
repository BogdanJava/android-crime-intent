package by.bogdan.criminalintent.controller.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.util.Objects;

import by.bogdan.criminalintent.R;
import by.bogdan.criminalintent.utils.PictureUtils;

public class CrimeImageFragment extends DialogFragment implements IDialogFragment {

    private static final String ARG_PHOTO_PATH = "photo_path";

    private ImageView mCrimePhoto;
    private String mPhotoPath;

    public static CrimeImageFragment newInstance(String photoPath) {
        if (photoPath == null) {
            throw new RuntimeException("photoPath cannot be null");
        }
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PHOTO_PATH, photoPath);
        CrimeImageFragment fragment = new CrimeImageFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);
        this.mPhotoPath = Objects.requireNonNull(getArguments()).getString(ARG_PHOTO_PATH);
        this.mCrimePhoto = view.findViewById(R.id.crime_photo_image_view);
        mCrimePhoto.setImageBitmap(getImageBitmap());

        return new AlertDialog.Builder(Objects.requireNonNull(getActivity()))
                .setView(view)
                .setTitle(R.string.image_dialog_title)
                .setPositiveButton(android.R.string.ok, ((dialog, which) -> {
                    this.hideDialog(getFragmentManager(), this);
                })).create();
    }

    private Bitmap getImageBitmap() {
        return PictureUtils.getScaledBitmap(mPhotoPath, getActivity());
    }
}
