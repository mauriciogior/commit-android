package com.mauriciogiordano.commit.database;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.mauriciogiordano.commit.CommitHelper;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class Commitment extends BaseModel
{
	@DatabaseField(generatedId = true)
	private int commitmentID;

	@DatabaseField
	private String description;

	@DatabaseField
	private long reminder;
	
	@DatabaseField
	private int consecutiveDays;
	
	private List<Commit> commits = null;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getConsecutiveDays() {
		return consecutiveDays;
	}

	public void setConsecutiveDays(int consecutiveDays) {
		this.consecutiveDays = consecutiveDays;
	}

	public List<Commit> getCommits() {
		return commits;
	}

	public void setCommits(List<Commit> commits) {
		this.commits = commits;
	}
	
	private void loadCommits(Context context) {

		DatabaseHelper dh = new DatabaseHelper(context);
        
        try {
        	Dao<Commit, Integer> dao = dh.getCommitDao();
        	
        	commits = dao.queryForEq("commitmentID", commitmentID);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void newCommit(Context context) {
        
        consecutiveDays++;
        
		Commit commit = new Commit();
		
		commit.setWhen(CommitHelper.getToday());
		commit.setCommitmentID(commitmentID);

        if(commits == null) loadCommits(context);

		commits.add(commit);
		
		DatabaseHelper dh = new DatabaseHelper(context);
        
        try {
        	Dao<Commit, Integer> dao = dh.getCommitDao();
        	
        	dao.create(commit);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		loadCommits(context);

        baseModelListenerHandler.execOnUpdateListeners(this);
	}
	
	public boolean hasCommitForToday(Context context) {
		if(commits == null) loadCommits(context);

		DatabaseHelper dh = new DatabaseHelper(context);
        
        try {
        	Dao<Commit, Integer> commitDao = dh.getCommitDao();
        	
        	List<Commit> list = commitDao.queryBuilder()
        		.where().eq("when", CommitHelper.getToday())
        		.and().eq("commitmentID", commitmentID)
        		.query();
        	        	
        	if(list.size() > 0)
        	{
        		return true;
        	}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
                
        try {
        	Dao<Commit, Integer> commitDao = dh.getCommitDao();
        	
        	List<Commit> list = commitDao.queryBuilder()
        		.where().eq("when", CommitHelper.getYesterday())
        		.and().eq("commitmentID", commitmentID)
        		.query();

        	if(list.size() == 0)
        	{
        		consecutiveDays = 0;
        	}

        	Dao<Commitment, Integer> commitmentDao = dh.getCommitmentDao();
        	commitmentDao.update(this);
        	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

    private void calculateConsecutiveDays()
    {
        int cd = 1, diff;

        Date from = new Date();
        Date to = new Date();

        int size = commits.size();

        if(commits.size() > 0)
        {
            to.setTime(commits.get(size - 1).getWhen());

            for (int i = size - 2; i >= 0; i--)
            {
                from.setTime(commits.get(i).getWhen());

                diff = Days.daysBetween(new DateTime(from), new DateTime(to)).getDays();

                if(diff == 1)
                {
                    cd++;
                }
                else
                {
                    break;
                }

                from.setTime(commits.get(i).getWhen());
            }

            setConsecutiveDays(cd);
        }
    }

    public boolean setCheckForYesterday(Context context)
    {
        if(commits == null) loadCommits(context);

        DatabaseHelper dh = new DatabaseHelper(context);

        try {
            Dao<Commit, Integer> commitDao = dh.getCommitDao();
            Dao<Commitment, Integer> commitmentDao = dh.getCommitmentDao();

            List<Commit> list = commitDao.queryBuilder()
                    .where().eq("when", CommitHelper.getYesterday())
                    .and().eq("commitmentID", commitmentID)
                    .query();

            if(list.size() == 0)
            {
                Commit commit = new Commit();

                commit.setWhen(CommitHelper.getYesterday());
                commit.setCommitmentID(commitmentID);

                commits.add(commit);

                commitDao.create(commit);

                loadCommits(context);

                calculateConsecutiveDays();

                commitmentDao.update(this);

                baseModelListenerHandler.execOnUpdateListeners(this);

                return true;
            }
            else
            {
                return false;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

	public long getReminder() {
		return reminder;
	}

	public void setReminder(long reminder) {
		this.reminder = reminder;
	}

	public int getCommitmentID() {
		return commitmentID;
	}

	public void setCommitmentID(int commitmentID) {
		this.commitmentID = commitmentID;
	}
	
}
