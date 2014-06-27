package ru.alastar.main.net;

import java.util.Date;

import ru.alastar.game.Entity;

import com.esotericsoftware.kryonet.Connection;

public class ConnectedClient {

	public String login;
	public String pass;
	public String mail;
	
	public boolean logged = false;
	
	public Date lastPacket;

	public Connection connection;

	public Entity controlledEntity;

	public ConnectedClient(Connection c) {
		this.connection = c;
		lastPacket = new Date();
	}

}
