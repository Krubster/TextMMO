package ru.alastar.main.handlers;

import com.esotericsoftware.kryonet.Connection;

import ru.alastar.main.net.Server;

public class ChatHandler extends Handler
{
    public ChatHandler()
    {
        this.description = "Sends message in the chat";
    }

    @Override
    public void execute(String[] args, Connection c)
    {
        try
        {
            String msg = "";
            for (int i = 1; i < args.length; ++i)
            {
                msg += " " + args[i];
            }
            Server.ProcessChat(msg, c);
        } catch (Exception e)
        {
            Server.handleError(e);
        }
    }

}
