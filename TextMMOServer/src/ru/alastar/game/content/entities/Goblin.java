package ru.alastar.game.content.entities;

import ru.alastar.enums.EntityType;
import ru.alastar.game.Entity;
import ru.alastar.game.Skills;
import ru.alastar.game.Stats;
import ru.alastar.game.worldwide.Location;

public class Goblin extends Entity {

	public Goblin(int i, String c, EntityType t, Location l, Skills sk, Stats st) {
		super(i, c, t, l, sk, st);

	}

}
