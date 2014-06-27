package ru.alastar.game;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.alastar.enums.ActionType;
import ru.alastar.enums.EntityType;
import ru.alastar.game.systems.BattleSystem;
import ru.alastar.game.systems.MagicSystem;
import ru.alastar.game.systems.SkillsSystem;
import ru.alastar.game.worldwide.Location;
import ru.alastar.main.net.Server;

public class Entity extends Transform
{

    public int        id        = -1;
    public String     caption   = "Generic Entity";
    public EntityType type      = EntityType.Human;
    public Stats      stats;
    public Skills     skills;
    public ArrayList<String> knownSpells;
    
    
    public static int startHits = 15;

    public Entity(int i, String c, EntityType t, Location l, Skills sk, Stats st,ArrayList<String> k)
    {
        super(l);
        this.id = i;
        this.caption = c;
        this.type = t;
        this.skills = sk;
        this.stats = st;
        this.knownSpells = k;
    }

    public void RemoveYourself()
    {
        Server.saveEntity(this);
        this.loc.RemoveEntity(this);
    }

    public void tryCut()
    {

        // for(String s: loc.flags.keySet())
        // {
        // Main.Log("[Location Flags]", s);
        // / }

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
                if (SkillsSystem.getChanceFromSkill(this,
                        skills.get("Lumberjacking")) > Server.random
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

    public void tryGrow()
    {
        if (loc.haveFlag("Plough"))
        {

        }
    }

    public void tryHerd()
    {
        if (loc.haveFlag("Plants"))
        {

        }
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
                if (SkillsSystem.getChanceFromSkill(this, skills.get("Mining")) > Server.random
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
      if(knowSpell(spellname))
      {
          if(stats.get("Mana").value >= MagicSystem.getSpell(spellname).manaRequired)
          {
              if(Server.haveItemSet(this, MagicSystem.getSpell(spellname).reagentsNeeded)){
              stats.set("Mana", stats.get("Mana").value - MagicSystem.getSpell(spellname).manaRequired, this, true);
              for(String s: MagicSystem.getSpell(spellname).reagentsNeeded)
              {
                  Server.consumeItem(this, s);
              }
              MagicSystem.tryCast(this, Server.getEntity(id2), spellname);
              }
              else
              {
                  Server.warnEntity(this, "You dont have enough reagents!");
              }
          }
          else
              Server.warnEntity(this, "You dont have enough mana!");
      }
      else
          Server.warnEntity(this, "You dont know that spell!");
    }

    public boolean knowSpell(String s)
    {
        for(String str: knownSpells)
        {
            if(str.equals(s))
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
            e.dealDamage(this, BattleSystem.calculateDamage(this, e));
            Server.warnEntity(this, "You trying to hit " + e.caption + "...");
        }
    }

    private void dealDamage(Entity entity, int calculateDamage)
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
        } else
        {
            Server.EntityDead(this);
        }
    }

    public void setRebirthHitsAmount()
    {
        this.stats.set("Hits", startHits, this, true);
    }

    public void startAttack(final int id2)
    {
        ExecutorService service = Executors.newCachedThreadPool();
        final Entity we = this;
        service.submit(new Runnable()
        {
            float lastHit = System.currentTimeMillis();

            public void run()
            {
                try
                {
                    for (;;)
                    {
                        if (loc.getEntityById(id2) != null)
                        {
                            if (System.currentTimeMillis() - lastHit >= BattleSystem
                                    .getWeaponSpeed(we))
                            {
                                tryAttack(loc.getEntityById(id2));
                            }
                        } else
                        {
                            break;
                        }
                    }
                } catch (Exception e)
                {
                    Server.handleError(e);
                }
            }

        });
    }

}
