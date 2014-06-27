package ru.alastar.main.executors;

import ru.alastar.main.net.Client;
import ru.alastar.main.net.requests.LoginRequest;

public class LoginCommandExecutor extends CommandExecutor{
	public LoginCommandExecutor()
	{
		super();
		this.numOfArgs = 2;
		this.description = "Login to the server Usage: server login {login} {password}, without brackets";
	}
  @Override
  public void execute(String[] args)
  {
	  if(numOfArgs == args.length){
	  LoginRequest r = new LoginRequest();
	  r.login = args[0];
	  r.pass = args[1];
	  Client.Send(r);
	  }
	  else
	  {
		  System.out.println(this.description);
	  }
  }
}
