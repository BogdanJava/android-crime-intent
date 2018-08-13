package by.bogdan.criminalintent.controller.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.UUID;

import by.bogdan.criminalintent.R;
import by.bogdan.criminalintent.controller.fragment.CrimeFragment;
import by.bogdan.criminalintent.model.Crime;
import by.bogdan.criminalintent.model.CrimeLab;

public class CrimePagerActivity extends AppCompatActivity {
    private static final String EXTRA_CRIME_ID =
            "by.bogdan.criminalintent.crime_id";
    private ViewPager mViewPager;
    private CrimeLab mCrimeLab;

    public static Intent newCrimeIdIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = findViewById(R.id.activity_crime_pager_view_pager);
        mCrimeLab = CrimeLab.get(this);
        FragmentManager fragmentManager = getSupportFragmentManager();

        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimeLab.getCrimes().get(position);
                return CrimeFragment.newInstance(crime.getId());
            }

            @Override
            public int getCount() {
                return mCrimeLab.getCrimes().size();
            }
        });
        mViewPager.setCurrentItem(mCrimeLab.getCrimes().indexOf(mCrimeLab.getCrime(crimeId)));
    }

}
