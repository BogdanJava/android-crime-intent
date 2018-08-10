package by.bogdan.criminalintent.utils;

import android.text.Editable;
import android.text.TextWatcher;

@FunctionalInterface
public interface TextChangedWatcher extends TextWatcher {
    @Override
    default void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    default void afterTextChanged(Editable editable) {}
}
