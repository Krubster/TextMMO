package ru.alastar.main.handlers;

import ru.alastar.main.net.Server;
import ru.alastar.main.net.ConnectedClient;

public class MoveHandler extends Handler
{
    public MoveHandler()
    {
        this.numOfArgs = 1;
        this.description = "Moves you to the location with given id";
    }

    @Override
    public void execute(String[] args, ConnectedClient c)
    {
        try
        {
            if ((args.length - 1) == numOfArgs)
            {
                Server.HandleMove(Integer.parseInt(args[1]), c);
            }
        } catch (Exception e)
        {
            Server.handleError(e);
        }
    }

}
