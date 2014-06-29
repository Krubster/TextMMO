package ru.alastar.main.handlers;

import com.esotericsoftware.kryonet.Connection;

import ru.alastar.main.net.Server;

public class CraftHandler extends Handler
{
    public CraftHandler()
    {
        this.description = "Tries to craft specified item";
    }

    @Override
    public void execute(String[] args, Connection c)
    {
        try
        {
            // Main.Log("[DEBUG]", "Handling craft command");

            if (args.length == 2)
            {
                // Main.Log("[DEBUG]", "1 arg");
                Server.HandleCraft(args[1], 1, c);
            } else if (args.length == 3)
            {
                // Main.Log("[DEBUG]", "2 args");

                Server.HandleCraft(args[1], Integer.parseInt(args[2]), c);
            }
        } catch (Exception e)
        {
            Server.handleError(e);
        }
    }

}
