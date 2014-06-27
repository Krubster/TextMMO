package ru.alastar.main.executors;

import ru.alastar.enums.EntityType;
import ru.alastar.main.net.Client;
import ru.alastar.main.net.requests.RegisterRequest;

public class RegisterCommandExecutor extends CommandExecutor {
	public RegisterCommandExecutor()
	{
		super();
		this.numOfArgs = 5;
		this.description = "Registers on the server Usage: server register {login} {password} {e-mail} {character name} {character type}, without brackets \n Alllowed types: \n  - Human\n  - Orc\n  - Elf\n  - Skeleton";
	}
	  @Override
	  public void execute(String[] args)
	  {
		  try{
		  if(numOfArgs == args.length){
		  RegisterRequest r = new RegisterRequest();
		  r.login = args[0];
		  r.pass = args[1];
		  r.mail = args[2];
		  r.name = args[3];
		  if(EntityType.valueOf(args[4]) != null){
		  r.type = EntityType.valueOf(args[4]);
		  Client.Send(r);
		  }
		  else {
			  System.out.println("Invalid arguments!");
			  System.out.println(this.description);
		  }
		  }else {
			  System.out.println("Invalid arguments!");
			  System.out.println(this.description);
		  }}catch(Exception e)
		  {
			  System.out.println("Invalid arguments!");
			  System.out.println(this.description);
		  }
	  }
}
