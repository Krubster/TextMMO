package ru.alastar.main.net;

import ru.alastar.game.Entity;

import com.esotericsoftware.kryonet.Connection;

public class ConnectedClient {

	public String login;
	public String pass;
	public String mail;
	public long lastPacket;

	public Connection connection;

	public Entity controlledEntity;

	public ConnectedClient(Connection c) {
		this.connection = c;
		lastPacket = System.currentTimeMillis();
	}

}
