package ru.alastar.main.executors;

import ru.alastar.main.net.Client;
import ru.alastar.main.net.requests.CastRequest;

public class CastExecutor extends CommandExecutor
{
    public CastExecutor()
    {
        super();
        this.numOfArgs = 0;
        this.description = "Tries to cast spell";
        this.specificMode = ClientMode.Game;
    }

    @Override
    public void execute(String[] args)
    {
        try
        {
            CastRequest r = new CastRequest();
            r.spellId = args[0];
            r.id = Integer.parseInt(args[1]);
            Client.Send(r);
        } catch (Exception e)
        {
            System.out.println(this.description);
        }
    }
}
