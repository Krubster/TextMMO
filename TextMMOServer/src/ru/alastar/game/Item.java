package ru.alastar.game;

import ru.alastar.enums.ActionType;
import ru.alastar.enums.EquipType;
import ru.alastar.game.worldwide.Location;
import ru.alastar.main.net.Server;

public class Item extends Transform {

	public int id;
	public String caption;
	public int amount;
	public int entityId;
	public EquipType eqType;
	public ActionType aType;
    public Attributes attributes;
	
	public Item(int i, String c, int a, Location l) {
		super(l);
		this.id = i;
		this.caption = c;
		this.amount = a;
		this.entityId = -1;
	}

	public Item(int i, int ei, String c, int a, Location l, EquipType et, ActionType aT, Attributes a1) {
		super(l);
		this.id = i;
		this.caption = c;
		this.amount = a;
		this.entityId = ei;
		this.eqType = et;
		this.aType = aT;
		this.attributes = a1;
		Server.SaveItem(this);
	}

	public int getLocId() {
		try {
			return loc.id;
		} catch (Exception e) {
			return -1;
		}
	}
	
	public int getAttributeValue(String s)
	{
		return attributes.getValue(s);
	}
	
	public boolean setAttributeValue(String s, int v)
	{
		return attributes.setValue(s, v);
	}

	public void diffValue(String s, int i) {
		attributes.setValue(s, attributes.getValue(s) - i);
	}

}
