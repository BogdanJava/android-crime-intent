package by.bogdan.criminalintent.controller.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.Objects;

public interface IDialogFragment {

    default void hideDialog(FragmentManager fragmentManager, Fragment targetFragment) {
        Objects.requireNonNull(fragmentManager).beginTransaction()
                .remove(targetFragment)
                .commit();
    }
}
