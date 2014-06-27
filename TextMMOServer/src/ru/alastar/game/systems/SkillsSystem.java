package ru.alastar.game.systems;

import ru.alastar.game.Entity;
import ru.alastar.game.Skill;
import ru.alastar.main.net.Server;

public class SkillsSystem {
	
	
	public static float getChanceFromSkill(Entity e, Skill s)
	{
		float c = 0.25F;
		if(s.value > 0)
		c += s.value / 100;
		else
		c += 1;
		return c;
		
	}
	
	public static void tryRaiseSkill(Entity e, Skill s)
	{
		float h = Server.random.nextFloat();
		if(h > s.value / 100)
		{
			if(s.value < s.maxValue && s.value + h <= s.maxValue){
			s.raise((int) h);
			Server.warnEntity(e, "Your skill ["+s.name+"] have been increased by " + (int)h + ", now it is " + s.value);
			}
		}
	}
	
}
