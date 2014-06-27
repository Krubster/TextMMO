package ru.alastar.game;

import java.util.Hashtable;


public class Item{

	public int id;
	public String caption;
	public int amount;
	public Hashtable<String, Integer> attributes;
	
	
	public Item(int i, String c, int a, Hashtable<String, Integer> at) {
		this.id = i;
		this.caption = c;
		this.amount = a;
		this.attributes = at;
	}
}
