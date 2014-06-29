package ru.alastar.main.handlers;

import com.esotericsoftware.kryonet.Connection;

import ru.alastar.main.net.Server;

public class MoveHandler extends Handler
{
    public MoveHandler()
    {
        this.numOfArgs = 1;
        this.description = "Moves you to the location with given id";
    }

    @Override
    public void execute(String[] args, Connection c)
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
