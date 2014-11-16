package com.mauriciogiordano.commit.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.j256.ormlite.dao.Dao;
import com.mauriciogiordano.commit.database.Commitment;
import com.mauriciogiordano.commit.database.DatabaseHelper;
import com.mauriciogiordano.commit.fragment.CommitFragment;
import com.mauriciogiordano.commit.fragment.CommitmentsFragment;

import java.sql.SQLException;
import java.util.List;

public class CommitAdapter extends FragmentStatePagerAdapter
{
	private List<Commitment> commitments;
    private int mCount;
    
    private Dao<Commitment, Integer> dao;
    private DatabaseHelper dh;
        
    private Fragment commitFragment;

    public CommitAdapter(FragmentManager fm, Context context)
    {    	
        super(fm);
    	
        dh = new DatabaseHelper(context);
        
        mCount = 1;
        
        commitments = null;
        
        try {
			dao = dh.getCommitmentDao();
			
			commitments = dao.queryForAll();

            for(int i=0; i<commitments.size(); i++)
            {
                commitments.get(i).calculateConsecutiveDays(context);
            }

			mCount = commitments.size() + 1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public Fragment getItem(int position)
    {
    	if(position == mCount - 1)
    	{
    		commitFragment = new CommitFragment();
    		
        	return commitFragment;
    	}
    	
    	return CommitmentsFragment.newInstance(commitments.get(position));
    }
    
    public Commitment getCommitment(int position)
    {
    	return commitments.get(position);
    }

    @Override
    public int getCount()
    {
        return mCount;
    }
    
    @Override
    public Object instantiateItem(android.view.ViewGroup container, int position)
    {
    	container.setTag(String.valueOf(position));
    	return super.instantiateItem(container, position);
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void refresh()
    {
        try {
			dao = dh.getCommitmentDao();
			
			commitments = dao.queryForAll();
			
			mCount = commitments.size() + 1;
			
            notifyDataSetChanged();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}