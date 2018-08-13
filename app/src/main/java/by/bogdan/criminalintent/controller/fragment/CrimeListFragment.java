package by.bogdan.criminalintent.controller.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import by.bogdan.criminalintent.R;
import by.bogdan.criminalintent.controller.activity.CrimePagerActivity;
import by.bogdan.criminalintent.model.Crime;
import by.bogdan.criminalintent.model.CrimeLab;

public class CrimeListFragment extends Fragment {

    public static final int REQUEST_DELETE = 0;
    private static final String SAVED_SUBTITLE_VISIBLE =
            "subtitle";
    private Integer mLastClickedCrimePosition;
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private LinearLayout mEmptyCrimeListView;
    private boolean mSubtitleVisible;
    private Button mAddCrimeButton;

    private boolean crimeListNotEmpty() {
        return CrimeLab.get(getActivity()).getCrimes().size() != 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        this.mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        this.mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        this.mEmptyCrimeListView = view.findViewById(R.id.crime_list_empty);

        this.mAddCrimeButton = view.findViewById(R.id.empty_list_add_button);
        this.mAddCrimeButton.setOnClickListener(button -> {
            createNewCrimeActions();
        });
        updateUI();
        return view;
    }

    private void changeVisibilities() {
        boolean crimesPresent = crimeListNotEmpty();
        this.mEmptyCrimeListView.setVisibility(crimesPresent ? View.GONE : View.VISIBLE);
        this.mCrimeRecyclerView.setVisibility(crimesPresent ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, this.mSubtitleVisible);
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);

        if (!mSubtitleVisible) subtitle = null;

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        changeVisibilities();
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if (mAdapter == null) {
            this.mAdapter = new CrimeAdapter(crimes);
            this.mCrimeRecyclerView.setAdapter(this.mAdapter);
        } else {
            if (this.mLastClickedCrimePosition != null)
                this.mAdapter.notifyItemChanged(this.mLastClickedCrimePosition);
        }
        updateSubtitle();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime: {
                createNewCrimeActions();
                return true;
            }
            case R.id.menu_item_show_subtitle: {
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    private void createNewCrimeActions() {
        Crime crime = new Crime();
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        crimeLab.addCrime(crime);
        Intent intent = CrimePagerActivity.newCrimeIdIntent(getContext(), crime.getId());
        startActivity(intent);
        this.mLastClickedCrimePosition = crimeLab.getCrimes().size() - 1;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            this.mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;

        switch (requestCode) {
            case REQUEST_DELETE: {
                boolean crimeDeleted = data.getBooleanExtra(CrimeFragment.EXTRA_CRIME_DELETED, false);
                if (crimeDeleted) {
                    CrimeLab crimeLab = CrimeLab.get(getActivity());
                    Crime removedCrime = crimeLab.getCrimes()
                            .remove(this.mLastClickedCrimePosition.intValue());
                    Toast.makeText(getActivity(), "Crime deleted: " + removedCrime.getTitle(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Crime mCrime;

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;

        private CrimeHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.mDateTextView = itemView.findViewById(R.id.list_item_crime_date_text_view);
            this.mTitleTextView = itemView.findViewById(R.id.list_item_crime_title_text_view);
            this.mSolvedCheckBox = itemView.findViewById(R.id.list_item_crime_solved_check_box);
            this.mSolvedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                this.mCrime.setSolved(isChecked);
            });
        }

        public void bindCrime(Crime crime) {
            this.mCrime = crime;
            this.mTitleTextView.setText(mCrime.getTitle());
            this.mDateTextView.setText(mCrime.getDate().toString());
            this.mSolvedCheckBox.setChecked(mCrime.isSolved());
        }

        @Override
        public void onClick(View view) {
            mLastClickedCrimePosition = CrimeLab.get(getActivity()).getCrimes().indexOf(this.mCrime);
            Intent intent = CrimePagerActivity.newCrimeIdIntent(getActivity(), this.mCrime.getId());
            startActivityForResult(intent, REQUEST_DELETE);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        private CrimeAdapter(List<Crime> crimes) {
            this.mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_crime, viewGroup, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder crimeHolder, int i) {
            Crime crime = mCrimes.get(i);
            crimeHolder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}
