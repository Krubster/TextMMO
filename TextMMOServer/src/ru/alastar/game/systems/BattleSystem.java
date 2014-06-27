package ru.alastar.game.systems;

import ru.alastar.game.Entity;

public class BattleSystem {
	public static int baseDamage = 1;
	public static int baseSpeed = 250; // in milliseconds

	public static int calculateDamage(Entity from, Entity to) {
		int d = baseDamage;
		/*
		 * Calculating attack damage over here
		 */

		d += from.stats.get("Strength").value / 25;
		return d;
	}

	public static float getWeaponSpeed(Entity from) {
		float s = baseSpeed;
		/*
		 * Calculating attack speed over here
		 */
		s += from.stats.get("Dexterity").value / 25;
		return s;
	}

}
