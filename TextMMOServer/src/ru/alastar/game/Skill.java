package ru.alastar.game;

public class Skill {
	public String name;
	public int value;
	public int maxValue;
	public float hardness;
	public String primaryStat;
	public String secondaryStat;

	public Skill(String n, int v, int mV, float h, String pS, String sS) {
		this.name = n;
		this.value = v;
		this.maxValue = mV;
		this.hardness = h;
		this.primaryStat = pS;
		this.secondaryStat = sS;
	}

	public void raise(int how) {
		value += how;
	}
}
