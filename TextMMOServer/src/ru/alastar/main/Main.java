package ru.alastar.main;

import ru.alastar.main.net.Server;

public class Main {

	public static void main(String[] args) {
		try {
			Server.startServer();
		} catch (Exception e) {
			Log("[SERVER]", e.getMessage());
		}
	}

	public static void Log(String prefix, String msg) {
		System.out.println(prefix + ":" + msg);
	}
}
