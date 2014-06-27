package ru.alastar.game;

import java.util.ArrayList;

import ru.alastar.main.net.Server;
import ru.alastar.main.net.responses.InventoryResponse;
import ru.alastar.main.net.responses.RemoveFromInventoryResponse;

public class Inventory implements IContainer
{

    public int             entityId, maxItems;
    public ArrayList<Item> items;

    public Inventory(int i, int m)
    {
        this.entityId = i;
        this.maxItems = m;
        this.items = new ArrayList<Item>();
    }

    public Inventory(int i, int m, ArrayList<Item> its)
    {
        this.entityId = i;
        this.maxItems = m;
        this.items = its;
    }

    @Override
    public void AddItem(Item i)
    {
        if (!items.contains(i))
        {
            Item st = getSameTypeItem(i.caption);
            if (st == null)
            {
                if (items.size() + 1 <= maxItems)
                {
                    items.add(i);
                    st = i;
                } 
                else
                {
                    Server.warnEntity(Server.getEntity(entityId),
                            "Your backpack is full!");
                }
            }
            else
            {
              //  Main.Log("[INVENTORY]", "Items stack exists! Adding items to it. Stack size: " + st.amount + ". Adding " + i.amount);
                st.amount += i.amount;
               // Main.Log("[INVENTORY]", "Added! Now its " + st.amount);
            }
            InventoryResponse r = new InventoryResponse();
            r.amount = st.amount;
            r.captiion = st.caption;
            r.id = st.id;
            Server.SendTo(
                    Server.getClientByEntity(Server.getEntity(entityId)).connection,
                    r);
        }
    }

    @Override
    public void RemoveItem(Item i)
    {
        if (items.contains(i))
            items.remove(i);

        RemoveFromInventoryResponse r = new RemoveFromInventoryResponse();
        r.id = i.id;
        Server.SendTo(
                Server.getClientByEntity(Server.getEntity(entityId)).connection,
                r);
    }

    @Override
    public void RemoveItem(int i)
    {
        for (Item it : items)
        {
            if (it.id == i)
            {
                items.remove(it);
                RemoveFromInventoryResponse r = new RemoveFromInventoryResponse();
                r.id = it.id;
                Server.SendTo(
                        Server.getClientByEntity(Server.getEntity(entityId)).connection,
                        r);
                break;
            }
        }
    }

    @Override
    public Item getItem(int i)
    {
        for (Item it : items)
        {
            if (it.id == i)
            {
                return it;
            }
        }
        return null;
    }

    public Item getSameTypeItem(String s)
    {
        for (Item it : items)
        {
            if (it.caption == s)
            {
                return it;
            }
        }
        return null;
    }

    @Override
    public boolean haveItem(int i)
    {
        for (Item it : items)
        {
            if (it.id == i)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public ArrayList<Item> getItems()
    {
        return items;
    }

}
