package ru.alastar.main.net;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.esotericsoftware.kryonet.Connection;

import ru.alastar.database.DatabaseClient;
import ru.alastar.enums.ActionType;
import ru.alastar.enums.EntityType;
import ru.alastar.enums.EquipType;
import ru.alastar.game.Attributes;
import ru.alastar.game.CraftInfo;
import ru.alastar.game.Entity;
import ru.alastar.game.Inventory;
import ru.alastar.game.Item;
import ru.alastar.game.PlantsType;
import ru.alastar.game.Skill;
import ru.alastar.game.Skills;
import ru.alastar.game.Statistic;
import ru.alastar.game.Stats;
import ru.alastar.game.security.Crypt;
import ru.alastar.game.spells.Heal;
import ru.alastar.game.systems.CraftSystem;
import ru.alastar.game.systems.GardenSystem;
import ru.alastar.game.systems.MagicSystem;
import ru.alastar.game.worldwide.Location;
import ru.alastar.game.worldwide.LocationFlag;
import ru.alastar.game.worldwide.World;
import ru.alastar.main.Main;
import ru.alastar.main.Plugin;
import ru.alastar.main.PluginInfo;
import ru.alastar.main.handlers.*;
import ru.alastar.main.net.requests.CommandRequest;
import ru.alastar.main.net.responses.AddFlagResponse;
import ru.alastar.main.net.responses.AddNearLocationResponse;
import ru.alastar.main.net.responses.AddSkillResponse;
import ru.alastar.main.net.responses.AddStatResponse;
import ru.alastar.main.net.responses.InventoryResponse;
import ru.alastar.main.net.responses.LocationInfoResponse;
import ru.alastar.main.net.responses.LoginResponse;
import ru.alastar.main.net.responses.MessageResponse;
import ru.alastar.main.net.responses.RegisterResponse;
import ru.alastar.main.net.responses.SetData;

public class Server
{

    public static com.esotericsoftware.kryonet.Server           server;
    public static int                                           port = 25565;
    public static Hashtable<InetSocketAddress, ConnectedClient> clients;
    public static Hashtable<String, World>                      worlds;
    public static Hashtable<Integer, Inventory>                 inventories;
    public static Hashtable<Integer, Entity>                    entities;
    public static Hashtable<String, Handler>                    commands;
    public static Hashtable<String, Float>                      plantsGrowTime;
    public static Hashtable<String, Plugin>                     plugins;
    public static File[][]                                      preparedPlugins;

    public static Random                                        random;

    public static void startServer()
    {
        try
        {
            server = new com.esotericsoftware.kryonet.Server();
            server.start();
            server.bind(port, port + 1);
            server.addListener(new TListener(server));
            Init();
        } catch (IOException e)
        {
            handleError(e);
        }
    }

    public static void Init()
    {
        try
        {
            random = new Random();
            clients = new Hashtable<InetSocketAddress, ConnectedClient>();
            worlds = new Hashtable<String, World>();
            inventories = new Hashtable<Integer, Inventory>();
            entities = new Hashtable<Integer, Entity>();
            commands = new Hashtable<String, Handler>();
            plantsGrowTime = new Hashtable<String, Float>();
            plugins = new Hashtable<String, Plugin>();
            preparedPlugins = new File[100][100];
            DatabaseClient.Start();
            LoadWorlds();
            LoadEntities();
            LoadInventories();
            LoadPlants();
            FillWoods();
            FillMiningItems();
            SetupSpells();
            FillCommands();
            FillPlants();
            FillCrafts();
            
            LoadPlugins(); //Always at least!
        } catch (InstantiationException e)
        {
            Main.Log("[ERROR]", e.getLocalizedMessage());
        } catch (IllegalAccessException e)
        {
            Main.Log("[ERROR]", e.getLocalizedMessage());
        } catch (ClassNotFoundException e)
        {
            Main.Log("[ERROR]", e.getLocalizedMessage());
        }
        try
        {
            ExecutorService service = Executors.newCachedThreadPool();
            service.submit(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

                        for (;;)
                        {
                            String line;

                            line = in.readLine();

                            if (line == null)
                            {
                                continue;
                            }

                            if ("save".equals(line.toLowerCase()))
                            {
                                Save();
                                continue;
                            }

                            if ("stop".equals(line.toLowerCase()))
                            {
                                Save();
                                server.close();
                                
                                Main.writer.close();
                                break;
                            }

                            if ("encrypt".equals(line.split(" ")[0]
                                    .toLowerCase()))
                            {
                                System.out.println(Crypt.encrypt(line
                                        .split(" ")[1]));
                                continue;
                            }

                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                private void Save()
                {
                    for(Plugin p : plugins.values())
                    {
                        p.OnDisable();
                    }
                    for (World w : worlds.values())
                    {
                        for (Location l : w.locations.values())
                        {
                            Server.SaveLocation(l);
                        }
                        GardenSystem.SaveAll();
                    }
                }
            });
        } catch (Exception e)
        {
            handleError(e);
        }
    }

    private static void LoadPlugins()
    {  
        Main.Log("[PLUGIN MANAGER]","Loading plugins...");
        File plugDir = new File("plugins");
        if(!plugDir.exists())
        {
            plugDir.mkdir();
        }
        else
        {   
 
           for(File plugin: plugDir.listFiles())
            {
               LoadPlugin(plugin);
            } 
           Main.Log("[PLUGIN MANAGER]","Plugins loaded! Count: " + plugins.size());
        } 
    }
    

    private static void LoadPlugin(File original)
    {    
        try
     {
           JarFile file = new JarFile(original);
           ZipEntry e = file.getEntry("plugin.txt");
           BufferedReader input;
           InputStream in;
           String pathToMainClass;
           PluginInfo pluginInfo;
           if(e != null)
           {
               in = file.getInputStream(e);
               input = new BufferedReader(new InputStreamReader(in));
               pluginInfo = BuildPluginInfo(input);
               pathToMainClass = pluginInfo.getPathToMainClass();
               
               URL url = original.toURI().toURL();
               URL[] urls = new URL[]{url};
               
               @SuppressWarnings("resource")
            final ClassLoader cl = new URLClassLoader(urls);

               @SuppressWarnings("unchecked")
               Class<Plugin> cls = (Class<Plugin>)cl.loadClass(pathToMainClass);
               final Plugin plug = cls.newInstance();
               Main.service.submit(new Runnable(){
                   
               @Override 
               public void run(){
               plug.OnLoad();
               }
               
               });
               plugins.put(pluginInfo.getName(), plug);
               cl.clearAssertionStatus();
               input.close();
               in.close();
           }
           else
           {
               Main.Log("[PLUGIN MANAGER]","jar file missing plugin.txt file! Skipping...");
           }
           file.close();  
           } catch (IOException | SecurityException | IllegalArgumentException | ClassNotFoundException | InstantiationException | IllegalAccessException e1)
           {
               e1.printStackTrace();
           }
    }

    private static PluginInfo BuildPluginInfo(BufferedReader input)
    { 
        try
    {
        String name = "", path ="";
        String[] authors = null, dep = null;
        String e;

        while((e = input.readLine()) != null)
        {
            if(e.split(":")[0].equals("name")){
                name = e.split(":")[1];
            }
            else if(e.split(":")[0].equals("path")){
                path = e.split(":")[1];
            }
            else if(e.split(":")[0].equals("authors")){
                if(e.split(":").length > 1){
                authors = new String[e.split(":")[1].split(",").length];
                for(int i =0; i <  authors.length; ++i)
                {
                    authors[i] = e.split(":")[1].split(",")[i];
                }
            }}
            else if(e.split(":")[0].equals("dependencies")){
                if(e.split(":").length > 1){
                dep = new String[e.split(":")[1].split(",").length];
                
                for(int i =0; i < dep.length; ++i)
                {
                    dep[i] = e.split(":")[1].split(",")[i];
                }}
            }

        }
        
        PluginInfo p = new PluginInfo(name, path, authors, dep); 
        return p;    
        } catch (IOException e1)
        {
            e1.printStackTrace();
            return null;
        }
    }

    private static void FillCrafts()
    {
        ArrayList<String> neededItems = new ArrayList<String>();
        Attributes attrs = new Attributes();

        neededItems.add("plain wood");
        neededItems.add("amber");
        attrs.addAttribute("Charges", 10);
        attrs.addAttribute("Durability", 100);

        CraftSystem.registerCraft("wooden_totem", new CraftInfo(neededItems,
                "Carpentry", 0, "Wooden Totem", EquipType.None,
                ActionType.Cast, attrs));
    }

    private static void FillPlants()
    {
        plantsGrowTime.put("wheat", (float) (24 * 60 * 60 * 1000));
    }

    private static void FillCommands()
    {
        registerCommand("login", new LoginHandler());
        registerCommand("register", new RegisterHandler());
        registerCommand("move", new MoveHandler());
        registerCommand("act", new ActionHandler());
        registerCommand("say", new ChatHandler());
        registerCommand("cast", new CastHandler());
        registerCommand("attack", new AttackHandler());
        registerCommand("help", new HelpHandler());
        registerCommand("craft", new CraftHandler());

    }

    public static void registerCommand(String key, Handler h)
    {
        try
        {
            commands.put(key, h);
        } catch (Exception e)
        {
            handleError(e);
        }
    }

    private static void SetupSpells()
    {
        MagicSystem.addSpell("heal", new Heal());
    }

    private static void FillWoods()
    {
        Location.woods.put(0, "plain wood");
        Location.woods.put(10, "oak wood");
        Location.woods.put(25, "yew wood");
        Location.woods.put(45, "ash wood");
        Location.woods.put(50, "greatwood");
    }

    private static void FillMiningItems()
    {
        Location.miningItems.put(0, "copper ore");
        Location.miningItems.put(10, "iron ore");
        Location.miningItems.put(25, "shadow metal ore");
        Location.miningItems.put(25, "amber");
        Location.miningItems.put(30, "old corrored golem core");
        Location.miningItems.put(35, "emerald");
        Location.miningItems.put(45, "gold ore");
        Location.miningItems.put(50, "valor ore");
        Location.miningItems.put(50, "diamond");
        Location.miningItems.put(50, "swiftstone");
    }

    private static void LoadPlants()
    {
        ResultSet plantsRs = DatabaseClient
                .commandExecute("SELECT * FROM plants");
        try
        {
            while (plantsRs.next())
            {
                GardenSystem.addGrowingPlant(new PlantsType(plantsRs
                        .getString("name"), plantsRs.getDate("growTime"),
                        getLocation(plantsRs.getInt("locationId"))));
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    public static void SavePlant(PlantsType p)
    {
        // Main.Log("[DEBUG]","save plant grow");

        ResultSet plantsRs = DatabaseClient
                .commandExecute("SELECT * FROM plants WHERE locationId="
                        + p.loc.id);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        String time = sdf.format(p.finish);
        try
        {
            if (plantsRs.next())
            {
                DatabaseClient.commandExecute("UPDATE plants SET name='"
                        + p.plantName + "', growTime='" + time
                        + "' WHERE locationId=" + p.loc.id);

            } else
            {
                DatabaseClient
                        .commandExecute("INSERT INTO plants(name, growTime, locationId) VALUES('"
                                + p.plantName
                                + "','"
                                + time
                                + "',"
                                + p.loc.id
                                + ");");
            }
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

    }

    private static void LoadInventories()
    {
        ResultSet inventoriesRs = DatabaseClient
                .commandExecute("SELECT * FROM inventories");
        Inventory i;
        try
        {
            while (inventoriesRs.next())
            {
                i = new Inventory(inventoriesRs.getInt("entityId"),
                        inventoriesRs.getInt("max"),
                        LoadItems(inventoriesRs.getInt("entityId")));
                inventories.put(i.entityId, i);
            }
        } catch (SQLException e)
        {
            handleError(e);
        }
    }

    private static ArrayList<Item> LoadItems(int i)
    {
        ArrayList<Item> items = new ArrayList<Item>();
        Attributes attrs;
        ResultSet itemsRS = DatabaseClient
                .commandExecute("SELECT * FROM items WHERE entityId=" + i);
        ResultSet attrsRS;
        try
        {
            while (itemsRS.next())
            {
                attrsRS = DatabaseClient
                        .commandExecute("SELECT * FROM attributes WHERE itemId="
                                + itemsRS.getInt("id"));
                // Loading attributes
                attrs = new Attributes();
                while (attrsRS.next())
                {
                    attrs.addAttribute(attrsRS.getString("name"),
                            attrsRS.getInt("value"));
                }

                items.add(new Item(itemsRS.getInt("id"), itemsRS
                        .getInt("entityId"), itemsRS.getString("caption"),
                        itemsRS.getInt("amount"), getLocation(itemsRS
                                .getInt("locationId")), EquipType
                                .valueOf(itemsRS.getString("type")), ActionType
                                .valueOf(itemsRS.getString("actionType")),
                        attrs));
            }
        } catch (SQLException e)
        {
            handleError(e);
        }

        return items;
    }

    private static void LoadEntities()
    {
        try
        {
            Hashtable<Integer, String> playersEntities = new Hashtable<Integer, String>();
            ResultSet accountsEntities = DatabaseClient
                    .commandExecute("SELECT * FROM accounts");
            ResultSet allEntities = DatabaseClient
                    .commandExecute("SELECT * FROM entities");
            Stats stats;
            Skills skills;
            ResultSet skillsRS;
            ResultSet statsRS;
            ArrayList<String> knownSpells;
            ResultSet spellsRS;
            Entity e;

            while (accountsEntities.next())
            {
                playersEntities.put(accountsEntities.getInt("entityId"),
                        accountsEntities.getString("login"));
            }

            while (allEntities.next())
            {
                if (!playersEntities.containsKey(allEntities.getInt("id")))
                {
                    stats = new Stats();

                    skills = new Skills();

                    knownSpells = new ArrayList<String>();

                    skillsRS = DatabaseClient
                            .commandExecute("SELECT * FROM skills WHERE entityId="
                                    + allEntities.getInt("id"));
                    statsRS = DatabaseClient
                            .commandExecute("SELECT * FROM stats WHERE entityId="
                                    + allEntities.getInt("id"));
                    spellsRS = DatabaseClient
                            .commandExecute("SELECT * FROM knownSpells WHERE entityId="
                                    + allEntities.getInt("id"));
                    while (skillsRS.next())
                    {
                        skills.put(
                                skillsRS.getString("name"),
                                new Skill(skillsRS.getString("name"), skillsRS
                                        .getInt("sValue"), skillsRS
                                        .getInt("mValue"), skillsRS
                                        .getFloat("hardness"), skillsRS
                                        .getString("primaryStat"), skillsRS
                                        .getString("secondaryStat")));
                    }

                    while (statsRS.next())
                    {
                        stats.put(
                                statsRS.getString("name"),
                                new Statistic(statsRS.getString("name"),
                                        statsRS.getInt("sValue"), statsRS
                                                .getInt("mValue"), statsRS
                                                .getFloat("hardness")));
                    }

                    while (spellsRS.next())
                    {
                        knownSpells.add(spellsRS.getString("spellName"));
                    }

                    e = new Entity(allEntities.getInt("id"),
                            allEntities.getString("caption"),
                            EntityType.valueOf(allEntities.getString("type")),
                            getLocation(allEntities.getInt("locationId")),
                            skills, stats, knownSpells);
                    e.loc.AddEntity(e);
                    entities.put(e.id, e);
                }
            }
        } catch (SQLException e)
        {
            handleError(e);
        }

    }

    protected static void SaveLocation(Location l)
    {
        // Location info
        DatabaseClient.commandExecute("UPDATE locations SET name='" + l.name
                + "' WHERE id=" + l.id);
        // Near Locations
        String s = "";
        for (int id : l.nearLocationsIDs)
        {
            s += id + ";";
        }
        DatabaseClient.commandExecute("UPDATE locations SET nearLocationsIDs='"
                + s + "' WHERE id=" + l.id);

        // Entities
        for (Entity e : l.entities.values())
        {
            saveEntity(e);
        }

        // Flags
        for (String s1 : l.flags.keySet())
        {
            saveFlag(l.id, s1, l.flags.get(s1));
        }
    }

    public static void saveFlag(int id, String s, LocationFlag e)
    {
        ResultSet plantsRs = DatabaseClient
                .commandExecute("SELECT * FROM locationflags WHERE locationId="
                        + id + " AND flag='" + s + "'");
        try
        {
            if (plantsRs.next())
            {
                DatabaseClient.commandExecute("UPDATE locationflags SET val='"
                        + e.value + "' WHERE locationId=" + id + " AND flag='"
                        + s + "'");

            } else
            {
                DatabaseClient
                        .commandExecute("INSERT INTO locationflags(locationId, flag, val) VALUES("
                                + id + ",'" + s + "','" + e.value + "')");
            }
        } catch (SQLException e1)
        {
            handleError(e1);
        }
    }

    private static void LoadWorlds()
    {
        try
        {
            ResultSet worlds = DatabaseClient
                    .commandExecute("SELECT * FROM worlds");
            ResultSet locations;
            ResultSet flags;

            World w;
            Hashtable<Integer, Location> locs;
            Hashtable<String, LocationFlag> lFlags;

            ArrayList<Integer> nlIDs;
            String[] locsIDsInStr;
            while (worlds.next())
            {
                locs = new Hashtable<Integer, Location>();

                locations = DatabaseClient
                        .commandExecute("SELECT * FROM locations WHERE worldId='"
                                + worlds.getString("name") + "'");
                Main.Log("[SERVER]",
                        "Loading world " + worlds.getString("name") + "...");
                while (locations.next())
                {
                    // Main.Log("[SERVER]", "Clearing nlIDs...");
                    nlIDs = new ArrayList<Integer>();
                    // Main.Log("[SERVER]",
                    // "Getting IDs string and splitting it...");
                    locsIDsInStr = locations.getString("nearLocationsIDs")
                            .split(";");
                    // Main.Log("[SERVER]", "Iterating this string...");
                    for (int i = 0; i < locsIDsInStr.length; ++i)
                    {
                        if (!locsIDsInStr[i].isEmpty())
                        {
                            // Main.Log("[SERVER]", "Adding locID "
                            // + locsIDsInStr[i]);
                            nlIDs.add(Integer.parseInt(locsIDsInStr[i]));
                        }
                    }
                    // Main.Log("[SERVER]", "Gettings flags...");
                    flags = DatabaseClient
                            .commandExecute("SELECT * FROM locationFlags WHERE locationId="
                                    + locations.getInt("id"));
                    // Main.Log("[SERVER]", "Iterating flags...");
                    lFlags = new Hashtable<String, LocationFlag>();
                    while (flags.next())
                    {
                        // Main.Log("[SERVER]",
                        // "Putting " + flags.getString("flag"));

                        lFlags.put(flags.getString("flag"), new LocationFlag(
                                flags.getString("val")));

                    }
                    // Main.Log("[SERVER]", "Putting location at hashtable...");
                    locs.put(
                            locations.getInt("id"),
                            new Location(locations.getInt("id"), locations
                                    .getString("name"), nlIDs, lFlags));

                }

                w = new World(worlds.getString("name"), locs);
                Server.worlds.put(w.name, w);
            }
            /*
             * Main.Log("[SERVER]", "Done! Now map is:"); for (World w1 :
             * Server.worlds.values()) { Main.Log("[WORLD]", w1.name); for
             * (Location l1 : w1.locations.values()) { Main.Log(" [LOCATION]",
             * l1.id + " - " + l1.name); for (String f : l1.flags.keySet()) {
             * Main.Log(" -[FLAG]", f); } for (int nl : l1.nearLocationsIDs) {
             * Main.Log("  [NEAR LOCATION]", nl + " "); } } }
             */
        } catch (SQLException e)
        {
            handleError(e);
        }
    }

    public static boolean hasClient(Connection connection)
    {
        if (clients.containsKey(connection.getRemoteAddressUDP()))
            return true;
        else
            return false;
    }

    public static void addClient(Connection connection)
    {
        clients.put(connection.getRemoteAddressUDP(), new ConnectedClient(
                connection));
    }

    public static void removeClient(Connection connection)
    {
        ConnectedClient c = getClient(connection);
        if (c.controlledEntity != null)
        {
            // Main.Log("[LOGIN]",
            // "Controlled entity is not null, saving it...");
            c.controlledEntity.RemoveYourself();
        }// else
         // Main.Log("[LOGIN]", "Controlled entity is null, skipping save");

        clients.remove(connection.getRemoteAddressUDP());
    }

    public static void Login(String login, String pass, ConnectedClient client)
    {
        try
        {
            // Main.Log("[SERVER]", "Process auth...");
            ResultSet l = DatabaseClient
                    .commandExecute("SELECT * FROM accounts WHERE login='"
                            + login + "' AND password='" + Crypt.encrypt(pass)
                            + "'");
            if (l.next())
            {
                // Main.Log("[SERVER]", "...auth succesful!");

                LoginResponse r = new LoginResponse();
                r.succesful = true;
                SendTo(client.connection, r);

                if (!client.logged)
                {
                    client.login = l.getString("login");
                    client.pass = l.getString("password");
                    client.mail = l.getString("mail");
                    client.logged = true;
                    LoadPlayer(l.getInt("entityId"), client);
                } else
                {
                    MessageResponse r1 = new MessageResponse();
                    r1.msg = "This account is already logged in!";
                    SendTo(client.connection, r1);
                }
            } else
            {
                // Main.Log("[SERVER]", "...auth unsuccesful(");

                LoginResponse r = new LoginResponse();
                r.succesful = false;
                SendTo(client.connection, r);
            }

        } catch (SQLException e)
        {
            handleError(e);
        }
    }

    private static void LoadPlayer(int i, ConnectedClient c)
    {
        try
        {
            ResultSet e = DatabaseClient
                    .commandExecute("SELECT * FROM entities WHERE id=" + i);

            if (e.next())
            {
                SetData sd = new SetData();
                // Main.Log("[SERVER]", "Creating entity...");
                Stats stats = new Stats();
                Skills skills = new Skills();
                ArrayList<String> knownSpells = new ArrayList<String>();

                ResultSet skillsRS = DatabaseClient
                        .commandExecute("SELECT * FROM skills WHERE entityId="
                                + e.getInt("id"));
                ResultSet statsRS = DatabaseClient
                        .commandExecute("SELECT * FROM stats WHERE entityId="
                                + e.getInt("id"));
                ResultSet spellsRS = DatabaseClient
                        .commandExecute("SELECT * FROM knownSpells WHERE entityId="
                                + e.getInt("id"));

                while (skillsRS.next())
                {
                    skills.put(
                            skillsRS.getString("name"),
                            new Skill(skillsRS.getString("name"), skillsRS
                                    .getInt("sValue"), skillsRS
                                    .getInt("mValue"), skillsRS
                                    .getFloat("hardness"), skillsRS
                                    .getString("primaryStat"), skillsRS
                                    .getString("secondaryStat")));
                }

                while (statsRS.next())
                {
                    stats.put(
                            statsRS.getString("name"),
                            new Statistic(statsRS.getString("name"), statsRS
                                    .getInt("sValue"),
                                    statsRS.getInt("mValue"), statsRS
                                            .getFloat("hardness")));
                }

                while (spellsRS.next())
                {
                    knownSpells.add(spellsRS.getString("spellName"));
                }

                Entity entity = new Entity(e.getInt("id"),
                        e.getString("caption"), EntityType.valueOf(e
                                .getString("type")),
                        getLocation(e.getInt("locationId")), skills, stats,
                        knownSpells);
                // Main.Log("[SERVER]",
                // "Assigning it as a controlled to the connected client...");
                c.controlledEntity = entity;
                sd.id = entity.id;
                // Main.Log("[SERVER]", "Sending set data packet...");
                SendTo(c.connection, sd);
                LocationInfoResponse lir = new LocationInfoResponse();
                lir.id = entity.loc.id;
                lir.name = entity.loc.name;
                // Main.Log("[SERVER]", "Sending load location packet...");
                SendTo(c.connection, lir);
                AddNearLocationResponse anlr;
                Location l;
                // Main.Log("[SERVER]", "Sending near locations("
                // + entity.loc.nearLocationsIDs.size() + ", "
                // + entity.loc.name + ")...");
                for (int i1 = 0; i1 < entity.loc.nearLocationsIDs.size(); ++i1)
                {
                    // Main.Log("[SERVER]", "Selected id is "
                    // + entity.loc.nearLocationsIDs.get(i1));
                    l = getLocation(entity.loc.nearLocationsIDs.get(i1));
                    anlr = new AddNearLocationResponse();
                    anlr.id = l.id;
                    anlr.name = l.name;
                    // Main.Log("[SERVER]", "Sending " + l.name + " info");
                    SendTo(c.connection, anlr);
                }
                entity.loc.AddEntity(entity);
                // Main.Log("[SERVER]", "Sending other entities to it...");
                entity.loc.SendEntitiesAround(entity);
                // Main.Log("[SERVER]", "Sending stats...");
                AddStatResponse r = new AddStatResponse();
                for (String s : entity.stats.vals.keySet())
                {
                    r.name = s;
                    r.sValue = entity.stats.get(s).value;
                    r.mValue = entity.stats.get(s).maxValue;
                    SendTo(c.connection, r);
                }
                // Main.Log("[SERVER]", "Sending skills...");
                AddSkillResponse sr = new AddSkillResponse();
                for (String s : entity.skills.vals.keySet())
                {
                    sr.name = s;
                    sr.sValue = entity.skills.get(s).value;
                    sr.mValue = entity.skills.get(s).maxValue;
                    SendTo(c.connection, sr);

                }
                // Main.Log("[SERVER]", "Sending inventory...");
                InventoryResponse ir = new InventoryResponse();
                for (Item i1 : inventories.get(entity.id).items)
                {
                    ir.id = i1.id;
                    ir.captiion = i1.caption;
                    ir.amount = i1.amount;
                    ir.attrs = i1.attributes.values;
                    SendTo(c.connection, ir);
                }

                AddFlagResponse af = new AddFlagResponse();
                for (String s : entity.loc.flags.keySet())
                {
                    af.flag = s;
                    af.val = entity.loc.flags.get(s).value;
                    SendTo(c.connection, af);
                }

                // Main.Log("[SERVER]", "Data was sent to player. Fuf...");
                entities.put(entity.id, entity);
            }
        } catch (SQLException e1)
        {
            handleError(e1);
        }
    }

    public static void warnEntity(Entity e, String m)
    {
        try
        {
            MessageResponse r = new MessageResponse();
            r.msg = m;
            SendTo(getClientByEntity(e).connection, r);
        } catch (Exception er)
        {
            handleError(er);
        }
    }

    public static Location getLocation(int int1)
    {
        // Main.Log("[SERVER]", "Requesting location id " + int1);
        for (World w : worlds.values())
        {
            if (w.locations.containsKey(int1))
            {
                // Main.Log("[SERVER]",
                // "Returning location " + w.locations.get(int1).name);
                return w.locations.get(int1);
            }
        }
        // Main.Log("[SERVER]", "Returning null location!");
        return null;
    }

    public static ConnectedClient getClient(Connection c)
    {
        return clients.get(c.getRemoteAddressUDP());
    }

    public static void SendTo(Connection c, Object o)
    {
        server.sendToUDP(c.getID(), o);
    }
    
    public static void SendTo(ConnectedClient c, Object o)
    {
        server.sendToUDP(c.connection.getID(), o);
    }
    
    public static ConnectedClient getClientByEntity(Entity e1)
    {
        for (ConnectedClient c : clients.values())
        {
            if (c.controlledEntity != null)
            {
                if (c.controlledEntity.id == e1.id)
                {
                    return c;
                }
            }
        }
        return null;
    }

    public static void ProcessChat(String msg, ConnectedClient c)
    {
        if (c.controlledEntity != null)
        {
            c.controlledEntity.loc.sendAll(msg, c.controlledEntity.caption);
        }
    }

    public static void saveEntity(Entity entity)
    {
        try
        {
            // Main.Log("[SAVE]", "Saving entity...");
            // Entity Main
            ResultSet entityEqRS = DatabaseClient
                    .commandExecute("SELECT * FROM entities WHERE id="
                            + entity.id);
            if (entityEqRS.next())
                DatabaseClient.commandExecute("UPDATE entities SET caption='"
                        + entity.caption + "', type='" + entity.type.name()
                        + "', locationId=" + entity.loc.id + " WHERE id="
                        + entity.id);
            else
                DatabaseClient
                        .commandExecute("INSERT INTO entities(id, caption, type, locationId, ai) VALUES("
                                + entity.id
                                + ",'"
                                + entity.caption
                                + "', '"
                                + entity.type.name()
                                + "',"
                                + entity.loc.id
                                + ", '')");
            // Main.Log("[SAVE]", "Saving stats...");

            // Stats
            ResultSet statEqRS;
            for (Statistic s : entity.stats.vals.values())
            {
                statEqRS = DatabaseClient
                        .commandExecute("SELECT * FROM stats WHERE entityId="
                                + entity.id + " AND name='" + s.name + "'");

                if (statEqRS.next())
                    DatabaseClient.commandExecute("UPDATE stats SET sValue="
                            + s.value + ", mValue=" + s.maxValue
                            + ", hardness=" + s.hardness + " WHERE entityId="
                            + entity.id + " AND name='" + s.name + "'");
                else
                    DatabaseClient
                            .commandExecute("INSERT INTO stats (sValue, mValue, hardness, entityId, name) VALUES("
                                    + s.value
                                    + ","
                                    + s.maxValue
                                    + ","
                                    + s.hardness
                                    + ","
                                    + entity.id
                                    + ",'"
                                    + s.name + "')");

            }
            // Main.Log("[SAVE]", "Saving skills...");

            // Skills
            ResultSet skillsEqRS;

            for (Skill s : entity.skills.vals.values())
            {
                skillsEqRS = DatabaseClient
                        .commandExecute("SELECT * FROM skills WHERE entityId="
                                + entity.id + " AND name='" + s.name + "'");
                if (skillsEqRS.next())
                    DatabaseClient.commandExecute("UPDATE skills SET sValue="
                            + s.value + ", mValue=" + s.maxValue
                            + ", hardness=" + s.hardness + ", primaryStat='"
                            + s.primaryStat + "', secondaryStat='"
                            + s.secondaryStat + "' WHERE entityId=" + entity.id
                            + " AND name='" + s.name + "'");
                else
                    DatabaseClient
                            .commandExecute("INSERT INTO skills (sValue, mValue, hardness, entityId, name, primaryStat, secondaryStat) VALUES("
                                    + s.value
                                    + ","
                                    + s.maxValue
                                    + ","
                                    + s.hardness
                                    + ","
                                    + entity.id
                                    + ",'"
                                    + s.name
                                    + "','"
                                    + s.primaryStat
                                    + "','"
                                    + s.secondaryStat + "')");
            }
            // Main.Log("[SAVE]", "Saving inventory...");

            // Inventory
            Inventory inv = inventories.get(entity.id);
            if (inv != null)
            {
                saveInventory(inv);
            }

        } catch (SQLException e)
        {
            handleError(e);
        }
    }

    public static void ProcessRegister(String login, String pass, String mail,
            String name, String race, ConnectedClient c)
    {
        try
        {
            ResultSet regRS = DatabaseClient
                    .commandExecute("SELECT * FROM accounts WHERE login='"
                            + login + "' AND mail='" + mail + "'");
            RegisterResponse r = new RegisterResponse();

            if (regRS.next())
            {
                r.successful = false;
                Server.SendTo(c, r);
            } else
            {
                CreateAccount(login, pass, mail, name, race,
                        c);
                r.successful = true;
                Server.SendTo(c, r);
            }
        } catch (SQLException e)
        {
            handleError(e);
        }
    }

    private static void CreateAccount(String login, String pass, String mail,
            String name, String race, ConnectedClient client)
    {
        try
        {
            Entity e = new Entity(getFreeId(), name, EntityType.valueOf(race),
                    Server.getRandomStartLocation(),
                    Server.getStandardSkillsSet(),
                    Server.getStandardStatsSet(), new ArrayList<String>());
            client.controlledEntity = e;
            entities.put(e.id, e);
            createInventory(e.id);
            saveInventory(inventories.get(e.id));
            saveEntity(e);
            DatabaseClient
                    .commandExecute("INSERT INTO accounts(login, password, mail, entityId) VALUES('"
                            + login
                            + "','"
                            + Crypt.encrypt(pass)
                            + "','"
                            + mail + "'," + e.id + ")");
        } catch (Exception e)
        {
            handleError(e);
        }
    }

    private static void createInventory(int id)
    {
        Inventory i = new Inventory(id, 20);
        i.AddItem(new Item(getFreeItemId(), id, "Coin", 1, getLocation(1),
                EquipType.None, ActionType.None, new Attributes()));
        inventories.put(id, i);
    }

    private static void saveInventory(Inventory i)
    {
        ResultSet entityEqRS = DatabaseClient
                .commandExecute("SELECT * FROM inventories WHERE entityId="
                        + i.entityId);
        try
        {
            if (entityEqRS.next())
            {
                DatabaseClient.commandExecute("UPDATE inventories SET max="
                        + i.maxItems + " WHERE entityId=" + i.entityId);

            } else
            {
                DatabaseClient
                        .commandExecute("INSERT INTO inventories(entityId, max) VALUES("
                                + i.entityId + "," + i.maxItems + ")");
            }

            for (Item item : i.items)
            {
                SaveItem(item);
            }
        } catch (SQLException e)
        {
            handleError(e);
        }
    }

    public static void SaveItem(Item item)
    {
        // Main.Log("[SAVE ITEM]", "Saving item " + item.caption + " entityId="
        // + item.entityId + " id=" + item.id + " amount" + item.amount);
        ResultSet itemEqRS = DatabaseClient
                .commandExecute("SELECT * FROM items WHERE entityId="
                        + item.entityId + " AND id=" + item.id);
        try
        {
            if (itemEqRS.next())
            {
                DatabaseClient.commandExecute("UPDATE items SET entityId="
                        + item.entityId + ", locationId=" + item.loc.id
                        + ", amount=" + item.amount + ", caption='"
                        + item.caption + "', type='" + item.eqType.name()
                        + "', actionType='" + item.aType.name() + "' WHERE id="
                        + item.id);

            } else
            {
                DatabaseClient
                        .commandExecute("INSERT INTO items(id, locationId, caption, amount, entityId, type, actionType) VALUES("
                                + item.id
                                + ","
                                + item.getLocId()
                                + ",'"
                                + item.caption
                                + "',"
                                + item.amount
                                + ", "
                                + item.entityId
                                + ",'"
                                + item.eqType.name()
                                + "','" + item.aType.name() + "')");
            }
            SaveAttributes(item);
        } catch (SQLException e)
        {
            handleError(e);
        }
    }

    private static void SaveAttributes(Item item)
    {
        ResultSet itemEqRS;
        try
        {
            for (String s : item.attributes.values.keySet())
            {
                itemEqRS = DatabaseClient
                        .commandExecute("SELECT * FROM attributes WHERE itemId="
                                + item.id);

                if (itemEqRS.next())
                {
                    DatabaseClient
                            .commandExecute("UPDATE attributes SET value="
                                    + item.getAttributeValue(s)
                                    + " WHERE name='" + s + "' AND itemId="
                                    + item.id);

                } else
                {
                    DatabaseClient
                            .commandExecute("INSERT INTO attributes(name, itemId, value) VALUES('"
                                    + s
                                    + "',"
                                    + item.id
                                    + ","
                                    + item.getAttributeValue(s) + ")");
                }
            }
        } catch (SQLException e)
        {
            handleError(e);
        }
    }

    private static Stats getStandardStatsSet()
    {
        Stats sts = new Stats();
        sts.put("Hits", new Statistic("Hits", 10, 50, 5));
        sts.put("Stringth", new Statistic("Strength", 5, 50, 5));
        sts.put("Dexterity", new Statistic("Dexterity", 5, 50, 5));
        sts.put("Int", new Statistic("Int", 5, 50, 5));
        sts.put("Mana", new Statistic("Mana", 20, 20, 5));
        return sts;
    }

    private static Skills getStandardSkillsSet()
    {
        Skills sks = new Skills();
        sks.put("Swords",
                new Skill("Swords", 0, 50, 5, "Strength", "Dexterity"));
        sks.put("Chivalry", new Skill("Chivalry", 0, 50, 5, "Strength", "Int"));
        sks.put("Magery", new Skill("Magery", 0, 50, 5, "Int", "Int"));
        sks.put("Lumberjacking", new Skill("Lumberjacking", 0, 50, 5,
                "Strength", "Dexterity"));
        sks.put("Mining", new Skill("Mining", 0, 50, 5, "Strength", "Int"));
        sks.put("Taming", new Skill("Taming", 0, 50, 5, "Int", "Strength"));
        sks.put("Necromancy", new Skill("Necromancy", 0, 50, 5, "Int", "Int"));
        sks.put("Parrying", new Skill("Parrying", 0, 50, 5, "Dexterity",
                "Strength"));
        sks.put("Herding", new Skill("Herding", 0, 50, 5, "Int", "Int"));
        sks.put("Carpentry",
                new Skill("Carpentry", 0, 50, 5, "Int", "Strength"));

        return sks;
    }

    private static Location getRandomStartLocation()
    {
        ArrayList<Location> startLocations = tookAllLocationsByFlag("Start");
        if (startLocations.size() != 0)
            return startLocations.get(random.nextInt(startLocations.size()));
        else
            return getLocation(1);
    }

    private static ArrayList<Location> tookAllLocationsByFlag(String string)
    {
        ArrayList<Location> ls = new ArrayList<Location>();
        for (World w : worlds.values())
        {
            for (Location l : w.locations.values())
            {
                if (l.haveFlag(string))
                {
                    ls.add(l);
                }
            }
        }
        return ls;
    }

    private static int getFreeId()
    {
        try
        {
            ResultSet rs = DatabaseClient
                    .commandExecute("SELECT max(id) as id FROM entities");
            int i = 0;
            if (rs.next())
            {
                i = rs.getInt("id");
            }
            return i + 1;
        } catch (SQLException e)
        {
            handleError(e);
        }
        return -1;
    }

    public static int getFreeItemId()
    {
        try
        {
            ResultSet rs = DatabaseClient
                    .commandExecute("SELECT max(id) as id FROM items");
            int i = 0;
            if (rs.next())
            {
                i = rs.getInt("id");
            }
            return i + 1;
        } catch (SQLException e)
        {
            handleError(e);
        }
        return -1;
    }

    public static void HandleMove(int id, ConnectedClient c)
    {
        // Main.Log("[MOVE]", "ID: " + moveRequest.id);
        // for(int near: c.controlledEntity.loc.nearLocationsIDs)
        // {
        // Main.Log("[MOVE]", "nlID: " + near);

        // }
        try
        {
            if (c.controlledEntity.loc.nearLocationsIDs.contains(id))
            {
                c.controlledEntity.loc.RemoveEntity(c.controlledEntity);
                MovePlayerAt(getLocation(id), c);
            } else
            {
                Main.Log("[ERROR]", "There's no near location with that id");
            }
        } catch (Exception e)
        {
            handleError(e);
        }
    }

    private static void MovePlayerAt(Location location, ConnectedClient c)
    {
        LocationInfoResponse lir = new LocationInfoResponse();
        lir.id = location.id;
        lir.name = location.name;
        SendTo(c.connection, lir);
        AddNearLocationResponse anlr;
        Location l;
        for (int i1 = 0; i1 < location.nearLocationsIDs.size(); ++i1)
        {
            l = getLocation(location.nearLocationsIDs.get(i1));
            anlr = new AddNearLocationResponse();
            anlr.id = l.id;
            anlr.name = l.name;
            SendTo(c.connection, anlr);
        }

        c.controlledEntity.loc = location;
        location.AddEntity(c.controlledEntity);
        location.SendEntitiesAround(c.controlledEntity);

        AddFlagResponse af = new AddFlagResponse();
        for (String s : c.controlledEntity.loc.flags.keySet())
        {
            af.flag = s;
            af.val = c.controlledEntity.loc.flags.get(s).value;
            SendTo(c.connection, af);
        }
    }

    public static void HandleAction(ActionType action, ConnectedClient c,
            String[] args)
    {
        // Main.Log("[DEBUG]", "Handling action " + action.name());
        try
        {
            switch (action)
            {
                case Cut:
                    c.controlledEntity.tryCut();
                    break;
                case Grow:
                    c.controlledEntity.tryGrow(args[2]);
                    break;
                case Herd:
                    c.controlledEntity.tryHerd();
                    break;
                case Mine:
                    c.controlledEntity.tryMine();
                    break;
                default:
                    break;

            }
        } catch (Exception e)
        {
            handleError(e);

        }

    }

    public static void HandleCast(String spellName, int eId,
            ConnectedClient c)
    {
        try
        {
            c.controlledEntity.tryCast(spellName.toLowerCase(), eId);
        } catch (Exception e)
        {
            handleError(e);
        }
    }

    public static void HandleAttack(int id, ConnectedClient c)
    {
        try
        {
            c.controlledEntity.startAttack(id);
        } catch (Exception e)
        {
            handleError(e);
        }

    }

    public static void EntityDead(final Entity entity, Entity from)
    {
        /*
         * Handling entitys death
         */
        Main.Log("[MESSAGE]", entity.caption + " the " + entity.type.name()
                + "(" + entity.id + ") dead!");

        warnEntity(entity, "==+[You're dead! Next time be more careful]+==");

        TravelEntity(entity, getRandomStartLocation());

        entity.setRebirthHitsAmount();
        entity.invul = true;
        entity.tryStopAttack();
        warnEntity(entity, "==+[You cannot be hurt by anyone]+==");

        Timer t = new Timer();
        t.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                entity.invul = false;
                warnEntity(entity, "==+[You can be hurt now]+==");
            }
        }, (long) (entity.invulTime * 1000));
    }

    public static void TravelEntity(Entity e, Location l)
    {
        e.loc.RemoveEntity(e);
        MovePlayerAt(l, getClientByEntity(e));
    }

    public static void handleError(Exception e)
    {
        Main.Log("[ERROR]", e.getMessage());
        e.printStackTrace();
    }

    public static Item checkInventory(Entity e, ActionType at)
    {
        for (Item i : inventories.get(e.id).getItems())
        {
            if (i.aType == at)
                return i;
        }
        return null;
    }

    public static Inventory getInventory(Entity entity)
    {
        return inventories.get(entity.id);
    }

    public static Entity getEntity(int entityId)
    {
        return entities.get(entityId);
    }

    public static void DestroyItem(Item item)
    {
        DatabaseClient.commandExecute("DELETE FROM items WHERE id = " + item.id
                + " LIMIT 1;");
        DatabaseClient.commandExecute("DELETE FROM attributes WHERE itemId = "
                + item.id + ";");
    }

    public static void DestroyItem(Inventory i, Item item)
    {
        i.RemoveItem(item.id);
        DestroyItem(item);
    }

    public static boolean haveItemSet(Entity e, ArrayList<String> reagentsNeeded)
    {
        Inventory i = getInventory(e);
        if (i != null)
        {
            for (String s : reagentsNeeded)
            {
                if (haveItem(e, s))
                {
                    continue;
                } else
                {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean haveItem(Entity e, String s)
    {
        Inventory i = getInventory(e);
        if (i != null)
        {
            for (Item item : i.items)
            {
                if (item.caption.equals(s))
                    return true;
            }
        }
        return false;
    }

    public static void consumeItem(Entity entity, String s)
    {
        Inventory i = getInventory(entity);
        if (i != null)
        {
            Item item = i.getItem(s);
            i.consume(item);
        }
    }

    public static void HandleCommand(CommandRequest commandRequest,
            Connection connection)
    {
        try
        {
            String commandKey = commandRequest.args[0];
            if (Server.commands.containsKey(commandKey))
            {
                Server.commands.get(commandKey).execute(commandRequest.args,
                        getClient(connection));
            } else
            {
                Server.warnClient(getClient(connection),
                        "Invalid server command");
            }
        } catch (Exception e)
        {
            handleError(e);
        }
    }

    public static void warnClient(ConnectedClient client, String string)
    {
        MessageResponse r = new MessageResponse();
        r.msg = string;
        SendTo(client.connection, r);
    }

    public static ActionType getActionFromString(String string)
    {
        for (ActionType aT : ActionType.values())
        {
            if (aT.name().toLowerCase().contains(string))
            {
                return aT;
            }
        }
        return ActionType.None;
    }

    public static float getPlantGrowTime(String seed)
    {
        try
        {
            return plantsGrowTime.get(seed);
        } catch (Exception e)
        {
            handleError(e);
            return 10;
        }
    }

    public static void DestroyFlag(int id, String string)
    {
        DatabaseClient
                .commandExecute("DELETE FROM locationflags WHERE locationId = "
                        + id + " AND flag='" + string + "' LIMIT 1;");
    }

    public static void HandleCraft(String string, int i, ConnectedClient c)
    {
        CraftSystem.tryCraft(string, i, c.controlledEntity);
    }
}
