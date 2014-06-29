package ru.alastar.main.handlers;

import com.esotericsoftware.kryonet.Connection;

import ru.alastar.main.net.Server;

public class ActionHandler extends Handler
{
    public ActionHandler()
    {
        this.description = "Acts";
    }

    @Override
    public void execute(String[] args, Connection c)
    {
        try
        {
            Server.HandleAction(Server.getActionFromString(args[1]), c, args);
        } catch (Exception e)
        {
            Server.handleError(e);
        }
    }

}
