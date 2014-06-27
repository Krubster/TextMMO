package ru.alastar.main.executors;

import ru.alastar.game.worldwide.Location;
import ru.alastar.main.net.Client;

public class AroundCommandExecutor extends CommandExecutor {
	public AroundCommandExecutor()
	{
		super();
		this.numOfArgs = 0;
		this.description = "Shows all near entities Usage: client around";
		this.specificMode = ClientMode.Game;
	}
  @Override
  public void execute(String[] args)
  {
	  try{
	  if(numOfArgs == args.length){
		System.out.println("You are looking around...");
	  for(int u: Location.entities.keySet())
	  {
		  if(u != Client.id){
		  System.out.println("[You see " + Location.entities.get(u).caption + " the " + Location.entities.get(u).type.name() + "(id:"+Location.entities.get(u).id+") near you]");
	  }}
	  }
	  else
	  {
		  System.out.println("Invalid arguments");
		  System.out.println(this.description);
	  }}catch(Exception e)
	  {
		  System.out.println("Invalid arguments");
		  System.out.println(this.description);
	  }
  }
}
