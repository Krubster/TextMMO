package ru.alastar.main.executors;

import ru.alastar.main.net.Client;
import ru.alastar.main.net.requests.AttackRequest;

public class CastExecutor extends CommandExecutor {
	public CastExecutor()
	{
		super();
		this.numOfArgs = 1;
		this.description = "Tries to attack entity in your location";
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
