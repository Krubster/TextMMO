package ru.alastar.game;

public class Statistic
{
    public String name;
    public int    value;
    public int    maxValue;
    public float  hardness;

    public Statistic(String n, int v, int mV, float h)
    {
        this.name = n;
        this.value = v;
        this.maxValue = mV;
        this.hardness = h;
    }
}
