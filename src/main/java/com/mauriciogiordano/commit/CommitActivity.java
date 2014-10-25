package com.mauriciogiordano.commit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.j256.ormlite.dao.Dao;
import com.mauriciogiordano.commit.adapter.CommitAdapter;
import com.mauriciogiordano.commit.database.Commitment;
import com.mauriciogiordano.commit.database.DatabaseHelper;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import java.sql.SQLException;

public class CommitActivity extends ActionBarActivity
{
	public static final String TAG = "CommitActivity";

	public Fragment currentFragment = null;
	
	private CommitAdapter mAdapter;
	private ViewPager mPager;
    private PageIndicator mIndicator;

    public Dao<Commitment, Integer> dao;
    private DatabaseHelper dh;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit);
        
        // Hide ActionBar (we are not using it).
        getSupportActionBar().hide();

        mAdapter = new CommitAdapter(getSupportFragmentManager(), getApplicationContext());

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);

        dh = new DatabaseHelper(getApplicationContext());

        try {
			dao = dh.getCommitmentDao();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updatePages()
	{
		mAdapter.refresh();
		int newItemPos = mAdapter.getCount() - 2;
				
		mIndicator.setCurrentItem(newItemPos);
	}
}
