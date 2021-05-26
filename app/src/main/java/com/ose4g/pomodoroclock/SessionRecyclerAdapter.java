package com.ose4g.pomodoroclock;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SessionRecyclerAdapter extends RecyclerView.Adapter<SessionRecyclerAdapter.ViewHolder>
{
    private final Context mContext;
    private final ArrayList<Sessions> mListOfSessions;
    private final LayoutInflater mLayoutInflater;

    public SessionRecyclerAdapter(Context context, ArrayList<Sessions> list)
    {
        mListOfSessions = list;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.session_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position)
    {
        Sessions sessions = mListOfSessions.get(position);
        holder.mDescription.setText(sessions.getDescription());
        holder.mTime.setText(Integer.toString(sessions.getSession_length())+" minutes/"+Integer.toString(sessions.getBreak_length())+" minutes");
        holder.mCurrentPosition = position;
        holder.mDescription.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.PlayPause.setColorFilter(null);

        if(position == Pomodoro.getmIsCurrentlySelected())
        {
            holder.mDescription.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));

        }

        if(Pomodoro.mCurrentlyPlaying==position)
        {
            holder.PlayPause.setColorFilter(ContextCompat.getColor(mContext,R.color.colorPrimary));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pomodoro.setPosition(position);
                notifyDataSetChanged();
            }
        });

        holder.mDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Pomodoro.setPosition(position);
                if (Pomodoro.mBreakIsCounting || Pomodoro.mSessionIsCounting || Pomodoro.mIsPaused)
                {
                    Toast.makeText(mContext,"Can't edit description values while countdown is active\nSTOP countdown to edit time values",
                            Toast.LENGTH_LONG).show();
                }
                else
                {
                    final View alert = mLayoutInflater.inflate(R.layout.get_session_description,null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    alertDialogBuilder.setView(alert);
                    final EditText description = (EditText) alert.findViewById(R.id.get_session_description);

                    alertDialogBuilder.setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DataManager.getSessions().get(position).setDescription(description.getText().toString());
                                    notifyItemChanged(position);

                                    if(position ==0)
                                    {
                                        Pomodoro.mCurrentActivity.setText(DataManager.getSessions().get(position).getDescription());
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setTitle("Session Details");
                    alertDialog.setMessage("Enter Session Description");
                    alertDialog.show();
                    notifyDataSetChanged();
                }

            }
        });

        holder.mTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pomodoro.setPosition(position);
                if (Pomodoro.mBreakIsCounting || Pomodoro.mSessionIsCounting || Pomodoro.mIsPaused)
                {
                    Toast.makeText(mContext,"Can't edit time values while countdown is active\nSTOP countdown to edit time values",
                            Toast.LENGTH_LONG).show();
                }
                else
                {
                    final View alert = mLayoutInflater.inflate(R.layout.get_session_times,null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
                    alertDialogBuilder.setView(alert);
                    final EditText session_time = (EditText) alert.findViewById(R.id.session_length_edittext);
                    final EditText breakTime = (EditText) alert.findViewById(R.id.break_length_edittext);

                    alertDialogBuilder.setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Sessions session = DataManager.getSessions().get(position);
                                    session.setSession_length(Integer.parseInt(session_time.getText().toString()));
                                    session.setBreak_length(Integer.parseInt(breakTime.getText().toString()));
                                    notifyItemChanged(position);

                                    if(position ==0)
                                    {
                                        Pomodoro.mShow_time.setText(Pomodoro.toTime(session.getSession_length()*60*1000));
                                        Pomodoro.mSession_length.setText(Integer.toString(session.getSession_length())+" minutes");
                                        Pomodoro.mBreak_length.setText(Integer.toString(session.getBreak_length())+" minutes");
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setTitle("Session Details");
                    alertDialog.setMessage("Enter Session Length and Break Time Length");
                    alertDialog.show();

                    notifyDataSetChanged();
                }




            }
        });

    }

    @Override
    public int getItemCount() {
        return mListOfSessions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView mDescription;
        public TextView mTime;
        public ImageView PlayPause;
        public int mCurrentPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mDescription = (TextView) itemView.findViewById(R.id.session_description);
            mTime = (TextView) itemView.findViewById(R.id.time_description);
            PlayPause = (ImageView) itemView.findViewById(R.id.play_pause_2);

        }
    }
}
