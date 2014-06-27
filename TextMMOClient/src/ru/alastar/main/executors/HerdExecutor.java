package ru.alastar.main.executors;

import ru.alastar.enums.ActionType;
import ru.alastar.main.net.Client;
import ru.alastar.main.net.requests.ActionRequest;

public class HerdExecutor extends CommandExecutor {
	public HerdExecutor()
	{
		super();
		this.numOfArgs = 0;
		this.description = "Tries to gather herds in your location";
	      this.specificMode = ClientMode.Game;

	}
  @Override
  public void execute(String[] args)
  {
	  try{
			 ActionRequest r = new ActionRequest();
			 r.action = ActionType.Herd;
			 Client.Send(r);
	  }catch(Exception e)
	  {
		  System.out.println(this.description);
	  }
  }
}
