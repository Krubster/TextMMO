package ru.alastar.game.systems;

import java.util.Hashtable;

import ru.alastar.game.Entity;
import ru.alastar.game.Spell;

public class MagicSystem
{

    public static Hashtable<String, Spell> spells = new Hashtable<String, Spell>();
    
    public static void addSpell(String s, Spell spell)
    {
        spells.put(s, spell);
    }
    
    public static void tryCast(Entity caster, Entity target, String spellName)
    {
        if(spells.containsKey(spellName.toLowerCase()))
        {
            spells.get(spellName.toLowerCase()).invoke(caster, target);
        }
    }

    public static Spell getSpell(String spellname)
    {
        return spells.get(spellname);
    }
    
}
