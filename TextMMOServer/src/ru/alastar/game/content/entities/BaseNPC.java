package ru.alastar.game.content.entities;

import java.util.ArrayList;

import ru.alastar.enums.EntityType;
import ru.alastar.game.Entity;
import ru.alastar.game.Skills;
import ru.alastar.game.Stats;
import ru.alastar.game.worldwide.Location;

public class BaseNPC extends Entity
{

    public BaseNPC(int i, String c, EntityType t, Location l, Skills sk,
            Stats st, ArrayList<String> s)
    {
        super(i, c, t, l, sk, st, s);

    }

}
