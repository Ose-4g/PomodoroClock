package com.ose4g.pomodoroclock;

public class Sessions
{
    private String Description;
    private int session_length,break_length;

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getSession_length() {
        return session_length;
    }

    public void setSession_length(int session_length) {
        this.session_length = session_length;
    }

    public int getBreak_length() {
        return break_length;
    }

    public void setBreak_length(int break_length) {
        this.break_length = break_length;
    }

    Sessions(int work, int rest, String description)
    {
        session_length = work;
        break_length = rest;
        Description = description;
    }
}
