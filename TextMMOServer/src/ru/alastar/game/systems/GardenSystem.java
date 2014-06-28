package ru.alastar.game.systems;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.alastar.game.PlantsType;
import ru.alastar.main.Main;
import ru.alastar.main.net.Server;

public class GardenSystem
{

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
                        p.Finish();
                        growingPlants.remove(p);
                        notifyPlants();
                        break;
                    }
                }
            }

        });
    }

    public static void addGrowingPlant(PlantsType p)
    {
        growingPlants.add(p);
    }

    public static void SaveAll()
    {
        for (PlantsType p : growingPlants)
        {
            Server.SavePlant(p);
        }
    }

}
