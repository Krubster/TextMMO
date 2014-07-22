package ru.alastar.main;

import ru.alastar.main.handlers.Handler;
import ru.alastar.main.net.Server;

public class Plugin
{
   public void OnLoad(){
       
   }
   
   public void OnDisable(){
       
   }
   
   protected void AddCustomCommand(String s, Handler h)
   {
       Server.registerCommand(s, h);
   }
}
