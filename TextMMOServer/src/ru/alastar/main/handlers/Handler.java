package ru.alastar.main.handlers;

import ru.alastar.main.net.ConnectedClient;

public abstract class Handler
{
    public int    numOfArgs   = 0;
    public String description = "Standard command handler, handles nothing";

    public abstract void execute(String[] args, ConnectedClient c);

}
