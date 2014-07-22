package ru.alastar.game.ai.npc;

import ru.alastar.game.Entity;

public class TownCrierAI extends NPCAI
{
    public TownCrierAI()
    {
    }

    @Override
    public void OnHear(Entity from, String words)
    {
        if (words.toLowerCase().contains("news"))
        {
            Say("Now news for now!");
        }
    }

    @Override
    public String getClassPath()
    {
        return "ru.alastar.game.ai.npc.TownCrierAI";
    }
}
