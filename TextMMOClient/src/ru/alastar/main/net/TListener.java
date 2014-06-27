package ru.alastar.main.net;

import java.util.ArrayList;
import java.util.Hashtable;

import ru.alastar.enums.ActionType;
import ru.alastar.enums.EntityType;
import ru.alastar.game.Entity;
import ru.alastar.game.Item;
import ru.alastar.game.Skill;
import ru.alastar.game.Statistic;
import ru.alastar.game.worldwide.Location;
import ru.alastar.main.Main;
import ru.alastar.main.net.requests.ActionRequest;
import ru.alastar.main.net.requests.AttackRequest;
import ru.alastar.main.net.requests.CastRequest;
import ru.alastar.main.net.requests.ChatSendRequest;
import ru.alastar.main.net.requests.LoginRequest;
import ru.alastar.main.net.requests.MoveRequest;
import ru.alastar.main.net.requests.RegisterRequest;
import ru.alastar.main.net.responses.AddEntityResponse;
import ru.alastar.main.net.responses.AddNearLocationResponse;
import ru.alastar.main.net.responses.AddSkillResponse;
import ru.alastar.main.net.responses.AddStatResponse;
import ru.alastar.main.net.responses.ChatSendResponse;
import ru.alastar.main.net.responses.InventoryResponse;
import ru.alastar.main.net.responses.LocationInfoResponse;
import ru.alastar.main.net.responses.LoginResponse;
import ru.alastar.main.net.responses.MessageResponse;
import ru.alastar.main.net.responses.RegisterResponse;
import ru.alastar.main.net.responses.RemoveEntityResponse;
import ru.alastar.main.net.responses.RemoveFromInventoryResponse;
import ru.alastar.main.net.responses.SetData;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;

public class TListener extends Listener {
	
   public static Kryo kryo;
   public static Connection connection;
   
   public TListener(EndPoint e)
   {
	   kryo = e.getKryo();

		//–егистраци€ пакетов. *ƒолжна выполн€тьс€ в такой же последовательности, что и на сервере!* 
       kryo.setRegistrationRequired(true);
       kryo.setAsmEnabled(true);
       
	   kryo.register(EntityType.class);
	   kryo.register(Hashtable.class);
	   kryo.register(ArrayList.class);
	   kryo.register(Entity.class);
	   kryo.register(String.class);
	   kryo.register(ActionType.class);

	   kryo.register(LoginRequest.class);
	   kryo.register(LoginResponse.class);
	   kryo.register(AddEntityResponse.class);
	   kryo.register(LocationInfoResponse.class);
	   kryo.register(AddNearLocationResponse.class);
	   kryo.register(SetData.class);
	   kryo.register(ChatSendRequest.class);
	   kryo.register(ChatSendResponse.class);
	   kryo.register(RemoveEntityResponse.class);
	   kryo.register(RegisterRequest.class);
	   kryo.register(RegisterResponse.class);
	   kryo.register(AddStatResponse.class);
	   kryo.register(AddSkillResponse.class);
	   kryo.register(MoveRequest.class);
	   kryo.register(ActionRequest.class);
	   kryo.register(CastRequest.class);
	   kryo.register(AttackRequest.class);
	   kryo.register(InventoryResponse.class);
	   kryo.register(MessageResponse.class);
	   kryo.register(RemoveFromInventoryResponse.class);

	 //  Main.Log("[LISTENER]", "All packets registered!");
   }
   
   public void received(Connection connection, Object object) {
    if(object instanceof LoginResponse)
    {
    	if(((LoginResponse)object).succesful)
    	Client.LoginSuccesful();
    	else
        Client.LoginUnsuccesful();
	
    }
    else if(object instanceof AddEntityResponse)
    {
    	AddEntityResponse r = (AddEntityResponse)object;
    	Entity e = new Entity(r.id, r.caption, r.type);
    	Location.AddEntity(e);
    	if(e.id == Client.id)
    	{
    		Client.controlledEntity = e;
    	}else{
    	Main.WarnForEntity(e);
    	}
    }
    else if(object instanceof LocationInfoResponse)
    {
    	LocationInfoResponse r = (LocationInfoResponse)object;
    	new Location(r.id, r.name);
    	Main.RefreshLocation();

    }
    else if(object instanceof AddNearLocationResponse)
    {
    	AddNearLocationResponse r = (AddNearLocationResponse)object;
    	Location.locationsAround.put(r.id, r.name);
    	Main.WarnForLocation(r);

    }
    else if(object instanceof SetData)
    {
    	SetData r = (SetData)object;
    	Client.id = r.id;
    }
    else if(object instanceof ChatSendResponse)
    {
    	ChatSendResponse r = (ChatSendResponse)object;
    	Main.LogChat(r.msg, r.sender);
    }
    else if(object instanceof RemoveEntityResponse)
    {
    	RemoveEntityResponse r = (RemoveEntityResponse)object;
    	Location.TryRemoveEntity(r.id);
    }
    else if(object instanceof RegisterResponse)
    {
    	RegisterResponse r = (RegisterResponse)object;
    	if(r.successful)
        Main.Say("Registration succesful!");
    	else
        Main.Say("Registration unsuccesful :(");
	
    }
    else if(object instanceof AddStatResponse)
    {
    	AddStatResponse r = (AddStatResponse)object;
    	if(Client.controlledEntity.stats.containsKey(r.name))
          Client.controlledEntity.stats.remove(r.name);

        Client.controlledEntity.stats.put(r.name, new Statistic(r.name, r.sValue, r.mValue));

    }
    else if(object instanceof AddSkillResponse)
    {
    	AddSkillResponse r = (AddSkillResponse)object;
    	if(Client.controlledEntity.skills.containsKey(r.name))
            Client.controlledEntity.skills.remove(r.name);

          Client.controlledEntity.skills.put(r.name, new Skill(r.name, r.sValue, r.mValue));
	
    }
    else if(object instanceof InventoryResponse)
    {
    	InventoryResponse r = (InventoryResponse)object;
    	if(!Client.inventory.containsKey(r.id))
    	Client.inventory.put(r.id, new Item(r.id, r.captiion, r.amount));
    	else{
        	Client.inventory.remove(r.id);
        	Client.inventory.put(r.id, new Item(r.id, r.captiion, r.amount));
        }

    }
    else if(object instanceof MessageResponse)
    {
    	MessageResponse r = (MessageResponse)object;
       	Main.Say(r.msg);
    }
    else if(object instanceof RemoveFromInventoryResponse)
    {
    	RemoveFromInventoryResponse r = (RemoveFromInventoryResponse)object;
    	Client.inventory.remove(r.id);
    }
   }
   

	@Override
	public void connected(Connection connection) {
     TListener.connection = connection;
     Main.DoLogin();
	}

	@Override
	public void disconnected(Connection connection) {
		connection.close();
		Main.Log("[NETWORK]", "Server closed connection...");
	}
}
