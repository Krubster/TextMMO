package ru.alastar.game;

import java.util.Hashtable;

public class Skills
{
    public Hashtable<String, Skill> vals;

    public Skills(Hashtable<String, Skill> v)
    {
        this.vals = v;
    }

    public Skills()
    {
        this.vals = new Hashtable<String, Skill>();
    }

    public boolean put(String k, Skill val)
    {
        if (!vals.containsKey(k))
        {
            vals.put(k, val);
            return true;
        } else
            return false;
    }

    public Skill get(String s)
    {
        if (vals.containsKey(s))
            return vals.get(s);
        else
            return null;
    }
}
