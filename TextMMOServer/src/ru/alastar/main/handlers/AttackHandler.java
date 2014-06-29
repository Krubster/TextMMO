package ru.alastar.main.handlers;

import com.esotericsoftware.kryonet.Connection;

import ru.alastar.main.net.Server;

public class AttackHandler extends Handler
{
    public AttackHandler()
    {
        this.numOfArgs = 1;
        this.description = "Attacks entity with given id";
    }

    @Override
    public void execute(String[] args, Connection c)
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
