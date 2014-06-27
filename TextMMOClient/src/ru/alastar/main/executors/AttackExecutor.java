package ru.alastar.main.executors;

import ru.alastar.main.net.Client;
import ru.alastar.main.net.requests.AttackRequest;

public class AttackExecutor extends CommandExecutor {
	public AttackExecutor()
	{
		super();
		this.numOfArgs = 1;
		this.description = "Tries to attack target entity Usage: server attack {entity id}";
	    this.specificMode = ClientMode.Game;
	}
  @Override
  public void execute(String[] args)
  {
	  try{
		 AttackRequest r = new AttackRequest();
		 r.id = Integer.parseInt(args[0]);
		 Client.Send(r);
	  }catch(Exception e)
	  {
		  System.out.println(this.description);
	  }
  }
}
