package ru.alastar.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

import ru.alastar.game.Entity;
import ru.alastar.game.worldwide.Location;
import ru.alastar.main.executors.AroundCommandExecutor;
import ru.alastar.main.executors.AttackExecutor;
import ru.alastar.main.executors.CastExecutor;
import ru.alastar.main.executors.ChatSendCommandExecutor;
import ru.alastar.main.executors.CommandExecutor;
import ru.alastar.main.executors.CommandsCommandExecutor;
import ru.alastar.main.executors.CutExecutor;
import ru.alastar.main.executors.GetIdExecutor;
import ru.alastar.main.executors.GrowExecutor;
import ru.alastar.main.executors.HerdExecutor;
import ru.alastar.main.executors.InvExecutor;
import ru.alastar.main.executors.LoginCommandExecutor;
import ru.alastar.main.executors.MineExecutor;
import ru.alastar.main.executors.MoveCommandExecutor;
import ru.alastar.main.executors.NearCommandExecutor;
import ru.alastar.main.executors.RegisterCommandExecutor;
import ru.alastar.main.executors.SkillsCommandExecutor;
import ru.alastar.main.executors.StatsCommandExecutor;
import ru.alastar.main.net.Client;
import ru.alastar.main.net.responses.AddNearLocationResponse;

public class Main {

	public static Hashtable<String, CommandExecutor> serverCommands = new Hashtable<String, CommandExecutor>();
	public static Hashtable<String, CommandExecutor> clientCommands = new Hashtable<String, CommandExecutor>();

	public static BufferedReader in = new BufferedReader(new InputStreamReader(
			System.in));

	public static void main(String[] args) {
		try {
			RegisterCommands();
			Client.startClient();
			ListenToCommands();
		} catch (Exception e) {
			Log("[ERROR]", e.getLocalizedMessage());
		}
	}

	private static void ListenToCommands() {
	String line;
	while (true) {
				try {
					line = in.readLine();
					if (line != null) {
						Main.ProcessCommand(line);
					}
				} catch (IOException e) {
					Log("[ERROR]", e.getLocalizedMessage());
				}
			}
	}

	private static void RegisterCommands() {
		serverCommands.put("login", new LoginCommandExecutor());
		serverCommands.put("register", new RegisterCommandExecutor());
		serverCommands.put("say", new ChatSendCommandExecutor());
		serverCommands.put("move", new MoveCommandExecutor());
		serverCommands.put("mine", new MineExecutor());
		serverCommands.put("cut", new CutExecutor());
		serverCommands.put("herd", new HerdExecutor());
		serverCommands.put("grow", new GrowExecutor());
		serverCommands.put("cast", new CastExecutor());
		serverCommands.put("attack", new AttackExecutor());

		clientCommands.put("id", new GetIdExecutor());
		clientCommands.put("around", new AroundCommandExecutor());
		clientCommands.put("near", new NearCommandExecutor());
		clientCommands.put("stats", new StatsCommandExecutor());
		clientCommands.put("skills", new SkillsCommandExecutor());
		clientCommands.put("commands", new CommandsCommandExecutor());
		clientCommands.put("inv", new InvExecutor());

	}

	public static void ProcessCommand(String line) {
		if (line.split(" ")[0].toLowerCase().equals("client")) {
			if (clientCommands.containsKey(line.split(" ")[1].toLowerCase())) {
				String[] args = new String[line.split(" ").length - 2];
				for (int i = 0; i < line.split(" ").length - 2; ++i) {
					args[i] = line.split(" ")[i + 2];
				}
				clientCommands.get(line.split(" ")[1].toLowerCase()).execute(
						args);
			} else
				System.out.println("Invalid command to client");
		} else if (line.split(" ")[0].toLowerCase().equals("server")) {
			if (serverCommands.containsKey(line.split(" ")[1].toLowerCase())) {
				String[] args = new String[line.split(" ").length - 2];
				for (int i = 0; i < line.split(" ").length - 2; ++i) {
					args[i] = line.split(" ")[i + 2];
				}
				serverCommands.get(line.split(" ")[1].toLowerCase()).execute(
						args);
			} else
				System.out.println("Invalid command to server");
		} else {
			System.out.println("Invalid command");
		}
	}

	public static void DoLogin() {
	System.out.println("----------^^GAME STARTED^^----------");
	System.out.println("Login with command 'server login {login} {password}' please");
	System.out.println("or use 'client commands' for command information");
	}
		/*
	}
		try {
			Main.blocked = true;
			System.out.println("Enter your login:");

			BufferedReader in = new BufferedReader(new InputStreamReader(
					System.in));

			for (;;) {
				String line = in.readLine();
				if (line == null) {
					continue;
				} else {
					Client.login = line;
					break;
				}
			}

			System.out.println("Enter your password:");

			for (;;) {
				String line = in.readLine();
				if (line == null) {
					continue;
				} else {
					Client.password = line;
					break;
				}
			}
			Client.DoLogin();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	public static void DoGame() {
		System.out.println("Entering the world...");
	}

	public static void Log(String prefix, String msg) {
		System.out.println(prefix + ":" + msg);
	}

	public static void RefreshLocation() {
		System.out.println("You have been moved to " + Location.name);
	}

	public static void WarnForEntity(Entity e) {
		System.out.println("You see " + e.caption + " the " + e.type.name());

	}

	public static void WarnForLocation(AddNearLocationResponse r) {
		System.out.println("You see the " + r.name + " near your location");
	}

	public static void LogChat(String msg, String sender) {
		System.out.println("--]"+sender + " says: " + msg);
	}

	public static void WarnLeave(Entity entity) {
		System.out.println(entity.caption + " leaves your spot");
	}

	public static void Say(String string) {
		System.out.println(string);
	}
}
