package ru.alastar.game;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.alastar.enums.ActionType;
import ru.alastar.enums.EntityType;
import ru.alastar.game.systems.BattleSystem;
import ru.alastar.game.systems.SkillsSystem;
import ru.alastar.game.worldwide.Location;
import ru.alastar.main.net.Server;

public class Entity extends Transform {

	public int id = -1;
	public String caption = "Generic Entity";
	public EntityType type = EntityType.Human;
	public Stats stats;
	public Skills skills;

	public static int startHits = 15;

	public Entity(int i, String c, EntityType t, Location l, Skills sk, Stats st) {
		super(l);
		this.id = i;
		this.caption = c;
		this.type = t;
		this.skills = sk;
		this.stats = st;
	}

	public void RemoveYourself() {
		Server.saveEntity(this);
		this.loc.RemoveEntity(this);
	}

	public void tryCut() {
	
	//	for(String s: loc.flags.keySet())
		//{
	//		Main.Log("[Location Flags]", s);
	///	}
		
       if(loc.haveFlag("Wood"))
       {
    	   Item woodcutter = Server.checkInventory(this, ActionType.Cut);
    	   if(woodcutter != null)
    	   {
        	   Server.warnEntity(this, "You chop the tree...");
    		   woodcutter.diffValue("Durability", 1);
    		   if(woodcutter.getAttributeValue("Durability") <= 0)
    		   {
    		       Server.DestroyItem(Server.getInventory(this), woodcutter);
    		   }
    		   if(SkillsSystem.getChanceFromSkill(this, skills.get("Lumberjacking")) < Server.random.nextFloat())
    		   {
    			   loc.getRandomWood(this, skills.get("Lumberjacking"));
    			   SkillsSystem.tryRaiseSkill(this, skills.get("Lumberjacking"));
            	   Server.warnEntity(this, "You harvest some wood");
    		   }
    		   else
    		   {
            	   Server.warnEntity(this, "You failed to cut tree");
    		   }
    	   }
    	   else{
        	   Server.warnEntity(this, "You dont have any instrument to perform this action");}
       }
       else
       {
    	   Server.warnEntity(this, "There's no wood");
       }
	}

	public void tryGrow() {
	       if(loc.haveFlag("Plough"))
	       {
	    	   
	       }
	}

	public void tryHerd() {
	       if(loc.haveFlag("Plants"))
	       {
	    	   
	       }
	}

	public void tryMine() {
	       if(loc.haveFlag("Mine"))
	       {
	    	   
	       }
	}

	public void tryCast(int spellId, int id2) {

	}

	public void tryAttack(Entity e) {
		if (e != null) {
			e.dealDamage(this, BattleSystem.calculateDamage(this, e));
			Server.warnEntity(this, "You trying to hit " + e.caption + "...");
		}
	}

	private void dealDamage(Entity entity, int calculateDamage) {
		int curHits = this.stats.get("Hits").value;
		if ((curHits - calculateDamage) > 0) {
			this.stats.set("Hits", curHits - calculateDamage, this, true);
			Server.warnEntity(
					this,
					entity.caption + " the " + entity.type.name()
							+ " hit you! Your hits now is: "
							+ this.stats.get("Hits").value);
		} else {
			Server.EntityDead(this);
		}
	}

	public void setRebirthHitsAmount() {
		this.stats.set("Hits", startHits, this, true);
	}

	public void startAttack(final int id2) {
		ExecutorService service = Executors.newCachedThreadPool();
		final Entity we = this;
		service.submit(new Runnable() {
			float lastHit = System.currentTimeMillis();

			public void run() {
				try {
					for (;;) {
						if (loc.getEntityById(id2) != null) {
							if (System.currentTimeMillis() - lastHit >= BattleSystem
									.getWeaponSpeed(we)) {
								tryAttack(loc.getEntityById(id2));
							}
						} else {
							break;
						}
					}
				} catch (Exception e) {
					Server.handleError(e);
				}
			}

		});
	}

}
