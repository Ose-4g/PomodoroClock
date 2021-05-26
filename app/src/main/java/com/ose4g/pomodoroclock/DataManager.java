package com.ose4g.pomodoroclock;

import java.util.ArrayList;
import java.util.List;

public class DataManager
{
    private static ArrayList<Sessions> sessions = new ArrayList<Sessions>();

    public static ArrayList<Sessions> getSessions()
    {
        if (sessions.size()==0)
        {
            sessions.add(new Sessions(25,5,"Email my boss"));
            sessions.add(new Sessions(20,5,"Sweep the House"));

        }
        return sessions;
    }

    public static void createNewSession()
    {
        sessions.add(new Sessions(25,25,"Your activity"));
    }

    public static void removeSession(int position)
    {
        sessions.remove(position);
    }
}
