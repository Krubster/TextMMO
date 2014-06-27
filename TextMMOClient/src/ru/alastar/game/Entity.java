package ru.alastar.game;

import java.util.Hashtable;

import ru.alastar.enums.EntityType;

public class Entity{
	
    public int id = -1;
    public String caption = "Generic Entity";
    public EntityType type = EntityType.Human;
    public Hashtable<String, Skill> skills;
    public Hashtable<String, Statistic> stats;

    
    
    public Entity(int i, String c, EntityType t)
    {
    	this.id = i;
    	this.caption = c;
    	this.type = t;
    	skills = new Hashtable<String, Skill>();
    	stats = new Hashtable<String, Statistic>();
    }
    
}
