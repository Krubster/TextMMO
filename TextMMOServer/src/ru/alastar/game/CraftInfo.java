package ru.alastar.game;

import java.util.ArrayList;

import ru.alastar.enums.ActionType;
import ru.alastar.enums.EquipType;

public class CraftInfo
{

    public ArrayList<String> neededItems;
    public String            skill;
    public int               skillVal;
    public String            caption;
    public EquipType         eqType;
    public ActionType        aType;
    public Attributes        attributes;

    public CraftInfo(ArrayList<String> n, String sk, int skV, String c,
            EquipType eT, ActionType aT, Attributes a)
    {
        this.neededItems = n;
        this.skill = sk;
        this.skillVal = skV;
        this.caption = c;
        this.eqType = eT;
        this.aType = aT;
        this.attributes = a;
    }

}
