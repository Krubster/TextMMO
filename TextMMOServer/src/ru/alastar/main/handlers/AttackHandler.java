package ru.alastar.main.handlers;

import ru.alastar.main.net.Server;
import ru.alastar.main.net.ConnectedClient;

public class AttackHandler extends Handler
{
    public AttackHandler()
    {
        this.numOfArgs = 1;
        this.description = "Attacks entity with given id";
    }

    @Override
    public void execute(String[] args, ConnectedClient c)
    {
        try
        {
            if ((args.length - 1) == numOfArgs)
            {
                Server.HandleAttack(Integer.parseInt(args[1]), c);
            }
        } catch (Exception e)
        {
            Server.handleError(e);
        }
    }

}
