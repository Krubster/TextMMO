package ru.alastar.main.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.esotericsoftware.kryonet.Connection;

import ru.alastar.database.DatabaseClient;
import ru.alastar.enums.ActionType;
import ru.alastar.enums.EntityType;
import ru.alastar.enums.EquipType;
import ru.alastar.game.Attributes;
import ru.alastar.game.Entity;
import ru.alastar.game.Inventory;
import ru.alastar.game.Item;
import ru.alastar.game.PlantsType;
import ru.alastar.game.Skill;
import ru.alastar.game.Skills;
import ru.alastar.game.Statistic;
import ru.alastar.game.Stats;
import ru.alastar.game.systems.GardenSystem;
import ru.alastar.game.worldwide.Location;
import ru.alastar.game.worldwide.LocationFlag;
import ru.alastar.game.worldwide.World;
import ru.alastar.main.Main;
import ru.alastar.main.net.requests.ActionRequest;
import ru.alastar.main.net.requests.AttackRequest;
import ru.alastar.main.net.requests.CastRequest;
import ru.alastar.main.net.requests.LoginRequest;
import ru.alastar.main.net.requests.MoveRequest;
import ru.alastar.main.net.requests.RegisterRequest;
import ru.alastar.main.net.responses.AddNearLocationResponse;
import ru.alastar.main.net.responses.AddSkillResponse;
import ru.alastar.main.net.responses.AddStatResponse;
import ru.alastar.main.net.responses.InventoryResponse;
import ru.alastar.main.net.responses.LocationInfoResponse;
import ru.alastar.main.net.responses.LoginResponse;
import ru.alastar.main.net.responses.MessageResponse;
import ru.alastar.main.net.responses.RegisterResponse;
import ru.alastar.main.net.responses.SetData;

public class Server {

	public static com.esotericsoftware.kryonet.Server server;
	public static int port = 25565;
	public static Hashtable<InetSocketAddress, ConnectedClient> clients;
	public static Hashtable<String, World> worlds;
	public static Hashtable<Integer, Inventory> inventories;

	public static Random random;

	public static void startServer() {
		try {
			server = new com.esotericsoftware.kryonet.Server();
			server.start();
			server.bind(port, port + 1);
			server.addListener(new TListener(server));
			Init();
		} catch (IOException e) {
			handleError(e);
		}
	}

	public static void Init() {
		try {
			random = new Random();
			clients = new Hashtable<InetSocketAddress, ConnectedClient>();
			worlds = new Hashtable<String, World>();
			inventories = new Hashtable<Integer, Inventory>();

			DatabaseClient.Start();
			LoadWorlds();
			LoadEntities();
			LoadInventories();
			LoadPlants();
			GardenSystem.StartGrowTimer();
			FillWoods();
		} catch (InstantiationException e) {
			Main.Log("[ERROR]", e.getLocalizedMessage());
		} catch (IllegalAccessException e) {
			Main.Log("[ERROR]", e.getLocalizedMessage());
		} catch (ClassNotFoundException e) {
			Main.Log("[ERROR]", e.getLocalizedMessage());
		}
		try {
			ExecutorService service = Executors.newCachedThreadPool();
			service.submit(new Runnable() {
				public void run() {
					try {
						BufferedReader in = new BufferedReader(
								new InputStreamReader(System.in));

						for (;;) {
							String line;

							line = in.readLine();

							if (line == null) {
								continue;
							}

							if ("save".equals(line.toLowerCase())) {
								Save();
								continue;
							}

							if ("stop".equals(line.toLowerCase())) {
								Save();
								server.close();
								break;
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				private void Save() {
					for (World w : worlds.values()) {
						for (Location l : w.locations.values()) {
							Server.SaveLocation(l);
						}
						GardenSystem.SaveAll();
					}
				}
			});
		} catch (Exception e) {
			handleError(e);
		}
	}

	private static void FillWoods() {
		Location.woods.put(0, "plain wood");
		Location.woods.put(10, "oak wood");
		Location.woods.put(25, "yew wood");
		Location.woods.put(45, "ash wood");
		Location.woods.put(50, "greatwood");
	}

	private static void LoadPlants() {
		ResultSet plantsRs = DatabaseClient
				.commandExecute("SELECT * FROM plants");
		try {
			while(plantsRs.next())
			{
				GardenSystem.addGrowingPlant(new PlantsType(plantsRs.getString("name"), plantsRs.getDate("growTime"), getLocation(plantsRs.getInt("locationId"))));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void SavePlant(PlantsType p) {
		ResultSet plantsRs = DatabaseClient
				.commandExecute("SELECT * FROM plants WHERE locationId="+ p.loc.id);
		try {
			if(plantsRs.next()){
				DatabaseClient.commandExecute("UPDATE plants SET name="
						+ p.plantName + ", growTime=" + p.finish
						+ " WHERE locationId=" + p.loc.id);

			} else {
				DatabaseClient
						.commandExecute("INSERT INTO plants(name, growTime, locationId) VALUES('"
								+ p.plantName
								+ "',"
								+  p.finish
								+ ","
								+ p.loc.id
								+ ")");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private static void LoadInventories() {
		ResultSet inventoriesRs = DatabaseClient
				.commandExecute("SELECT * FROM inventories");
		Inventory i;
		try {
			while (inventoriesRs.next()) {
				i = new Inventory(inventoriesRs.getInt("entityId"),
						inventoriesRs.getInt("max"),
						LoadItems(inventoriesRs.getInt("entityId")));
				inventories.put(i.entityId, i);
			}
		} catch (SQLException e) {
			handleError(e);
		}
	}

	private static ArrayList<Item> LoadItems(int i) {
		ArrayList<Item> items = new ArrayList<Item>();
		Attributes attrs;
		ResultSet itemsRS = DatabaseClient
				.commandExecute("SELECT * FROM items WHERE entityId=" + i);
		ResultSet attrsRS;
		try {
			while (itemsRS.next()) {
				attrsRS = DatabaseClient
						.commandExecute("SELECT * FROM attributes WHERE itemId=" + itemsRS.getInt("id"));
				//Loading attributes
				attrs = new Attributes();
				while(attrsRS.next())
				{
					attrs.addAttribute(attrsRS.getString("name"), attrsRS.getInt("value"));
				}
				
				items.add(new Item(itemsRS.getInt("id"), itemsRS
						.getInt("entityId"), itemsRS.getString("caption"),
						itemsRS.getInt("amount"), getLocation(itemsRS
								.getInt("locationId")), EquipType
								.valueOf(itemsRS.getString("type")), ActionType
								.valueOf(itemsRS.getString("actionType")), attrs));
			}
		} catch (SQLException e) {
			handleError(e);
		}

		return items;
	}

	private static void LoadEntities() {
		try {
			Hashtable<Integer, String> playersEntities = new Hashtable<Integer, String>();
			ResultSet accountsEntities = DatabaseClient
					.commandExecute("SELECT * FROM accounts");
			ResultSet allEntities = DatabaseClient
					.commandExecute("SELECT * FROM entities");
			Stats stats;
			Skills skills;
			ResultSet skillsRS;
			ResultSet statsRS;
			Entity e;

			while (accountsEntities.next()) {
				playersEntities.put(accountsEntities.getInt("entityId"),
						accountsEntities.getString("login"));
			}

			while (allEntities.next()) {
				if (!playersEntities.containsKey(allEntities.getInt("id"))) {
					stats = new Stats();

					skills = new Skills();
					skillsRS = DatabaseClient
							.commandExecute("SELECT * FROM skills WHERE entityId="
									+ allEntities.getInt("id"));
					statsRS = DatabaseClient
							.commandExecute("SELECT * FROM stats WHERE entityId="
									+ allEntities.getInt("id"));

					while (skillsRS.next()) {
						skills.put(
								skillsRS.getString("name"),
								new Skill(skillsRS.getString("name"), skillsRS
										.getInt("sValue"), skillsRS
										.getInt("mValue"), skillsRS
										.getFloat("hardness"), skillsRS
										.getString("primaryStat"), skillsRS
										.getString("secondaryStat")));
					}

					while (statsRS.next()) {
						stats.put(
								statsRS.getString("name"),
								new Statistic(statsRS.getString("name"),
										statsRS.getInt("sValue"), statsRS
												.getInt("mValue"), statsRS
												.getFloat("hardness")));
					}
					e = new Entity(allEntities.getInt("id"),
							allEntities.getString("caption"),
							EntityType.valueOf(allEntities.getString("type")),
							getLocation(allEntities.getInt("locationId")),
							skills, stats);
					e.loc.AddEntity(e);
				}
			}
		} catch (SQLException e) {
			handleError(e);
		}

	}

	protected static void SaveLocation(Location l) {
		// Location info
		DatabaseClient.commandExecute("UPDATE locations SET name='" + l.name
				+ "' WHERE id=" + l.id);
		// Near Locations
		String s = "";
		for (int id : l.nearLocationsIDs) {
			s += id + ";";
		}
		DatabaseClient.commandExecute("UPDATE locations SET nearLocationsIDs='"
				+ s + "' WHERE id=" + l.id);

		// Entities
		for (Entity e : l.entities.values()) {
			saveEntity(e);
		}
		
		//Flags
		for (String s1 : l.flags.keySet()) {
			saveFlag(l.id, s1, l.flags.get(s1));
		}
	}

	private static void saveFlag(int id, String s, LocationFlag e) {
		ResultSet plantsRs = DatabaseClient
				.commandExecute("SELECT * FROM locationflags WHERE locationId="+ id+ " AND flag="+ s);
		try {
			if(plantsRs.next()){
				DatabaseClient.commandExecute("UPDATE locationflags SET value="
						+ e.value +"  WHERE locationId=" + id+ " AND name="+ s);

			} else {
				DatabaseClient
						.commandExecute("INSERT INTO locationflags(locationId, flag, value) VALUES("
								+ id
								+ ",'"
								+  s
								+ "',"
								+ e.value
								+ ")");
			}
		} catch (SQLException e1) {
			handleError(e1);
		}
	}

	private static void LoadWorlds() {
		try {
			ResultSet worlds = DatabaseClient
					.commandExecute("SELECT * FROM worlds");
			ResultSet locations;
			ResultSet flags;

			World w;
			Hashtable<Integer, Location> locs;
			Hashtable<String, LocationFlag> lFlags;

			ArrayList<Integer> nlIDs;
			String[] locsIDsInStr;
			while (worlds.next()) {
				locs = new Hashtable<Integer, Location>();
				
				locations = DatabaseClient
						.commandExecute("SELECT * FROM locations WHERE worldId='"
								+ worlds.getString("name") + "'");
				Main.Log("[SERVER]",
						"Loading world " + worlds.getString("name") + "...");
				while (locations.next()) {
					Main.Log("[SERVER]", "Clearing nlIDs...");
					nlIDs = new ArrayList<Integer>();
					Main.Log("[SERVER]",
							"Getting IDs string and splitting it...");
					locsIDsInStr = locations.getString("nearLocationsIDs")
							.split(";");
					Main.Log("[SERVER]", "Iterating this string...");
					for (int i = 0; i < locsIDsInStr.length; ++i) {
						if (!locsIDsInStr[i].isEmpty()) {
							Main.Log("[SERVER]", "Adding locID "
									+ locsIDsInStr[i]);
							nlIDs.add(Integer.parseInt(locsIDsInStr[i]));
						}
					}
					Main.Log("[SERVER]", "Gettings flags...");
					flags = DatabaseClient
							.commandExecute("SELECT * FROM locationFlags WHERE locationId="
									+ locations.getInt("id"));
					Main.Log("[SERVER]", "Iterating flags...");
					lFlags = new Hashtable<String, LocationFlag>();
					while (flags.next()) {
						Main.Log("[SERVER]", "Putting " + flags.getString("flag"));

						lFlags.put(flags.getString("flag"), new LocationFlag(flags.getString("val")));
						
					}
					Main.Log("[SERVER]", "Putting location at hashtable...");
					locs.put(
							locations.getInt("id"),
							new Location(locations.getInt("id"), locations
									.getString("name"), nlIDs, lFlags));

				}

				w = new World(worlds.getString("name"), locs);
				Server.worlds.put(w.name, w);
			}
			Main.Log("[SERVER]", "Done! Now map is:");
			for (World w1 : Server.worlds.values()) {
				Main.Log("[WORLD]", w1.name);
				for (Location l1 : w1.locations.values()) {
					Main.Log(" [LOCATION]", l1.id + " - " + l1.name);
					for (String f : l1.flags.keySet()) {
						Main.Log(" -[FLAG]", f);
					}
					for (int nl : l1.nearLocationsIDs) {
						Main.Log("  [NEAR LOCATION]", nl + " ");
					}
				}
			}
		} catch (SQLException e) {
			handleError(e);
		}
	}

	public static boolean hasClient(Connection connection) {
		if (clients.containsKey(connection.getRemoteAddressUDP()))
			return true;
		else
			return false;
	}

	public static void addClient(Connection connection) {
		clients.put(connection.getRemoteAddressUDP(), new ConnectedClient(
				connection));
	}

	public static void removeClient(Connection connection) {
		ConnectedClient c = getClient(connection);
		if (c.controlledEntity != null) {
			Main.Log("[LOGIN]", "Controlled entity is not null, saving it...");
			c.controlledEntity.RemoveYourself();
		}
		else
			Main.Log("[LOGIN]", "Controlled entity is null, skipping save");
			
		clients.remove(connection.getRemoteAddressUDP());
	}

	public static void Login(LoginRequest object, Connection c) {
		try {
			Main.Log("[SERVER]", "Process auth...");
			ResultSet l = DatabaseClient
					.commandExecute("SELECT * FROM accounts WHERE login='"
							+ object.login + "' AND password='" + object.pass
							+ "'");
			if (l.next()) {
				Main.Log("[SERVER]", "...auth succesful!");

				LoginResponse r = new LoginResponse();
				r.succesful = true;
				SendTo(c, r);
				ConnectedClient client = getClient(c);
				client.login = l.getString("login");
				client.pass = l.getString("password");
				client.mail = l.getString("mail");

				LoadPlayer(l.getInt("entityId"), client);
			} else {
				Main.Log("[SERVER]", "...auth unsuccesful(");

				LoginResponse r = new LoginResponse();
				r.succesful = false;
				SendTo(c, r);
			}

		} catch (SQLException e) {
			handleError(e);
		}
	}

	private static void LoadPlayer(int i, ConnectedClient c) {
		try {
			ResultSet e = DatabaseClient
					.commandExecute("SELECT * FROM entities WHERE id=" + i);

			if (e.next()) {
				SetData sd = new SetData();
				Main.Log("[SERVER]", "Creating entity...");
				Stats stats = new Stats();
				Skills skills = new Skills();
				ResultSet skillsRS = DatabaseClient
						.commandExecute("SELECT * FROM skills WHERE entityId="
								+ e.getInt("id"));
				ResultSet statsRS = DatabaseClient
						.commandExecute("SELECT * FROM stats WHERE entityId="
								+ e.getInt("id"));

				while (skillsRS.next()) {
					skills.put(
							skillsRS.getString("name"),
							new Skill(skillsRS.getString("name"), skillsRS
									.getInt("sValue"), skillsRS
									.getInt("mValue"), skillsRS
									.getFloat("hardness"), skillsRS
									.getString("primaryStat"), skillsRS
									.getString("secondaryStat")));
				}

				while (statsRS.next()) {
					stats.put(
							statsRS.getString("name"),
							new Statistic(statsRS.getString("name"), statsRS
									.getInt("sValue"),
									statsRS.getInt("mValue"), statsRS
											.getFloat("hardness")));
				}

				Entity entity = new Entity(e.getInt("id"),
						e.getString("caption"), EntityType.valueOf(e
								.getString("type")),
						getLocation(e.getInt("locationId")), skills, stats);
				Main.Log("[SERVER]",
						"Assigning it as a controlled to the connected client...");
				c.controlledEntity = entity;
				sd.id = entity.id;
				Main.Log("[SERVER]", "Sending set data packet...");
				SendTo(c.connection, sd);
				LocationInfoResponse lir = new LocationInfoResponse();
				lir.id = entity.loc.id;
				lir.name = entity.loc.name;
				Main.Log("[SERVER]", "Sending load location packet...");
				SendTo(c.connection, lir);
				AddNearLocationResponse anlr;
				Location l;
				Main.Log("[SERVER]", "Sending near locations("
						+ entity.loc.nearLocationsIDs.size() + ", "
						+ entity.loc.name + ")...");
				for (int i1 = 0; i1 < entity.loc.nearLocationsIDs.size(); ++i1) {
					Main.Log("[SERVER]", "Selected id is "
							+ entity.loc.nearLocationsIDs.get(i1));
					l = getLocation(entity.loc.nearLocationsIDs.get(i1));
					anlr = new AddNearLocationResponse();
					anlr.id = l.id;
					anlr.name = l.name;
					Main.Log("[SERVER]", "Sending " + l.name + " info");
					SendTo(c.connection, anlr);
				}
				Main.Log("[SERVER]", "Adding entity to the location...");
				entity.loc.AddEntity(entity);
				Main.Log("[SERVER]", "Sending other entities to it...");
				entity.loc.SendEntitiesAround(entity);
				Main.Log("[SERVER]", "Sending stats...");
				AddStatResponse r = new AddStatResponse();
				for (String s : entity.stats.vals.keySet()) {
					r.name = s;
					r.sValue = entity.stats.get(s).value;
					r.mValue = entity.stats.get(s).maxValue;
					SendTo(c.connection, r);
				}
				Main.Log("[SERVER]", "Sending skills...");
				AddSkillResponse sr = new AddSkillResponse();
				for (String s : entity.skills.vals.keySet()) {
					sr.name = s;
					sr.sValue = entity.skills.get(s).value;
					sr.mValue = entity.skills.get(s).maxValue;
					SendTo(c.connection, sr);

				}
				Main.Log("[SERVER]", "Sending inventory...");
				InventoryResponse ir = new InventoryResponse();
				for (Item i1 : inventories.get(entity.id).items) {
					ir.id = i1.id;
					ir.captiion = i1.caption;
					ir.amount = i1.amount;
					SendTo(c.connection, ir);
				}
				Main.Log("[SERVER]", "Data was sent to player. Fuf...");
			}
		} catch (SQLException e1) {
			handleError(e1);
		}
	}

	public static void warnEntity(Entity e, String m) {
		MessageResponse r = new MessageResponse();
		r.msg = m;
		SendTo(getClientByEntity(e).connection, r);
	}

	public static Location getLocation(int int1) {
		Main.Log("[SERVER]", "Requesting location id " + int1);
		for (World w : worlds.values()) {
			if (w.locations.containsKey(int1)) {
				Main.Log("[SERVER]",
						"Returning location " + w.locations.get(int1).name);
				return w.locations.get(int1);
			}
		}
		Main.Log("[SERVER]", "Returning null location!");
		return null;
	}

	public static ConnectedClient getClient(Connection c) {
		return clients.get(c.getRemoteAddressUDP());
	}

	public static void SendTo(Connection c, Object o) {
		server.sendToUDP(c.getID(), o);
	}

	public static ConnectedClient getClientByEntity(Entity e1) {
		for (ConnectedClient c : clients.values()) {
			if (c.controlledEntity != null) {
				if (c.controlledEntity.id == e1.id) {
					return c;
				}
			}
		}
		return null;
	}

	public static void ProcessChat(String msg, Connection connection) {
		ConnectedClient c = getClient(connection);
		if (c.controlledEntity != null) {
			c.controlledEntity.loc.sendAll(msg, c.controlledEntity.caption);
		}
	}

	public static void saveEntity(Entity entity) {
		try {
			Main.Log("[SAVE]", "Saving entity...");
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
			Main.Log("[SAVE]", "Saving stats...");

			// Stats
			ResultSet statEqRS;
			for (Statistic s : entity.stats.vals.values()) {
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
			Main.Log("[SAVE]", "Saving skills...");

			// Skills
			ResultSet skillsEqRS;

			for (Skill s : entity.skills.vals.values()) {
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
			Main.Log("[SAVE]", "Saving inventory...");

			//Inventory
			Inventory inv = inventories.get(entity.id);
			if(inv != null)
			{
				saveInventory(inv);
			}
		} catch (SQLException e) {
			handleError(e);
		}
	}

	public static void ProcessRegister(RegisterRequest registerRequest,
			Connection connection) {
		try {
			ResultSet regRS = DatabaseClient
					.commandExecute("SELECT * FROM accounts WHERE login='"
							+ registerRequest.login + "' AND mail='"
							+ registerRequest.mail + "'");
			RegisterResponse r = new RegisterResponse();

			if (regRS.next()) {
				r.successful = false;
				Server.SendTo(connection, r);
			} else {
				CreateAccount(registerRequest);
				r.successful = true;
				Server.SendTo(connection, r);
			}
		} catch (SQLException e) {
			handleError(e);
		}
	}

	private static void CreateAccount(RegisterRequest registerRequest) {
		Entity e = new Entity(getFreeId(), registerRequest.name,
				registerRequest.type, Server.getRandomStartLocation(),
				Server.getStandardSkillsSet(), Server.getStandardStatsSet());
		createInventory(e.id);
		saveInventory(inventories.get(e.id));
		saveEntity(e);
		DatabaseClient
				.commandExecute("INSERT INTO accounts(login, password, mail, entityId) VALUES('"
						+ registerRequest.login
						+ "','"
						+ registerRequest.pass
						+ "','" + registerRequest.mail + "'," + e.id + ")");
	}

	private static void createInventory(int id) {
		Inventory i = new Inventory(id, 20);
		i.AddItem(new Item(getFreeItemId(), id, "Coin", 1, getLocation(1),
				EquipType.None, ActionType.None, new Attributes()));
		inventories.put(id, i);
	}

	private static void saveInventory(Inventory i) {
		ResultSet entityEqRS = DatabaseClient
				.commandExecute("SELECT * FROM inventories WHERE entityId="
						+ i.entityId);
		try {
			if (entityEqRS.next()) {
				DatabaseClient.commandExecute("UPDATE inventories SET max="
						+ i.maxItems + " WHERE entityId=" + i.entityId);

			} else {
				DatabaseClient
						.commandExecute("INSERT INTO inventories(entityId, max) VALUES("
								+ i.entityId + "," + i.maxItems + ")");
			}

			for (Item item : i.items) {
				SaveItem(item);
			}
		} catch (SQLException e) {
			handleError(e);
		}
	}

	private static void SaveItem(Item item) {
		Main.Log("[SAVE ITEM]", "Saving item " + item.caption);
		ResultSet itemEqRS = DatabaseClient
				.commandExecute("SELECT * FROM items WHERE entityid="
						+ item.entityId + " AND id="+item.id);
		try {
			if (itemEqRS.next()) {
				DatabaseClient.commandExecute("UPDATE items SET entityId="
						+ item.entityId + ", locationId=" + item.loc.id
						+ ", amount=" + item.amount + ", caption='"
						+ item.caption + "' WHERE id=" + item.id);

			} else {
				DatabaseClient
						.commandExecute("INSERT INTO items(id, locationId, caption, amount, entityId, type, actionType) VALUES(" // TODO: add fields
								+ item.id
								+ ","
								+ item.getLocId()
								+ ",'"
								+ item.caption
								+ "',"
								+ item.amount
								+ ",'"
								+ item.eqType.name()
								+ "','"
								+ item.aType.name()
								+ ")");
			}
			SaveAttributes(item);
		} catch (SQLException e) {
			handleError(e);
		}
	}

	private static void SaveAttributes(Item item) {
		ResultSet itemEqRS;
		try {
		for(String s: item.attributes.values.keySet()){
		itemEqRS = DatabaseClient
				.commandExecute("SELECT * FROM attributes WHERE itemId="
						+ item.id);

			if (itemEqRS.next()) {
				DatabaseClient.commandExecute("UPDATE attributes SET value="
						+ item.getAttributeValue(s) + " WHERE name='" + s +"' AND itemId=" + item.id);

			} else {
				DatabaseClient
						.commandExecute("INSERT INTO attributes(name, itemId, value) VALUES('"
								+ s
								+ "',"
								+ item.id
								+ ","
								+ item.getAttributeValue(s)
								+ ")");
			}
		}
		} catch (SQLException e) {
			handleError(e);
		}
	}

	private static Stats getStandardStatsSet() {
		Stats sts = new Stats();
		sts.put("Hits", new Statistic("Hits", 10, 50, 5));
		sts.put("Stringth", new Statistic("Strength", 5, 50, 5));
		sts.put("Dexterity", new Statistic("Dexterity", 5, 50, 5));
		sts.put("Int", new Statistic("Int", 5, 50, 5));
		return sts;
	}

	private static Skills getStandardSkillsSet() {
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
		return sks;
	}

	private static Location getRandomStartLocation() {
		ArrayList<Location> startLocations = tookAllLocationsByFlag("Start");
		if (startLocations.size() != 0)
			return startLocations.get(random.nextInt(startLocations.size()));
		else
			return getLocation(1);
	}

	private static ArrayList<Location> tookAllLocationsByFlag(String string) {
		ArrayList<Location> ls = new ArrayList<Location>();
		for (World w : worlds.values()) {
			for (Location l : w.locations.values()) {
				if (l.haveFlag(string)) {
					ls.add(l);
				}
			}
		}
		return ls;
	}

	private static int getFreeId() {
		try {
			ResultSet rs = DatabaseClient
					.commandExecute("SELECT max(id) as id FROM entities");
			int i = 0;
			if (rs.next()) {
				i = rs.getInt("id");
			}
			return i + 1;
		} catch (SQLException e) {
			handleError(e);
		}
		return -1;
	}

	public static int getFreeItemId() {
		try {
			ResultSet rs = DatabaseClient
					.commandExecute("SELECT max(id) as id FROM items");
			int i = 0;
			if (rs.next()) {
				i = rs.getInt("id");
			}
			return i + 1;
		} catch (SQLException e) {
			handleError(e);
		}
		return -1;
	}

	public static void HandleMove(MoveRequest moveRequest, Connection connection) {
		ConnectedClient c = getClient(connection);
       // Main.Log("[MOVE]", "ID: " + moveRequest.id);
       // for(int near: c.controlledEntity.loc.nearLocationsIDs)
       // {
       //     Main.Log("[MOVE]", "nlID: " + near);

       // }
		if (c.controlledEntity.loc.nearLocationsIDs.contains(moveRequest.id)) {
			c.controlledEntity.loc.RemoveEntity(c.controlledEntity);
			MovePlayerAt(getLocation(moveRequest.id), c);
		} else {
         Main.Log("[ERROR]", "There's no near location with that id");
		}
	}

	private static void MovePlayerAt(Location location, ConnectedClient c) {
		LocationInfoResponse lir = new LocationInfoResponse();
		lir.id = location.id;
		lir.name = location.name;
		SendTo(c.connection, lir);
		AddNearLocationResponse anlr;
		Location l;
		for (int i1 = 0; i1 < location.nearLocationsIDs.size(); ++i1) {
			l = getLocation(location.nearLocationsIDs.get(i1));
			anlr = new AddNearLocationResponse();
			anlr.id = l.id;
			anlr.name = l.name;
			SendTo(c.connection, anlr);
		}
		c.controlledEntity.loc = location;
		location.AddEntity(c.controlledEntity);
		location.SendEntitiesAround(c.controlledEntity);
	}

	public static void HandleAction(ActionRequest actionRequest,
			Connection connection) {
		try {
			ConnectedClient c = getClient(connection);
			switch (actionRequest.action) {
			case Cut:
				c.controlledEntity.tryCut();
				break;
			case Grow:
				c.controlledEntity.tryGrow();
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
		} catch (Exception e) {
			handleError(e);

		}

	}

	public static void HandleCast(CastRequest castRequest, Connection connection) {
		try {
			ConnectedClient c = getClient(connection);
			c.controlledEntity.tryCast(castRequest.spellId, castRequest.id);
		} catch (Exception e) {
			handleError(e);
		}
	}

	public static void HandleAttack(AttackRequest attackRequest,
			Connection connection) {
		try {
			ConnectedClient c = getClient(connection);
			c.controlledEntity.startAttack(attackRequest.id);
		} catch (Exception e) {
			handleError(e);
		}

	}

	public static void EntityDead(Entity entity) {
		/*
		 * Handling entitys death
		 */
		Main.Log("[MESSAGE]", entity.caption + " the " + entity.type.name()
				+ "(" + entity.id + ") dead!");

		warnEntity(entity, "==+[You're dead! Next time be more careful]+==");

		TravelEntity(entity, getRandomStartLocation());

		entity.setRebirthHitsAmount();

	}

	public static void TravelEntity(Entity e, Location l) {
		e.loc.RemoveEntity(e);
		MovePlayerAt(l, getClientByEntity(e));
	}

	public static void handleError(Exception e) {
		Main.Log("[ERROR]", e.getLocalizedMessage());
		e.printStackTrace();
	}

	public static Item checkInventory(Entity e, ActionType at) {
		for(Item i: inventories.get(e.id).getItems())
		{
			if(i.aType == at)
				return i;
		}
		return null;
	}

	public static Inventory getInventory(Entity entity) {
		return inventories.get(entity.id);
	}

	public static Entity getEntity(int entityId) {
		Entity e;
		for(World w: worlds.values())
		{
			for(Location l: w.locations.values())
			{
				e = l.getEntityById(entityId);
				if(e != null)
					return e;
			}
		}
		return null;
	}

}