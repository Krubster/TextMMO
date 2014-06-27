package ru.alastar.main.executors;

import ru.alastar.main.net.Client;

public class GetIdExecutor extends CommandExecutor
{
    public GetIdExecutor()
    {
        super();
        this.numOfArgs = 0;
        this.description = "Shows your id";
        this.specificMode = ClientMode.Game;

    }

    @Override
    public void execute(String[] args)
    {
        try
        {
            System.out.println("Your id is: " + Client.id);
        } catch (Exception e)
        {
            System.out.println(this.description);
        }
    }
}
