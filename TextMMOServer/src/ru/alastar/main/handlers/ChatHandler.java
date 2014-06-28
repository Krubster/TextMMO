package ru.alastar.main.handlers;

import com.esotericsoftware.kryonet.Connection;

import ru.alastar.main.net.Server;

public class ChatHandler extends Handler
{
  public ChatHandler()
  {
      this.numOfArgs = 1;
      this.description = "Sends message in the chat";
  }
  
  @Override
  public void execute(String[] args, Connection c)
  {
   try{
       if((args.length - 1) == numOfArgs)
       {
           Server.ProcessChat(args[1], c);
       }
   }catch(Exception e)
   {
       Server.handleError(e);
   }
  }
  
}
