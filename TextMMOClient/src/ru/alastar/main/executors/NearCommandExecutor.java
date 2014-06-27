package ru.alastar.main.executors;

import ru.alastar.game.worldwide.Location;

public class NearCommandExecutor extends CommandExecutor {
	public NearCommandExecutor()
	{
		super();
		this.numOfArgs = 0;
		this.description = "Shows all near locations Usage: client near";
	}
  @Override
  public void execute(String[] args)
  {
	  try{
	  if(numOfArgs == args.length){
		System.out.println("You are looking around...");
	  for(int i: Location.locationsAround.keySet())
	  {
		  System.out.println("[You see " + Location.locationsAround.get(i) + " near your location(ID: " +i+ ")]");
	  }
	   for(String i: Location.flags.keySet())
	      {
	          System.out.println("[You see " +i + " in your location]");
	      }
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
