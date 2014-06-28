package ru.alastar.main.executors;

import java.util.Calendar;
import java.util.Date;

import ru.alastar.enums.ActionType;
import ru.alastar.main.Main;
import ru.alastar.main.net.Client;
import ru.alastar.main.net.requests.ActionRequest;

public class ClientInfoExecutor extends CommandExecutor
{
    public ClientInfoExecutor()
    {
        super();
        this.numOfArgs = 0;
        this.description = "Shows client info";
        this.specificMode = ClientMode.All;

    }

    @Override
    public void execute(String[] args)
    {
        try
        {
            System.out.println("[TextMMO Client");
            System.out.println("[Client Version: " + Main.version);
            System.out.println("[Authors: ");
            for(String s: Main.authors)
            {
                System.out.println(" - " + s);
            }
            System.out.println("[Copyright: 2014 - " + Calendar.getInstance().get(Calendar.YEAR) + " (c)");

        } catch (Exception e)
        {
            System.out.println(this.description);
        }
    }
}