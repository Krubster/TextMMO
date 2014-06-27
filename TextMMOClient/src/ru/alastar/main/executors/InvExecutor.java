package ru.alastar.main.executors;

import ru.alastar.game.Item;
import ru.alastar.main.Main;
import ru.alastar.main.net.Client;

public class InvExecutor extends CommandExecutor {
	public InvExecutor()
	{
		super();
		this.numOfArgs = 0;
		this.description = "Shows your inventory";
	}
  @Override
  public void execute(String[] args)
  {
	  try{
	  Main.Say("-----["+ Client.controlledEntity.caption+"'s inventory]-----");
	  for(Item i: Client.inventory.values())
	  {
		 Main.Say("--["+i.caption+", " +i.amount+ "]--");	  
	  }
	  Main.Say("------[0]------");
	  }catch(Exception e)
	  {
		  System.out.println(this.description);
	  }
  }
}
