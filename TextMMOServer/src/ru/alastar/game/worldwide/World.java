package ru.alastar.game.worldwide;

import java.util.Hashtable;

import ru.alastar.main.Main;

public class World {

	public String name = "generic World";
	public Hashtable<Integer, Location> locations;

	public World(String n, Hashtable<Integer, Location> ls) {
		this.name = n;
		this.locations = ls;
		Main.Log("[WORLD]", "World " + name + " is ready! Locations: "
				+ locations.size());
	}

}
