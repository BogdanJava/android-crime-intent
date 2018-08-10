package by.bogdan.criminalintent.controller.activity;

import android.support.v4.app.Fragment;

import by.bogdan.criminalintent.controller.fragment.CrimeListFragment;

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
