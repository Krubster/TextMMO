package ru.alastar.main.executors;

import ru.alastar.main.net.Client;
import ru.alastar.main.net.requests.MoveRequest;

public class MoveCommandExecutor extends CommandExecutor
{
    public MoveCommandExecutor()
    {
        super();
        this.numOfArgs = -1;
        this.description = "Moves to the near location Usage: server move {id}";
        this.specificMode = ClientMode.Game;

    }

    @Override
    public void execute(String[] args)
    {
        try
        {
            MoveRequest r = new MoveRequest();
            r.id = Integer.parseInt(args[0]);
            Client.Send(r);
        } catch (Exception e)
        {
            System.out.println("Invalid arguments");
            System.out.println(this.description);
        }
    }
}
