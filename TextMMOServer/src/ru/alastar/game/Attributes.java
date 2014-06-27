package ru.alastar.game;

import java.util.Hashtable;

public class Attributes
{

    public Hashtable<String, Integer> values;

    public Attributes()
    {
        values = new Hashtable<String, Integer>();
    }

    public Attributes(Hashtable<String, Integer> v)
    {
        this.values = v;
    }

    public void addAttribute(String s, int val)
    {
        values.put(s, val);
    }

    public int getValue(String s)
    {
        return values.get(s);
    }

    public boolean setValue(String s, int v)
    {
        if (values.containsKey(s))
        {
            values.remove(s);
            values.put(s, v);
            return true;
        } else
        {
            return false;
        }
    }
}
