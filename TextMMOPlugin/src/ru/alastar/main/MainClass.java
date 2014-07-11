package ru.alastar.main;

public class MainClass extends Plugin
{

    @Override
    public void OnLoad()
    {
        Main.Log("[Plugin]","Plugin enabled!");
    }

    @Override
    public void OnDisable()
    {
        Main.Log("[Plugin]","Plugin disabled!");        
    }

}
