package ru.alastar.main.net;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import ru.alastar.enums.ActionType;
import ru.alastar.enums.EntityType;
import ru.alastar.game.Entity;
import ru.alastar.main.Main;
import ru.alastar.main.net.requests.ActionRequest;
import ru.alastar.main.net.requests.AttackRequest;
import ru.alastar.main.net.requests.CastRequest;
import ru.alastar.main.net.requests.ChatSendRequest;
import ru.alastar.main.net.requests.LoginRequest;
import ru.alastar.main.net.requests.MoveRequest;
import ru.alastar.main.net.requests.RegisterRequest;
import ru.alastar.main.net.responses.AddEntityResponse;
import ru.alastar.main.net.responses.AddFlagResponse;
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
	public static float packetDelay = 100F;

	public TListener(EndPoint e) {
		kryo = e.getKryo();

		// –егистраци€ пакетов. *ƒолжна выполн€тьс€ в такой же
		// последовательности, что и на сервере!*
		kryo.setRegistrationRequired(true);
		kryo.setAsmEnabled(true);

		kryo.register(EntityType.class);
		kryo.register(Hashtable.class);
		kryo.register(ArrayList.class);
		kryo.register(Entity.class);
		kryo.register(String.class);
		kryo.register(ActionType.class);
	    kryo.register(Integer.class);

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
	    kryo.register(AddFlagResponse.class);

		// Main.Log("[LISTENER]", "All packets registered!");
	}

	public void received(Connection connection, Object object) {
		try {
			ConnectedClient c = Server.getClient(connection);
			if (c != null) {
				//Main.Log("[INFO]", "CTM - " + System.currentTimeMillis() + " lastPacket - " + c.lastPacket);
				if ((new Date().getTime() - c.lastPacket.getTime()) > packetDelay) {
					if (object instanceof LoginRequest) {
						Server.Login((LoginRequest) object, connection);
					} else if (object instanceof ChatSendRequest) {
						Server.ProcessChat(((ChatSendRequest) object).msg,
								connection);
					} else if (object instanceof RegisterRequest) {
						Server.ProcessRegister(((RegisterRequest) object),
								connection);
					} else if (object instanceof MoveRequest) {
						Server.HandleMove(((MoveRequest) object), connection);
					} else if (object instanceof ActionRequest) {
						Server.HandleAction(((ActionRequest) object),
								connection);
						//Main.Log("[ACTION]", "Handling action: " + ((ActionRequest) object).action.name());
					} else if (object instanceof CastRequest) {
						Server.HandleCast(((CastRequest) object), connection);
					} else if (object instanceof AttackRequest) {
						Server.HandleAttack(((AttackRequest) object),
								connection);
					}
				}
				c.lastPacket = new Date();
			}
			else
				Main.Log("[ERROR]", "Connected client is null");
		} catch (Exception e) {
			Main.Log("[SERVER]", e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void connected(Connection connection) {
		if (!Server.hasClient(connection)) {
			Server.addClient(connection);
		}
	}

	@Override
	public void disconnected(Connection connection) {
		connection.close();
		Server.removeClient(connection);
	}
}
