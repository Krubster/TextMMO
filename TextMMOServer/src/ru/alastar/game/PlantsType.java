package ru.alastar.game;

import java.util.Date;

import ru.alastar.game.worldwide.Location;
import ru.alastar.game.worldwide.LocationFlag;

public class PlantsType {

	public String plantName;
	public Date finish;
	public Location loc;
	
	public PlantsType(String n, Date f, Location l)
	{
		this.plantName = n;
		this.finish = f;
		this.loc = l;
	}
	public void Finish() {
		loc.addFlag("Plants", new LocationFlag(plantName));
	}
}
