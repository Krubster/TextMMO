package ru.alastar.game.ai;

import java.util.Timer;

import ru.alastar.game.Entity;
import ru.alastar.game.Spell;

public interface AI
{

    public float getReactionTime();

    public void setReactionTime(float t);

    public Entity getEntity();

    public void setEntity(Entity e);

    public void OnGetDamage(Entity from, int amt);

    public void OnSeeEntity(Entity who);

    public void OnHitEntity(Entity who);

    public void OnLostTarget();

    public void OnTarget(Entity who);

    public void OnCast(Spell spell);

    public void OnTick();

    public void OnHear(Entity from, String words);

    public void Save();
    
    public void OnDeath(Entity from);
    
    public void OnKill(Entity who);

    public String getClassPath();
    
    public void OnLostEntity(Entity who);

    Timer getReactionTimer();
    
}
