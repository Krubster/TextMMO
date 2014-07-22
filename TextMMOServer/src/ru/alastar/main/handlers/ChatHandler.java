package ru.alastar.main.handlers;

import ru.alastar.main.net.Server;
import ru.alastar.main.net.ConnectedClient;

public class ChatHandler extends Handler
{
    public ChatHandler()
    {
        this.description = "Sends message in the chat";
    }

    @Override
    public void execute(String[] args, ConnectedClient c)
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
