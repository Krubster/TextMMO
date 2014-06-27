package ru.alastar.main.executors;

import ru.alastar.enums.ActionType;
import ru.alastar.main.net.Client;
import ru.alastar.main.net.requests.ActionRequest;

public class CutExecutor extends CommandExecutor {
	public CutExecutor()
	{
		super();
		this.numOfArgs = 0;
		this.description = "Tries to cut wood in your location";
	}
  @Override
  public void execute(String[] args)
  {
	  try{
			 ActionRequest r = new ActionRequest();
			 r.action = ActionType.Cut;
			 Client.Send(r);
	  }catch(Exception e)
	  {
		  System.out.println(this.description);
	  }
  }
}
