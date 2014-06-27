package ru.alastar.game.worldwide;

import java.util.Hashtable;

import ru.alastar.game.Entity;
import ru.alastar.main.Main;

public class Location {

	public static int id = -1;
	public static String name = "generic location";
	public static Hashtable<Integer, Entity> entities;
	public static Hashtable<Integer, String> locationsAround;

	public Location(int i, String n)
	{
		Location.id = i;
		Location.name = n;
		Location.entities = new Hashtable<Integer, Entity>();
		Location.locationsAround = new Hashtable<Integer, String>();
      //  System.out.println("Location was created!");
	}

	public static void AddEntity(Entity e)
	{
		entities.put(e.id, e);
	}

	public static void TryRemoveEntity(int id2) {
		if(entities.containsKey(id2))
		{
			Main.WarnLeave(entities.get(id2));
			entities.remove(id2);
		}
		
	}
	
}
