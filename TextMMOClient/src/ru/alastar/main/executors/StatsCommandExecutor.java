package ru.alastar.main.executors;

import ru.alastar.game.Statistic;
import ru.alastar.main.net.Client;


public class StatsCommandExecutor extends CommandExecutor {
	public StatsCommandExecutor()
	{
		super();
		this.numOfArgs = 0;
		this.description = "Shows all of your current stats Usage: client stats";
	      this.specificMode = ClientMode.Game;

	}
  @Override
  public void execute(String[] args)
  {
	  if(numOfArgs == args.length){
		  Statistic stat;
			System.out.println("---* STATS *---");
			System.out.println(" *---");

	    for(String s: Client.stats.keySet())
	    {
	    	stat = Client.stats.get(s);
	    	System.out.println(" "+s + " - " + stat.value + "/" + stat.maxValue);
			System.out.println(" *---");

	    }
	  }
	  else
	  {
		  System.out.println(this.description);
	  }
  }
}
