package com.mauriciogiordano.commit;

import android.app.Activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.mauriciogiordano.commit.database.Commitment;
import com.mauriciogiordano.commit.database.DatabaseHelper;

import java.sql.SQLException;

public class CommitAlarmIntent extends IntentService
{
	public CommitAlarmIntent() {
        super("CommitAlarmIntent");
	}
    public CommitAlarmIntent(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        int requestCode = extras.getInt("requestCode");

        switch(requestCode)
        {
            case Constants.INTENT_COMMIT_ACTION:
                executeAction(intent);
                break;

            case Constants.INTENT_COMMIT_NOTIFICATION:
                sendNotification(intent);
                break;
        }
    }

    private void executeAction(Intent intent) {
        Bundle extras = intent.getExtras();

        int commitmentID = extras.getInt("commitmentID");

        NotificationManager notificationManager =
                (NotificationManager) CommitAlarmIntent.this.getSystemService(Activity.NOTIFICATION_SERVICE);

        notificationManager.cancel(commitmentID);

        DatabaseHelper dh = new DatabaseHelper(getApplicationContext());

        try {
            Dao<Commitment, Integer> dao = dh.getCommitmentDao();

            Commitment commitment = dao.queryForId(commitmentID);

            commitment.newCommit(getApplicationContext());

            dao.update(commitment);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Handler h = new Handler(getApplicationContext().getMainLooper());
        // Although you need to pass an appropriate context
        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), R.string.Toast_great_job, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendNotification(Intent intent) {
        Bundle extras = intent.getExtras();

		int commitmentID = extras.getInt("commitmentID");

        DatabaseHelper dh = new DatabaseHelper(getApplicationContext());

        try {
        	Dao<Commitment, Integer> dao = dh.getCommitmentDao();
	        
        	Commitment c = dao.queryForId(commitmentID);
        	
        	if(!c.hasCommitForToday(getApplicationContext()))
        	{
                Intent intentCommit = new Intent(getApplicationContext(), this.getClass());
                intentCommit.putExtra("requestCode", Constants.INTENT_COMMIT_ACTION);
                intentCommit.putExtra("commitmentID", commitmentID);

                PendingIntent pIntentCommit = PendingIntent.getService(getApplicationContext(), 0, intentCommit,
                                            PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_UPDATE_CURRENT);

                String description = c.getDescription();
                char[] stringArray = description.trim().toCharArray();
                stringArray[0] = Character.toUpperCase(stringArray[0]);

                description = new String(stringArray);

				Notification n = new NotificationCompat.Builder(CommitAlarmIntent.this)
					.setContentTitle(description
						  + " " + getString(R.string.TextView_every_day))
				    .setContentText(getString(R.string.TextView_just_reminding))
				    .setSmallIcon(R.drawable.ic_stat_toggle_check_box)
				    .setAutoCancel(true)
				    .addAction(R.drawable.icon_yes_small, getString(R.string.Button_yes_i_did), pIntentCommit)
				    .build();
                
				NotificationManager notificationManager = 
						(NotificationManager) CommitAlarmIntent.this.getSystemService(Activity.NOTIFICATION_SERVICE);

				notificationManager.notify(commitmentID, n);
        	}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
