package ru.alastar.main.handlers;

import com.esotericsoftware.kryonet.Connection;

import ru.alastar.main.net.Server;

public class LoginHandler extends Handler
{
    public LoginHandler()
    {
        this.numOfArgs = 2;
        this.description = "Try to log in";
    }

    @Override
    public void execute(String[] args, Connection c)
    {
        try
        {
            if ((args.length - 1) == numOfArgs)
            {
                Server.Login(args[1], args[2], c);
            }
        } catch (Exception e)
        {
            Server.handleError(e);
        }
    }

}
