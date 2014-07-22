package ru.alastar.main.handlers;

import ru.alastar.main.net.Server;
import ru.alastar.main.net.ConnectedClient;

public class LoginHandler extends Handler
{
    public LoginHandler()
    {
        this.numOfArgs = 2;
        this.description = "Try to log in";
    }

    @Override
    public void execute(String[] args, ConnectedClient c)
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
