package ru.alastar.main.handlers;

import ru.alastar.main.net.Server;
import ru.alastar.main.net.ConnectedClient;

public class HelpHandler extends Handler
{
    public HelpHandler()
    {
        this.numOfArgs = 0;
        this.description = "Lists all of the server commands";
    }

    @Override
    public void execute(String[] args, ConnectedClient c)
    {
        try
        {
            if ((args.length - 1) == numOfArgs)
            {
                String msg = "Server commands: \n";
                for (String k : Server.commands.keySet())
                {
                    msg += k + " - " + Server.commands.get(k).description
                            + "\n";
                }
                Server.warnClient(c, msg);
            }
        } catch (Exception e)
        {
            Server.handleError(e);
        }
    }

}
