package com.ose4g.pomodoroclock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Pomodoro extends AppCompatActivity
{
    private ArrayList<Sessions> mSessionsList;
    private static RecyclerView mRecyclerView;
    private SessionRecyclerAdapter mSessionRecyclerAdaptor;
    private LinearLayoutManager mLayoutManager;
    private static int mIsCurrentlySelected;
    public static int mCurrentlyPlaying;
    private CountDownTimer mTimer;
    private long mSessionTimeRemaining;
    private long mBreakTimeRemaining;
    public static boolean mSessionIsCounting=false;
    public static boolean mBreakIsCounting=false;
    public static boolean mIsPaused=false;
    public static TextView mShow_time;
    private ProgressBar mProgressBar;
    private ImageView mStartCountdown;
    public static TextView mSession_length;
    public static TextView mBreak_length;
    private TextView mSession_indicator;
    private TextView mBreak_indicator;
    private ImageView mIncrease_session;
    private ImageView mReduce_session;
    private ImageView mIncrease_break;
    private ImageView mReduce_break;
    public static TextView mCurrentActivity;

    public static int getmIsCurrentlySelected() {
        return mIsCurrentlySelected;
    }

    public static RecyclerView getmRecyclerView() {
        return mRecyclerView;
    }

    public static void setPosition(int position) {
        mIsCurrentlySelected = position;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        mShow_time = (TextView) findViewById(R.id.time_elasped);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mSession_length = (TextView) findViewById(R.id.session_length);
        mBreak_length = (TextView) findViewById(R.id.break_length);
        mSession_indicator = (TextView) findViewById(R.id.session_indicator);
        mBreak_indicator = (TextView) findViewById(R.id.break_indicator);

        mSessionsList = DataManager.getSessions();
        mRecyclerView = (RecyclerView) findViewById(R.id.list_of_sessions);
        mSessionRecyclerAdaptor = new SessionRecyclerAdapter(this,mSessionsList);
        mLayoutManager = new LinearLayoutManager(this);

        mIsCurrentlySelected = 0;
        mCurrentlyPlaying=0;
        mSessionTimeRemaining = new Long(mSessionsList.get(mCurrentlyPlaying).getSession_length()*60*1000);
        mBreakTimeRemaining = new Long(mSessionsList.get(mCurrentlyPlaying).getBreak_length()*60*1000);

        mIncrease_session = (ImageView) findViewById(R.id.session_length_up);
        mReduce_session = (ImageView) findViewById(R.id.session_length_down);
        mIncrease_break = (ImageView) findViewById(R.id.break_length_up);
        mReduce_break = (ImageView) findViewById(R.id.break_length_down);

        mIncrease_session.setVisibility(View.INVISIBLE);
        mIncrease_session.setClickable(false);

        mReduce_session.setVisibility(View.INVISIBLE);
        mReduce_session.setClickable(false);

        mIncrease_break.setVisibility(View.INVISIBLE);
        mIncrease_break.setClickable(false);

        mReduce_break.setVisibility(View.INVISIBLE);
        mReduce_break.setClickable(false);

        mCurrentActivity = (TextView) findViewById(R.id.currentActivity);


        ImageView new_item = (ImageView) findViewById(R.id.add_new_item);
        new_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewSession();
            }
        });

        ImageView remove_item = (ImageView) findViewById(R.id.remove_item);
        remove_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeSession();
            }
        });


        mShow_time.setText(toTime(mSessionTimeRemaining));
        mCurrentActivity.setText(mSessionsList.get(mCurrentlyPlaying).getDescription());


        //click listener for pause play button
        mStartCountdown = (ImageView) findViewById(R.id.play_pause_1);
        mStartCountdown.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View view) {
            if (mSessionsList.size()>=1)//if there is at least one session in the list
            {


                if(!(mSessionIsCounting || mBreakIsCounting))//if the countdown is just starting or has been paused
                {
                    mShow_time.setTextColor(Color.BLACK);
                    mStartCountdown.setImageResource(android.R.drawable.ic_media_pause);
                    if(!mIsPaused)//if it is just starting
                    {
                        mSessionTimeRemaining = new Long(mSessionsList.get(mCurrentlyPlaying).getSession_length()*60*1000);
                        mBreakTimeRemaining = new Long(mSessionsList.get(mCurrentlyPlaying).getBreak_length()*60*1000);
                        startSessionCountdown();
                        mSessionIsCounting = true;
                    }
                    else//if it was paused
                    {

                        if(mSessionTimeRemaining>0)
                        {
                            startSessionCountdown();
                            mSessionIsCounting = true;
                            mIsPaused = false;
                        }
                        else
                        {
                            startBreakCountDown();
                            mBreakIsCounting = true;
                            mIsPaused=false;
                        }
                    }
                }
                else//it is playing and we ant to pause
                {
                    mStartCountdown.setImageResource(android.R.drawable.ic_media_play);
                    pauseTimer();
                }
            }
            else //no sessions in the list
            {
                Toast.makeText(Pomodoro.this, "The list is empty, Please add a new session",Toast.LENGTH_SHORT).show();
            }

        }
    });


    ImageView stop = (ImageView) findViewById(R.id.stop_reset);
    stop.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mStartCountdown.setImageResource(android.R.drawable.ic_media_play);
            stopTimer();
        }
    });




        mIncrease_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimer!=null) {mTimer.cancel();}
                mSessionTimeRemaining += 1*60*1000;
                mShow_time.setText(toTime(mSessionTimeRemaining));
                startSessionCountdown();
                if(mIsPaused || (!mSessionIsCounting && !mBreakIsCounting))
                    pauseTimer();
            }
        });

        mReduce_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimer!=null) {mTimer.cancel();}
                mSessionTimeRemaining =Math.max(0,mSessionTimeRemaining-1*60*1000);
                mShow_time.setText(toTime(mSessionTimeRemaining));
                startSessionCountdown();
                if(mIsPaused || (!mSessionIsCounting && !mBreakIsCounting))
                    pauseTimer();
            }
        });

        mIncrease_break.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimer!=null) {mTimer.cancel();}
                mBreakTimeRemaining += 1*60*1000;
                mShow_time.setText(toTime(mBreakTimeRemaining));
                startBreakCountDown();
                if(mIsPaused || (!mSessionIsCounting && !mBreakIsCounting))
                    pauseTimer();
            }
        });

        mReduce_break.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTimer!=null) {mTimer.cancel();}
                mBreakTimeRemaining = Math.max(mBreakTimeRemaining-1*60*1000,0);
                mShow_time.setText(toTime(mBreakTimeRemaining));
                startBreakCountDown();
                if(mIsPaused || (!mSessionIsCounting && !mBreakIsCounting))
                    pauseTimer();
            }
        });

        initialize_sessions();
    }

    private void stopTimer()
    {
        pauseTimer();
        mIsPaused=false;
        mSessionRecyclerAdaptor.notifyItemChanged(mCurrentlyPlaying);
        mCurrentlyPlaying = 0;
        mSessionRecyclerAdaptor.notifyItemChanged(mCurrentlyPlaying);

        String start_time = Integer.toString(mSessionsList.get(mCurrentlyPlaying).getSession_length())+":00" ;
        if (start_time.length()<5){start_time="0"+start_time;}
        mShow_time.setText(start_time);
        mShow_time.setTextColor(Color.RED);

        mProgressBar.setProgress(0);
        mSessionTimeRemaining = new Long(mSessionsList.get(mCurrentlyPlaying).getSession_length()*60*1000);
        mBreakTimeRemaining = new Long(mSessionsList.get(mCurrentlyPlaying).getBreak_length()*60*1000);
        mShow_time.setText(toTime(mSessionTimeRemaining));

        mSession_indicator.setTextColor(getResources().getColor(R.color.black));
        mBreak_indicator.setTextColor(getResources().getColor(R.color.black));
        mBreak_length.setTextColor(Color.BLACK);
        mSession_length.setTextColor(Color.BLACK);

    }

    private void pauseTimer()
    {
        mTimer.cancel();
        mIsPaused = true;
        mSessionIsCounting = false;
        mBreakIsCounting = false;
        mShow_time.setTextColor(Color.RED);

        mIncrease_session.setVisibility(View.INVISIBLE);
        mIncrease_session.setClickable(false);

        mReduce_session.setVisibility(View.INVISIBLE);
        mReduce_session.setClickable(false);

        mIncrease_break.setVisibility(View.INVISIBLE);
        mIncrease_break.setClickable(false);

        mReduce_break.setVisibility(View.INVISIBLE);
        mReduce_break.setClickable(false);
    }

    private void removeSession()
    {
        if (mIsPaused || mSessionIsCounting || mBreakIsCounting)//if the time is counting or is paused
        {
            Toast.makeText(this,"Session is currently counting\n STOP countdown to delete session",Toast.LENGTH_LONG).show();
        }
        else//it is not counting or paused
        {
            if(mSessionsList.size()> mIsCurrentlySelected+1)
            {
                DataManager.removeSession(mIsCurrentlySelected);
                mIsCurrentlySelected = Math.max(0,mIsCurrentlySelected-1);
                mSessionRecyclerAdaptor.notifyDataSetChanged();
                mCurrentlyPlaying=0;
                mSessionTimeRemaining = new Long(mSessionsList.get(mCurrentlyPlaying).getSession_length()*60*1000);
                mBreakTimeRemaining = new Long(mSessionsList.get(mCurrentlyPlaying).getBreak_length()*60*1000);
                mShow_time.setText(toTime(mSessionTimeRemaining));
            }
            else
            {
                Toast.makeText(this, "The list can't be empty", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void initialize_sessions()
    {

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mSessionRecyclerAdaptor);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSessionRecyclerAdaptor.notifyDataSetChanged();
    }

    private void addNewSession()
    {
        if(Pomodoro.mBreakIsCounting || Pomodoro.mSessionIsCounting || Pomodoro.mIsPaused)
            Toast.makeText(this, "Can't add new session since countdown has started",Toast.LENGTH_LONG).show();
        else
        {
            DataManager.createNewSession();
            mSessionRecyclerAdaptor.notifyDataSetChanged();
        }


    }
    private void startSessionCountdown()
    {

        mSession_length.setText(Integer.toString(mSessionsList.get(mCurrentlyPlaying).getSession_length())+" minutes");

        mCurrentActivity.setText(mSessionsList.get(mCurrentlyPlaying).getDescription());

        mIncrease_session.setVisibility(View.VISIBLE);
        mIncrease_session.setClickable(true);

        mReduce_session.setVisibility(View.VISIBLE);
        mReduce_session.setClickable(true);

        mBreak_length.setText(Integer.toString(mSessionsList.get(mCurrentlyPlaying).getBreak_length())+" minutes");
        mSession_length.setTextColor(getResources().getColor(R.color.colorPrimary));
        mSession_indicator.setTextColor(getResources().getColor(R.color.colorPrimary));
        mProgressBar.setMax(DataManager.getSessions().get(mCurrentlyPlaying).getSession_length()*60*1000);
        mTimer = new CountDownTimer(mSessionTimeRemaining,1000)
        {

            @Override
            public void onTick(long milliSecondsLeft)
            {
                mSessionTimeRemaining = milliSecondsLeft  ;
//                long min = (milliSecondsLeft/1000)/60;
//                long seconds = (milliSecondsLeft/1000)-min*60;
//                String min_s = Long.toString(min);
//                String sec_s = Long.toString(seconds);
//                if(min<10){min_s="0"+min_s;}
//                if(seconds<10){sec_s="0"+sec_s;}
                //mShow_time = (TextView) findViewById(R.id.time_elasped);
                //mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

                mShow_time.setText(toTime(milliSecondsLeft));
                mProgressBar.setProgress((int) (mProgressBar.getMax()-milliSecondsLeft));
            }

            @Override
            public void onFinish() {
                notifyFinished();
                mProgressBar.setProgress(mProgressBar.getMax());
                mSession_length.setTextColor(getResources().getColor(R.color.black));
                mSession_indicator.setTextColor(getResources().getColor(R.color.black));

                mBreak_length.setTextColor(getResources().getColor(R.color.colorPrimary));
                mBreak_indicator.setTextColor(getResources().getColor(R.color.colorPrimary));
                mSessionIsCounting = false;
                mBreakIsCounting = true;
                mProgressBar.setProgress(0);

                mIncrease_session.setVisibility(View.INVISIBLE);
                mIncrease_session.setClickable(false);

                mReduce_session.setVisibility(View.INVISIBLE);
                mReduce_session.setClickable(false);

                startBreakCountDown();
            }
        };
        mTimer.start();

    }

    private void startBreakCountDown()
    {
        mProgressBar.setMax(DataManager.getSessions().get(mCurrentlyPlaying).getBreak_length()*60*1000);

        mIncrease_break.setVisibility(View.VISIBLE);
        mIncrease_break.setClickable(true);

        mReduce_break.setVisibility(View.VISIBLE);
        mReduce_break.setClickable(true);

        mTimer = new CountDownTimer(mBreakTimeRemaining,1000) {
            @Override
            public void onTick(long milliSecondsLeft)
            {
                mBreakTimeRemaining = milliSecondsLeft;
//                long min = (milliSecondsLeft/1000)/60;
//                long seconds = (milliSecondsLeft/1000)-min*60;
//                String min_s = Long.toString(min);
//                String sec_s = Long.toString(seconds);
//                if(min<10){min_s="0"+min_s;}
//                if(seconds<10){sec_s="0"+sec_s;}
                //mShow_time = (TextView) findViewById(R.id.time_elasped);
                //mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

                mShow_time.setText(toTime(milliSecondsLeft));
                mProgressBar.setProgress((int) (mProgressBar.getMax()-milliSecondsLeft));
            }

            @Override
            public void onFinish() {
                notifyFinished();
                mProgressBar.setProgress(mProgressBar.getMax());
                mBreakIsCounting = false;

                mIncrease_break.setVisibility(View.INVISIBLE);
                mIncrease_break.setClickable(false);

                mReduce_break.setVisibility(View.INVISIBLE);
                mReduce_break.setClickable(false);

                if (mCurrentlyPlaying !=mSessionsList.size()-1)
                {
                    mBreak_length.setTextColor(getResources().getColor(R.color.black));
                    mBreak_indicator.setTextColor(getResources().getColor(R.color.black));

                    mSessionRecyclerAdaptor.notifyItemChanged(mCurrentlyPlaying);
                    mCurrentlyPlaying++;
                    mSessionRecyclerAdaptor.notifyItemChanged(mCurrentlyPlaying);
                    mSessionIsCounting=true;
                    mSessionTimeRemaining = new Long(mSessionsList.get(mCurrentlyPlaying).getSession_length()*60*1000);
                    mBreakTimeRemaining = new Long(mSessionsList.get(mCurrentlyPlaying).getBreak_length()*60*1000);
                    startSessionCountdown();
                }
                else
                    mShow_time.setTextColor(Color.GREEN);
            }
        };
        mTimer.start();
    }

    public static String  toTime(long milliSecondsLeft)
    {
        long min = (milliSecondsLeft/1000)/60;
        long seconds = (milliSecondsLeft/1000)-min*60;
        String min_s = Long.toString(min);
        String sec_s = Long.toString(seconds);
        if(min<10){min_s="0"+min_s;}
        if(seconds<10){sec_s="0"+sec_s;}

        return min_s+":"+sec_s;
    }

    private void notifyFinished()
    {
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION,100);
        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP,1000);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)//for API HIGHER THAN 26
        {
            vibrator.vibrate(VibrationEffect.createOneShot(1000,VibrationEffect.DEFAULT_AMPLITUDE));
        }
        else
        {
            vibrator.vibrate(1000);
        }

    }

}