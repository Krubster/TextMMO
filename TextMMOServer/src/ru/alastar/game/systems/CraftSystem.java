package ru.alastar.game.systems;

import java.util.Hashtable;

import ru.alastar.game.CraftInfo;
import ru.alastar.game.Entity;
import ru.alastar.game.Inventory;
import ru.alastar.game.Item;
import ru.alastar.main.net.Server;

public class CraftSystem
{

    public static Hashtable<String, CraftInfo> crafts = new Hashtable<String, CraftInfo>();

    public static void registerCraft(String s, CraftInfo c)
    {
        crafts.put(s, c);
    }

    public static void tryCraft(String string, int i, Entity e)
    {
        // Main.Log("[DEBUG]", "try craft, i: " + i + " name: " + string);
        try
        {
            for (int c = 0; c < i; ++c)
            {
                CraftInfo ci = crafts.get(string.toLowerCase());
                if (ci != null)
                {
                    if (ci.skillVal <= e.skills.get(ci.skill).value)
                    {
                        if (Server.haveItemSet(e, ci.neededItems))
                        {
                            if (SkillsSystem.getCraftSkillChance(ci.skillVal,
                                    e.skills.get(ci.skill)) > Server.random
                                    .nextFloat())
                            {
                                Item item = new Item(Server.getFreeItemId(),
                                        e.id, ci.caption, i, e.loc, ci.eqType,
                                        ci.aType, ci.attributes);
                                Inventory inv = Server.getInventory(e);
                                if (inv != null)
                                    inv.AddItem(item);
                                SkillsSystem.tryRaiseSkill(e,
                                        e.skills.get(ci.skill));

                                for (String s : ci.neededItems)
                                {
                                    Server.consumeItem(e, s);
                                }
                                Server.warnEntity(e, "You succesfully created "
                                        + ci.caption);

                            } else
                            {
                                Server.warnEntity(e, "You failed to create "
                                        + ci.caption);
                                SkillsSystem.tryRaiseSkill(e,
                                        e.skills.get(ci.skill));
                            }
                        } else
                        {
                            Server.warnEntity(e,
                                    "You dont have needed items for crafting "
                                            + ci.caption
                                            + "\n Needed items is:\n");
                            for (String str : ci.neededItems)
                            {

                                Server.warnEntity(e, " - " + str + "\n");

                            }
                        }
                    } else
                        Server.warnEntity(e,
                                "You have too little skill to create "
                                        + ci.caption);
                } else
                    Server.warnEntity(e, "There's no item with given caption");
            }
        } catch (Exception e1)
        {
            Server.handleError(e1);
        }
    }

}
