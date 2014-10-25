package com.mauriciogiordano.commit.database;

import com.j256.ormlite.field.DatabaseField;

public class Commit
{
	@DatabaseField
	private Long when;

	@DatabaseField
	private int commitmentID;
	
	public Long getWhen() {
		return when;
	}

	public void setWhen(Long when) {
		this.when = when;
	}

	public int getCommitmentID() {
		return commitmentID;
	}

	public void setCommitmentID(int commitmentID) {
		this.commitmentID = commitmentID;
	}
	
}
