package ru.alastar.game.spells;

import java.util.ArrayList;

import ru.alastar.game.Entity;
import ru.alastar.game.Spell;
import ru.alastar.game.systems.SkillsSystem;
import ru.alastar.main.net.Server;

public class Heal extends Spell
{

    public Heal()
    {
        super("heal", "magery", 10);
        ArrayList<String> regs = new ArrayList<String>();
        regs.add("ginseng");
        this.reagentsNeeded = regs;
    }

    @Override
    public void invoke(Entity caster, Entity target)
    {
        int h = 0;
        int b = 5 * SkillsSystem.getSpellStrength("Magery", caster);
        if (target.stats.get("Hits").value + b <= target.stats.get("Hits").maxValue)
            h = target.stats.get("Hits").value + b;
        else
            h = target.stats.get("Hits").maxValue
                    - target.stats.get("Hits").value;

        target.stats.set("Hits", h, target, true);
        Server.warnEntity(target, "You was healed! Health restored: " + b);
        Server.warnEntity(caster, "You healed target by " + b);

    }
}
