package ru.alastar.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ru.alastar.enums.ActionType;
import ru.alastar.enums.EntityType;
import ru.alastar.enums.EquipType;
import ru.alastar.game.systems.BattleSystem;
import ru.alastar.game.systems.GardenSystem;
import ru.alastar.game.systems.MagicSystem;
import ru.alastar.game.systems.SkillsSystem;
import ru.alastar.game.worldwide.Location;
import ru.alastar.main.Main;
import ru.alastar.main.net.Server;

public class Entity extends Transform
{

    public int               id        = -1;
    public String            caption   = "Generic Entity";
    public EntityType        type      = EntityType.Human;
    public Stats             stats;
    public Skills            skills;
    public ArrayList<String> knownSpells;

    public boolean           invul     = false;
    public float             invulTime = 15;              // in seconds
    public static int        startHits = 15;
    public Timer             battleTimer;
    public Entity            target;

    public Entity(int i, String c, EntityType t, Location l, Skills sk,
            Stats st, ArrayList<String> k)
    {
        super(l);
        this.id = i;
        this.caption = c;
        this.type = t;
        this.skills = sk;
        this.stats = st;
        this.knownSpells = k;
        battleTimer = null;
        target = null;
    }

    public void RemoveYourself()
    {
        Server.saveEntity(this);
        Server.entities.remove(id);
        this.loc.RemoveEntity(this);
    }

    public void tryCut()
    {

        if (loc.haveFlag("Wood"))
        {
            Item woodcutter = Server.checkInventory(this, ActionType.Cut);
            if (woodcutter != null)
            {
                Server.warnEntity(this, "You chop the tree...");
                woodcutter.diffValue("Durability", 1);
                if (woodcutter.getAttributeValue("Durability") <= 0)
                {
                    Server.DestroyItem(Server.getInventory(this), woodcutter);
                }
                if (SkillsSystem
                        .getChanceFromSkill(skills.get("Lumberjacking")) > Server.random
                        .nextFloat())
                {
                    loc.getRandomMaterial(this, skills.get("Lumberjacking"),
                            Location.woods);
                    SkillsSystem.tryRaiseSkill(this,
                            skills.get("Lumberjacking"));
                    Server.warnEntity(this, "You harvest some wood");
                } else
                {
                    Server.warnEntity(this, "You failed to cut tree");
                }
            } else
            {
                Server.warnEntity(this,
                        "You dont have any instrument to perform this action");
            }
        } else
        {
            Server.warnEntity(this, "There's no wood");
        }
    }

    public void tryGrow(String seed)
    {
        // Main.Log("[DEBUG]", "Grow action! Seed: " + seed);
        if (loc.haveFlag("Plough"))
        {
            if (GardenSystem.getGrowsFromLoc(loc) == null)
            {
                Date d = new Date();
                d.setTime((long) (d.getTime() + Server.getPlantGrowTime(seed)));
                GardenSystem.addGrowingPlant(new PlantsType(seed, d, this.loc));
                Server.consumeItem(this, seed);
                Server.warnEntity(this, "Plant begin to grow. It will grow on "
                        + d.toString());
                SkillsSystem.tryRaiseSkill(this, this.skills.get("Herding"));
            } else
                Server.warnEntity(this, "There's already growing plants!");
        } else
            Server.warnEntity(this, "There's no plough in this location!");
    }

    public void tryHerd()
    {
        if (loc.haveFlag("Plants"))
        {
            Item item = new Item(Server.getFreeItemId(), id,
                    this.loc.getFlag("Plants").value, Server.random.nextInt(2)
                            + 1
                            + SkillsSystem.getSkillBonus(this.skills
                                    .get("Herding")), this.loc, EquipType.None,
                    ActionType.None, new Attributes());
            Inventory inv = Server.getInventory(this);
            if (inv != null)
                inv.AddItem(item);
            this.loc.removeFlag("Plants");
            SkillsSystem.tryRaiseSkill(this, this.skills.get("Herding"));

        } else
            Server.warnEntity(this, "There's no plants in this location!");
    }

    public void tryMine()
    {
        if (loc.haveFlag("Mine"))
        {
            Item pickaxe = Server.checkInventory(this, ActionType.Mine);
            if (pickaxe != null)
            {
                Server.warnEntity(this, "You start to mine...");
                pickaxe.diffValue("Durability", 1);
                if (pickaxe.getAttributeValue("Durability") <= 0)
                {
                    Server.DestroyItem(Server.getInventory(this), pickaxe);
                }
                if (SkillsSystem.getChanceFromSkill(skills.get("Mining")) > Server.random
                        .nextFloat())
                {
                    loc.getRandomMaterial(this, skills.get("Mining"),
                            Location.miningItems);
                    SkillsSystem.tryRaiseSkill(this, skills.get("Mining"));
                    Server.warnEntity(this, "You found something!");
                } else
                {
                    Server.warnEntity(this,
                            "You failed to get any useful material");
                }
            } else
            {
                Server.warnEntity(this,
                        "You dont have any instrument to perform this action");
            }
        } else
        {
            Server.warnEntity(this, "There's no mine");
        }
    }

    public void tryCast(String spellname, int id2)
    {
        if (knowSpell(spellname))
        {
            if (stats.get("Mana").value >= MagicSystem.getSpell(spellname).manaRequired)
            {
                if (Server.haveItemSet(this,
                        MagicSystem.getSpell(spellname).reagentsNeeded))
                {
                    stats.set(
                            "Mana",
                            stats.get("Mana").value
                                    - MagicSystem.getSpell(spellname).manaRequired,
                            this, true);
                    for (String s : MagicSystem.getSpell(spellname).reagentsNeeded)
                    {
                        Server.consumeItem(this, s);
                    }
                    MagicSystem.tryCast(this, Server.getEntity(id2), spellname);
                } else
                {
                    Server.warnEntity(this, "You dont have enough reagents!");
                }
            } else
                Server.warnEntity(this, "You dont have enough mana!");
        } else
            Server.warnEntity(this, "You dont know that spell!");
    }

    public boolean knowSpell(String s)
    {
        for (String str : knownSpells)
        {
            if (str.equals(s))
            {
                return true;
            }
        }
        return false;
    }

    public void tryAttack(Entity e)
    {
        if (e != null)
        {
            if (SkillsSystem.getChanceFromSkill(this.skills.vals.get("Swords")) > Server.random
                    .nextFloat())
            {
                e.dealDamage(this, BattleSystem.calculateDamage(this, e));
                Server.warnEntity(this, "You trying to hit " + e.caption
                        + "...");
                SkillsSystem
                        .tryRaiseSkill(this, this.skills.vals.get("Swords"));
            } else
            {
                Server.warnEntity(this, "You miss!");
                SkillsSystem
                        .tryRaiseSkill(this, this.skills.vals.get("Swords"));
            }
        }
    }

    private void dealDamage(Entity entity, int calculateDamage)
    {
        if (SkillsSystem.getChanceFromSkill(this.skills.vals.get("Parrying")) > Server.random
                .nextFloat())
        {
            Server.warnEntity(this, "You successfully parried "
                    + entity.caption + "'s attack!");
            SkillsSystem.tryRaiseSkill(this, this.skills.vals.get("Parrying"));
            startAttack(entity.id);

        } else
        {
            int curHits = this.stats.get("Hits").value;
            if ((curHits - calculateDamage) > 0)
            {
                this.stats.set("Hits", curHits - calculateDamage, this, true);
                Server.warnEntity(
                        this,
                        entity.caption + " the " + entity.type.name()
                                + " hit you! Your hits now is: "
                                + this.stats.get("Hits").value);
                SkillsSystem.tryRaiseSkill(this,
                        this.skills.vals.get("Parrying"));

                if (target == null)
                    startAttack(entity.id);

            } else
            {
                Server.EntityDead(this, entity);
            }
        }
    }

    public void setRebirthHitsAmount()
    {
        this.stats.set("Hits", startHits, this, true);
    }

    public void tryStopAttack()
    {
        if (battleTimer != null)
            battleTimer.cancel();
    }

    public void startAttack(final int id2)
    {
        // System.out.println("Start Attack. Weapon speed: " +
        // (long)BattleSystem.getWeaponSpeed(this)*1000);
        if (battleTimer != null)
            battleTimer.cancel();

        battleTimer = new Timer();
        battleTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                // System.out.println("Attack run");
                if (loc.getEntityById(id2) != null)
                {
                    target = loc.getEntityById(id2);
                    if (target.stats.get("Hits").value > 0)
                    {
                        if (!target.invul)
                        {
                            // System.out.println("hit it");
                            tryAttack(target);
                        } else
                        {
                            target = null;
                            battleTimer.cancel();
                        }
                    } else
                    {
                        target = null;
                        battleTimer.cancel();
                    }
                } else
                {
                    // System.out.println("entity null");
                    target = null;
                    battleTimer.cancel();
                }
            }
        }, 0, (long) BattleSystem.getWeaponSpeed(this) * 1000);
    }

}
