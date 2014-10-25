package com.mauriciogiordano.commit.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.mauriciogiordano.commit.CommitActivity;
import com.mauriciogiordano.commit.CommitAlarmReceiver;
import com.mauriciogiordano.commit.R;
import com.mauriciogiordano.commit.database.BaseModel;
import com.mauriciogiordano.commit.database.Commitment;
import com.mauriciogiordano.commit.database.DatabaseHelper;

import java.sql.SQLException;

public class CommitmentsFragment extends Fragment
{
    private static final String KEY_CONTENT = "CommitsFragment:Content";

    private String mContent = "???";
    private Commitment mCommitment;
    
    private CommitActivity mainActivity;
    
    private View rootView;
    private View vControls;
    
    private TextView tDaysInARow;
    private TextView tCommitment;
    
    private ProgressBar pCommit;

    private Button bCommit;
    private Button bConfig;
    private Button bRemove;
    private Button bYesterday;
    
    private boolean controlsExpanded;
    
    public static CommitmentsFragment newInstance(Commitment mCommitment)
    {
    	CommitmentsFragment fragment = new CommitmentsFragment(mCommitment);
    	
        return fragment;
    }

    private CommitmentsFragment(Commitment mCommitment)
    {
    	super();
    	this.mCommitment = mCommitment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mainActivity = (CommitActivity) getActivity();
        
        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT))
        {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
    	/* Inflate and set as View */
		rootView = inflater.inflate(R.layout.fragment_commitment,
							container, false);

		/* Access to View elements */
		vControls = rootView.findViewById(R.id.Layout_controls);
		
		tCommitment = (TextView) rootView.findViewById(R.id.TextView_commitment);
		tDaysInARow = (TextView) rootView.findViewById(R.id.TextView_days);
		
		pCommit = (ProgressBar) rootView.findViewById(R.id.progress_commit);

		bCommit = (Button) rootView.findViewById(R.id.Button_commit);
		bConfig = (Button) rootView.findViewById(R.id.Button_config);
		bRemove = (Button) rootView.findViewById(R.id.Button_remove);
		bYesterday = (Button) rootView.findViewById(R.id.Button_check_yesterday);
		
		tCommitment.setText(mCommitment.getDescription());
		
		if(mCommitment.getDescription().length() > 12)
		{
			tCommitment.setTextSize(getResources().getDimension(R.dimen.textview_h1));
		}
		else
		{
			tCommitment.setTextSize(getResources().getDimension(R.dimen.textview_hero));
		}
		
		if(mCommitment.hasCommitForToday(mainActivity.getApplicationContext()))
		{
			bCommit.setVisibility(View.INVISIBLE);
			rootView.findViewById(R.id.Button_commit_ok).setVisibility(View.VISIBLE);
		}
		else
		{
			bCommit.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					mCommitment.newCommit(mainActivity.getApplicationContext());
					
			        try {
			        	mainActivity.dao.update(mCommitment);

			    		tDaysInARow.setText(mCommitment.getConsecutiveDays() + " ");
						pCommit.setProgress(mCommitment.getConsecutiveDays());
			    		
			    		Toast.makeText(getActivity(),
			    				getResources().getString(R.string.Toast_great_job), Toast.LENGTH_SHORT).show();
			    		
			        	bCommit.setOnClickListener(null);

			        	bCommit.setVisibility(View.INVISIBLE);
						rootView.findViewById(R.id.Button_commit_ok).setVisibility(View.VISIBLE);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}

		pCommit.setProgress(mCommitment.getConsecutiveDays());
		tDaysInARow.setText(mCommitment.getConsecutiveDays() + " ");
		
		collapse(vControls);
		controlsExpanded = false;
		
		bConfig.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(controlsExpanded) collapse(vControls);
				else expand(vControls);
				
				controlsExpanded = !controlsExpanded;
			}
		});
		
		bRemove.setOnClickListener(new OnClickListener()
		{	
			@Override
			public void onClick(View v)
			{
		        DatabaseHelper dh = new DatabaseHelper(getActivity().getApplicationContext());

		        try {
		        	Dao<Commitment, Integer> dao = dh.getCommitmentDao();
					
				    Intent alarmIntent = new Intent(mainActivity, CommitAlarmReceiver.class);
				    alarmIntent.putExtra("commitmentID", mCommitment.getCommitmentID());
				    
				    AlarmManager manager = (AlarmManager) mainActivity.getSystemService(Context.ALARM_SERVICE);
				    PendingIntent pIntentToCancel = PendingIntent.getBroadcast(mainActivity, mCommitment.getCommitmentID(), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

				    manager.cancel(pIntentToCancel);

		        	dao.delete(mCommitment);
				    
		        	Toast.makeText(mainActivity, getString(R.string.Toast_commitment_deleted), Toast.LENGTH_LONG).show();
		        	
		        	mainActivity.updatePages();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

        mCommitment.addOnUpdateListener(new BaseModel.OnUpdateListener() {

            private BaseModel target;

            @Override
            public void onUpdate(BaseModel object) {

                target = object;

                Handler h = new Handler(mainActivity.getMainLooper());
                // Although you need to pass an appropriate context
                h.post(new Runnable() {

                    @Override
                    public void run() {
                        mCommitment = (Commitment) target;

                        if(mCommitment.hasCommitForToday(mainActivity.getApplicationContext()))
                        {
                            bCommit.setVisibility(View.INVISIBLE);
                            bCommit.setOnClickListener(null);
                            rootView.findViewById(R.id.Button_commit_ok).setVisibility(View.VISIBLE);

                            pCommit.setProgress(mCommitment.getConsecutiveDays());
                            tDaysInARow.setText(mCommitment.getConsecutiveDays() + " ");

                            rootView.invalidate();
                        }
                    }
                });
            }

        });
		
        return rootView;
    }
    
    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density))*2);
        v.startAnimation(a);
    }
    
    public static void expand(final View v) {
        v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        final int targtetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? LayoutParams.WRAP_CONTENT
                        : (int)(targtetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration(((int) (targtetHeight / v.getContext().getResources().getDisplayMetrics().density))*2);
        v.startAnimation(a);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }
}
