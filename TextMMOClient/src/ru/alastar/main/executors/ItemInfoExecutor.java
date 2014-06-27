package ru.alastar.main.executors;

import ru.alastar.game.Item;
import ru.alastar.main.Main;
import ru.alastar.main.net.Client;

public class ItemInfoExecutor extends CommandExecutor
{
    public ItemInfoExecutor()
    {
        super();
        this.numOfArgs = 1;
        this.description = "Shows item info Usage: client item {id}";
        this.specificMode = ClientMode.Game;
    }

    @Override
    public void execute(String[] args)
    {
        try
        {
            Item i = Client.inventory.get(Integer.parseInt(args[0]));
            Main.Say("+++++[" + i.caption + "]+++++");
            Main.Say("+++[ID:" + i.id + "]+++");
            Main.Say("+++[Amount:" + i.amount + "]+++");

            for (String s : i.attributes.keySet())
            {
                Main.Say("+++[" + s + " - " + i.attributes.get(s) + "]+++");
            }
            Main.Say("+++++[-]+++++");
        } catch (Exception e)
        {
            System.out.println(this.description);
        }
    }
}
