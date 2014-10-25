package com.mauriciogiordano.commit.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseConfigUtil extends OrmLiteConfigUtil
{
	private static final Class<?>[] classes = new Class[] {
		Commitment.class,
	};
	
	public static void main(String[] args) throws SQLException, IOException
	{
		writeConfigFile("orm_config.txt", classes);
	}

}
