package ru.alastar.game;

import java.util.ArrayList;

public class Spell
{
    public String            name;
    public String            magicSchool;
    public int               manaRequired;
    public ArrayList<String> reagentsNeeded;

    public Spell(String n, String mS, int m)
    {
        this.name = n;
        this.magicSchool = mS;
        this.manaRequired = m;
        this.reagentsNeeded = new ArrayList<String>();
    }

    public void invoke(Entity caster, Entity target)
    {

    }

}
