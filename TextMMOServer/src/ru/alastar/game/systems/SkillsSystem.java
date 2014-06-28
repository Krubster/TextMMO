package ru.alastar.game.systems;

import ru.alastar.game.Entity;
import ru.alastar.game.Skill;
import ru.alastar.main.net.Server;

public class SkillsSystem
{

    public static float getChanceFromSkill(Entity e, Skill s)
    {
        float c = 0.15F;
        
        if (s.value > 0)
            c += s.value / 100;

        return c;

    }

    public static void tryRaiseSkill(Entity e, Skill s)
    {
        float h = Server.random.nextFloat();
        if (h > s.value / 100 + 0.5)
        {
            if (s.value + 1 <= s.maxValue)
            {
                s.raise((int) 1);
                Server.warnEntity(e, "Your skill [" + s.name
                        + "] have been increased by 1, now it is " + s.value);
            }
        }
    }

    public static int getSpellStrength(String string, Entity caster)
    {
        int s = 1;
        
        if( caster.skills.get(string).value > 0)
        s += caster.skills.get(string).value / caster.skills.get(string).maxValue * 10;
        
        return s;
    }

}
