package ru.alastar.main.executors;

import ru.alastar.main.Main;
import ru.alastar.main.net.Client;

public class LogoutExecutor extends CommandExecutor
{
    public LogoutExecutor()
    {
        super();
        this.numOfArgs = 0;
        this.description = "Logging out from the server";
        this.specificMode = ClientMode.Game;

    }
  @Override
  public void execute(String[] args)
  {
      try{
         Client.client.close();
         Main.currentMode = ClientMode.Login;
      }catch(Exception e)
      {
          System.out.println(this.description);
      }
  }
}
