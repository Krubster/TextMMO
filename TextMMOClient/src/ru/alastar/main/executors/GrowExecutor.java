package ru.alastar.main.executors;

import ru.alastar.enums.ActionType;
import ru.alastar.main.net.Client;
import ru.alastar.main.net.requests.ActionRequest;

public class GrowExecutor extends CommandExecutor {
	public GrowExecutor()
	{
		super();
		this.numOfArgs = 0;
		this.description = "Tries to grow plants in your location";
	}
  @Override
  public void execute(String[] args)
  {
	  try{
			 ActionRequest r = new ActionRequest();
			 r.action = ActionType.Grow;
			 Client.Send(r);
	  }catch(Exception e)
	  {
		  System.out.println(this.description);
	  }
  }
}
