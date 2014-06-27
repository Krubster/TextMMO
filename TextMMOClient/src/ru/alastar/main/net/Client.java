package ru.alastar.main.net;

import java.io.IOException;
import java.util.Hashtable;

import ru.alastar.game.Entity;
import ru.alastar.game.Item;
import ru.alastar.main.Main;
import ru.alastar.main.executors.ClientMode;
import ru.alastar.main.net.requests.LoginRequest;

public class Client {
	
	public static Entity controlledEntity;
    public static com.esotericsoftware.kryonet.Client client;
    public static String host = "127.0.0.1";
    public static int port = 25565;
	public static String login;
	public static String password;
	public static int id = -1;
	public static Hashtable<Integer, Item> inventory = new Hashtable<Integer, Item>();
    
	public static void startClient() {
		client = new com.esotericsoftware.kryonet.Client();
		client.start();
		Main.Log("[CLIENT]", "Client created!");
		client.addListener(new TListener(client));
		Client.Connect();
	}
	public static void Connect() {
		try {
			Main.Log("[CLIENT]", "Client connecting to: " + host + ":" + port + "...");
			client.connect(100, host, port, port + 1);
			Main.Log("[CLIENT]", "Client connected to " + host + ":" + port);
		} catch (IOException e) {
			Main.Log("[ERROR]", e.getLocalizedMessage());
		}
	}
	public static void DoLogin() {
		System.out.println("Logging in...");
		LoginRequest r = new LoginRequest();
		r.login = login;
		r.pass = password;
		Send(r);
	}
	public static void Send(Object o)
	{
		 client.sendUDP(o);
	}
	public static void LoginSuccesful() {
		//Main.Log("[CLIENT]", "Success!");
        Main.currentMode = ClientMode.Game;
		Main.DoGame();
	}
	public static void LoginUnsuccesful() {	
		Main.Log("[CLIENT]", "Login failed :(");
		Main.DoLogin();
		login = "";
		password = "";
	}
}
