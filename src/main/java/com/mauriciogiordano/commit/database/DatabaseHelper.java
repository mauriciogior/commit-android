package com.mauriciogiordano.commit.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mauriciogiordano.commit.R;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{
	public static final int DATABASE_VERSION = 7;
	
	private Dao<Commitment, Integer> commitmentDao = null;
	private Dao<Commit, Integer> commitDao = null;
	
	public static ConnectionSource cs;
		
	public DatabaseHelper(Context context)
	{
		super(context, "commit.db", null, DATABASE_VERSION, R.raw.orm_config);
	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource cs)
	{
		try
		{
			TableUtils.createTable(cs, Commitment.class);
			TableUtils.createTable(cs, Commit.class);
			DatabaseHelper.cs = cs;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource cs, int oldVersion,
			int newVersion)
	{
		try
		{
			TableUtils.dropTable(cs, Commitment.class, true);
			TableUtils.dropTable(cs, Commit.class, true);
			onCreate(database, cs);
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Dao<Commitment, Integer> getCommitmentDao() throws SQLException
	{
		if(commitmentDao == null)
		{
			commitmentDao = getDao(Commitment.class);
		}
		
		return commitmentDao;
	}
	
	public Dao<Commit, Integer> getCommitDao() throws SQLException
	{
		if(commitDao == null)
		{
			commitDao = getDao(Commit.class);
		}
		
		return commitDao;
	}
	
}
