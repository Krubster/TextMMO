package ru.alastar.game;

import java.util.ArrayList;

public interface IContainer
{
    public void AddItem(Item i);

    public void RemoveItem(Item i);

    public void RemoveItem(int i);

    public Item getItem(int i);

    public boolean haveItem(int i);

    public ArrayList<Item> getItems();
}
