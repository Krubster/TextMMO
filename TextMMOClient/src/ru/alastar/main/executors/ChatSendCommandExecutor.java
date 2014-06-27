package ru.alastar.main.executors;

import ru.alastar.main.net.Client;
import ru.alastar.main.net.requests.ChatSendRequest;

public class ChatSendCommandExecutor extends CommandExecutor {
	
	public ChatSendCommandExecutor()
	{
		super();
		this.description = "Sends message in chat Usage: server say {message}, without brackets";
	      this.specificMode = ClientMode.Game;

	}
  @Override
  public void execute(String[] args)
  {
	  ChatSendRequest r = new ChatSendRequest();
	  r.msg = "";
	  for(String part: args){
	  r.msg += part;
	  }
	  Client.Send(r);
  }
}
