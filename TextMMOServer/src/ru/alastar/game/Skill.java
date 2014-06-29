package ru.alastar.game;

import ru.alastar.main.net.Server;
import ru.alastar.main.net.responses.AddStatResponse;

public class Skill
{
    public String name;
    public int    value;
    public int    maxValue;
    public float  hardness;
    public String primaryStat;
    public String secondaryStat;

    public Skill(String n, int v, int mV, float h, String pS, String sS)
    {
        this.name = n;
        this.value = v;
        this.maxValue = mV;
        this.hardness = h;
        this.primaryStat = pS;
        this.secondaryStat = sS;
    }

    public void raise(int how, Entity e, boolean player)
    {
        value += how;
        if (player)
        {
            try
            {
                AddStatResponse r = new AddStatResponse();
                r.name = name;
                r.sValue = value;
                r.mValue = maxValue;
                Server.SendTo(Server.getClientByEntity(e).connection, r);
            } catch (Exception e1)
            {
                Server.handleError(e1);
            }
        }
    }
}
