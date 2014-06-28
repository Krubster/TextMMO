package ru.alastar.main.handlers;

import com.esotericsoftware.kryonet.Connection;

import ru.alastar.enums.ActionType;
import ru.alastar.main.net.Server;

public class ActionHandler extends Handler
{
  public ActionHandler()
  {
      this.numOfArgs = 1;
      this.description = "Acts";
  }
  
  @Override
  public void execute(String[] args, Connection c)
  {
   try{
       if((args.length - 1) == numOfArgs)
       {
           Server.HandleAction(Server.getActionFromString(args[1]), c);
       }
   }catch(Exception e)
   {
       Server.handleError(e);
   }
  }
  
}
