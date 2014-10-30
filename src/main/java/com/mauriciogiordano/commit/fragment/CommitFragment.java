package com.mauriciogiordano.commit.fragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.mauriciogiordano.commit.CommitActivity;
import com.mauriciogiordano.commit.CommitAlarmReceiver;
import com.mauriciogiordano.commit.R;
import com.mauriciogiordano.commit.database.Commitment;
import com.mauriciogiordano.commit.database.DatabaseHelper;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class CommitFragment extends Fragment
{
	private EditText description;
	private EditText reminder;
	
	private CommitActivity mainActivity;
	
	private LayoutInflater inflater;
	private ViewGroup container;
	
	private AlertDialog dialog = null;
	
	private long reminderTime = 0;
	
	private View rootView;
		
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	this.inflater = inflater;
    	this.container = container;
    	
		rootView = inflater.inflate(R.layout.fragment_commit,
							container, false);
		
		mainActivity = ((CommitActivity) getActivity());
		
		description = (EditText) rootView.findViewById(R.id.EditText_commitment);
		reminder = (EditText) rootView.findViewById(R.id.EditText_reminder);
		Button commit = (Button) rootView.findViewById(R.id.Button_commit);

		description.addTextChangedListener(new TextWatcher()
		{
		
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

				if(description.getText().length() > 12)
				{
					description.setTextSize(getResources().getDimension(R.dimen.textview_h2));
				}
				else
				{
					description.setTextSize(getResources().getDimension(R.dimen.textview_hero));
				}
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		commit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(description.getText().toString().equals(""))
				{
					Toast.makeText(mainActivity, getResources().getString(R.string.Toast_description), Toast.LENGTH_SHORT).show();
					
					return;
				}
				else if(reminder.getText().toString().equals(""))
				{
					Toast.makeText(mainActivity, getResources().getString(R.string.Toast_fill_reminder), Toast.LENGTH_SHORT).show();
					
					return;
				}
				
				Commitment commitment = new Commitment();
				
				commitment.setDescription(description.getText().toString());
				commitment.setReminder(reminderTime);
				commitment.setConsecutiveDays(0);
				
		        DatabaseHelper dh = new DatabaseHelper(getActivity().getApplicationContext());

		        try {
		        	Dao<Commitment, Integer> dao = dh.getCommitmentDao();
					
		        	dao.create(commitment);

				    Intent alarmIntent = new Intent(mainActivity, CommitAlarmReceiver.class);
				    alarmIntent.putExtra("commitmentID", commitment.getCommitmentID());
				    
				    PendingIntent pendingIntent = PendingIntent.getBroadcast(mainActivity, commitment.getCommitmentID(), alarmIntent, 0);
		        	
				    AlarmManager manager = (AlarmManager) mainActivity.getSystemService(Context.ALARM_SERVICE);
				    manager.setRepeating(AlarmManager.RTC_WAKEUP, commitment.getReminder(), AlarmManager.INTERVAL_DAY, pendingIntent);
				    
				    Toast.makeText(mainActivity,
				    		getString(R.string.Toast_you_committed_to)
				    		+ " " + commitment.getDescription()
				    		+ " " + getString(R.string.TextView_every_day), Toast.LENGTH_LONG).show();
				    
		        	mainActivity.updatePages();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		reminder.setOnFocusChangeListener(new OnFocusChangeListener()
    	{	
			private AlertDialog.Builder dialogBuilder = null;
			private TimePicker timePicker;
			private final DateFormat formater = DateFormat.getTimeInstance(DateFormat.SHORT);
			
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if(hasFocus)
				{
					reminder.clearFocus();
					
					dialogBuilder = new AlertDialog.Builder(mainActivity);
					
					View timeDialogView = CommitFragment.this.inflater.inflate(R.layout.dialog_time,
							CommitFragment.this.container, false);
					
					Button action = (Button) timeDialogView.findViewById(R.id.buttonTime);
					timePicker = (TimePicker) timeDialogView.findViewById(R.id.time);
					
					Calendar now = Calendar.getInstance();
					
					timePicker.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
					timePicker.setCurrentMinute(now.get(Calendar.MINUTE));
					
					dialogBuilder.setView(timeDialogView);

					dialog = dialogBuilder.create();
					
					dialog.show();
					
					action.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							dialog.dismiss();
							
							timePicker.clearFocus();
							
							Calendar selected = Calendar.getInstance();
							
							selected.setTimeZone(TimeZone.getDefault());
							selected.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
							selected.set(Calendar.MINUTE, timePicker.getCurrentMinute());
							
							//selected.set(Calendar.DAY_OF_YEAR, selected.get(Calendar.DAY_OF_YEAR) + 1);
							selected.set(Calendar.SECOND, 0);
							selected.set(Calendar.MILLISECOND, 0);
							
							formater.setCalendar(selected);
							
							reminder.setText(formater.format(selected.getTime()));
							reminderTime = selected.getTimeInMillis();
						}
					});
				}
			}
    	});
		
        return rootView;
    }
    
}
