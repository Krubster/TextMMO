package ru.alastar.game;

import ru.alastar.enums.EntityType;

public class Entity{
	
    public int id = -1;
    public String caption = "Generic Entity";
    public EntityType type = EntityType.Human;    
    
    public Entity(int i, String c, EntityType t)
    {
    	this.id = i;
    	this.caption = c;
    	this.type = t;
    }
    
}
