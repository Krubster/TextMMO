package ru.alastar.main.executors;

import ru.alastar.main.Main;

public class CommandsCommandExecutor extends CommandExecutor
{
    public CommandsCommandExecutor()
    {
        super();
        numOfArgs = 0;
        this.description = "Usage: client commands";
        this.specificMode = ClientMode.All;
    }

    @Override
    public void execute(String[] args)
    {
        try
        {
            if (args.length == numOfArgs)
            {
                System.out.println("");

                System.out.println("----------------");
                System.out.println("CLIENT COMMANDS:");
                System.out.println("----------------");
                System.out.println("-----0");

                String str = "";
                for (String ccen : Main.clientCommands.keySet())
                {
                    str = "Name: " + ccen + " Description: "
                            + Main.clientCommands.get(ccen).description;
                    System.out.println(str);
                    System.out.println("-----0");

                }
                System.out.println("----------------");

             //   System.out.println("");

                System.out.println("----------------");
//
              //  System.out.println("SERVER COMMANDS:");
             //   System.out.println("----------------");
             //   System.out.println("-----0");

              //  for (String scen : Main.serverCommands.keySet())
              //  {
               //     str = "Name: " + scen + " Description: "
              //              + Main.serverCommands.get(scen).description;
              //      System.out.println(str);
              //      System.out.println("-----0");

              //  }
            //    System.out.println("----------------");
            } else
            {
                System.out.println(this.description);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
