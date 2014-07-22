package ru.alastar.game.ai.npc;

import ru.alastar.game.ai.BaseAI;
import ru.alastar.main.net.Server;

public class NPCAI extends BaseAI
{
    public NPCAI()
    {
    }

    public void Say(String msg)
    {
        Server.ProcessChat(msg, this.getEntity());
    }

    @Override
    public String getClassPath()
    {
        return "ru.alastar.game.ai.npc.NPCAI";
    }
}
