package ru.alastar.game.ai;

import java.util.Timer;

import ru.alastar.game.Entity;
import ru.alastar.game.Spell;

public class BaseAI implements AI
{

    public float  reactTime;
    public Entity entity;
    public Timer  reactionTimer = null;

    @Override
    public float getReactionTime()
    {
        return reactTime;
    }
    
    @Override
    public Timer getReactionTimer()
    {
        return reactionTimer;
    }
    
    public void StartChasingTarget()
    {  
     //   if(getReactionTimer() != null)
     //       getReactionTimer().cancel();

    }

    @Override
    public void setReactionTime(float t)
    {
        reactTime = t;
    }

    @Override
    public Entity getEntity()
    {
        return entity;
    }

    @Override
    public void setEntity(Entity e)
    {
        entity = e;
    }

    @Override
    public void OnGetDamage(Entity from, int amt)
    {
    }

    @Override
    public void OnSeeEntity(Entity who)
    {
    }

    @Override
    public void OnHitEntity(Entity who)
    {
    }

    @Override
    public void OnLostTarget()
    {
    }

    @Override
    public void OnTarget(Entity who)
    {
    }

    @Override
    public void OnCast(Spell spell)
    {
    }

    @Override
    public void OnTick()
    {
    }

    @Override
    public void OnHear(Entity from, String words)
    {
    }

    @Override
    public void Save()
    {
       // Server.SaveAI(this);
    }

    @Override
    public String getClassPath()
    {
        return "ru.alastar.game.ai.BaseAI";
    }

    @Override
    public void OnLostEntity(Entity who)
    {        
    }

    @Override
    public void OnDeath(Entity from)
    {        
    }

    @Override
    public void OnKill(Entity who)
    {        
    }

}
