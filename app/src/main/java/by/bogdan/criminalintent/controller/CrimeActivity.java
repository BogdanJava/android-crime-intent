package by.bogdan.criminalintent.controller;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import by.bogdan.criminalintent.R;
import by.bogdan.criminalintent.controller.fragment.CrimeFragment;
import lombok.val;
import lombok.var;

public class CrimeActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime);

        val fragmentManager = getSupportFragmentManager();
        var fragment = fragmentManager.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new CrimeFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
