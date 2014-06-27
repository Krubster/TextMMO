package ru.alastar.game;

import java.util.Hashtable;

import ru.alastar.main.net.Server;
import ru.alastar.main.net.responses.AddStatResponse;

public class Stats
{
    public Hashtable<String, Statistic> vals;

    public Stats(Hashtable<String, Statistic> v)
    {
        this.vals = v;
    }

    public Stats()
    {
        this.vals = new Hashtable<String, Statistic>();
    }

    public boolean put(String k, Statistic val)
    {
        if (!vals.containsKey(k))
        {
            vals.put(k, val);
            return true;
        } else
            return false;
    }

    public Statistic get(String s)
    {
        if (vals.containsKey(s))
            return vals.get(s);
        else
            return null;
    }

    public void set(String s, int i, Entity whom, boolean player)
    {
        if (vals.containsKey(s))
        {
            vals.get(s).value = i;
            if (player)
            {
                AddStatResponse r = new AddStatResponse();
                r.name = s;
                r.sValue = vals.get(s).value;
                r.mValue = vals.get(s).maxValue;
                Server.SendTo(Server.getClientByEntity(whom).connection, r);
            }
        }
    }
}
