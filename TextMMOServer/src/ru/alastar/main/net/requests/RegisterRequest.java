package ru.alastar.main.net.requests;

import ru.alastar.enums.EntityType;

public class RegisterRequest {
	public String name;
	public String login;
	public String pass, mail;
	public EntityType type;
}
