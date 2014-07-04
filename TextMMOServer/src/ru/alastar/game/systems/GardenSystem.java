package ru.alastar.game.systems;

import java.util.ArrayList;
import java.util.Date;

import ru.alastar.database.DatabaseClient;
import ru.alastar.game.PlantsType;
import ru.alastar.game.worldwide.Location;
import ru.alastar.main.Main;
import ru.alastar.main.net.Server;

public class GardenSystem
{

    // FULLY BROKEN!
    public static ArrayList<PlantsType> growingPlants = new ArrayList<PlantsType>();

    public static void StartGrowTimer()
    {
        Main.service.execute(new Runnable()
        {
            public void run()
            {
                try
                {
                    for (;;)
                    {
                        notifyPlants();
                    }
                } catch (Exception e)
                {
                    Server.handleError(e);
                }
            }

            private void notifyPlants()
            {
                Date d = new Date();
                for (PlantsType p : growingPlants)
                {
                    if (p.finish.before(d))
                    {
                        FinishPlant(p);
                        notifyPlants();
                        break;
                    }
                }
            }

            private void FinishPlant(PlantsType p)
            {
                p.Finish();
                growingPlants.remove(p);
                DatabaseClient
                        .commandExecute("DELETE FROM plants WHERE locationId="
                                + p.loc.id + " LIMIT 1;");
                // Main.Log("[DEBUG]","Finish plant grow");
            }
        });
    }

    public static void addGrowingPlant(PlantsType p)
    {
        growingPlants.add(p);
        Server.SavePlant(p);
    }

    public static void SaveAll()
    {
        for (PlantsType p : growingPlants)
        {
            Server.SavePlant(p);
        }
    }

    public static PlantsType getGrowsFromLoc(Location l)
    {
        for (PlantsType p : growingPlants)
        {
            if (p.loc.id == l.id)
            {
                return p;
            }
        }
        return null;
    }

}
