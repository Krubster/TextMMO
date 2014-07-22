package ru.alastar.game.ai.npc;

import ru.alastar.game.Entity;

public class GuardAI extends NPCAI
{

    public GuardAI()
    {
    }


    @Override
    public String getClassPath()
    {
        return "ru.alastar.game.ai.npc.GuardAI";
    }  
    
    @Override
    public void OnSeeEntity(Entity y)
    {
       // Main.Log("[GUARD]"," See the " + y.caption);
    }
}
