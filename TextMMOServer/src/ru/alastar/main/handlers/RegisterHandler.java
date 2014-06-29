package ru.alastar.main.handlers;

import com.esotericsoftware.kryonet.Connection;

import ru.alastar.main.net.Server;

public class RegisterHandler extends Handler
{
    public RegisterHandler()
    {
        this.numOfArgs = 5;
        this.description = "Registers user with given credentials";
    }

    @Override
    public void execute(String[] args, Connection c)
    {
        try
        {
            if ((args.length - 1) == numOfArgs)
            {
                Server.ProcessRegister(args[1], args[2], args[3], args[4],
                        args[5], c); // args[1] - login, args[2] - password,
                                     // args[3] - mail, args[4] - character
                                     // name, args[5] - character race
            }
        } catch (Exception e)
        {
            Server.handleError(e);
        }
    }

}
