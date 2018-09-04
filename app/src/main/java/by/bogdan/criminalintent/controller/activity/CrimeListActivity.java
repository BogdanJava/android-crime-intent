package by.bogdan.criminalintent.controller.activity;

import android.support.v4.app.Fragment;

import by.bogdan.criminalintent.R;
import by.bogdan.criminalintent.controller.fragment.CrimeFragment;
import by.bogdan.criminalintent.controller.fragment.CrimeListFragment;
import by.bogdan.criminalintent.model.Crime;

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragment_container) != null) {
            CrimeFragment crimeFragment = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, crimeFragment)
                    .commit();
        } else {
            startActivity(
                    CrimePagerActivity.newCrimeIdIntent(this, crime.getId())
            );
        }
    }
}
