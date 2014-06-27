package ru.alastar.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ru.alastar.main.Main;

public class DatabaseClient {
	public static Connection conn = null;

	public static void Start() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager
					.getConnection("jdbc:mysql://localhost/textmmo?"
							+ "user=root&password=");
		} catch (SQLException ex) {
			Main.Log("[ERROR]", "SQLException: " + ex.getMessage());
			Main.Log("[ERROR]", "SQLState: " + ex.getSQLState());
			Main.Log("[ERROR]", "VendorError: " + ex.getErrorCode());
		}
	}

	public static ResultSet commandExecute(String cmd) {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareStatement(cmd);

			if (stmt.execute(cmd)) {
				rs = stmt.getResultSet();
			}

			return rs;
		} catch (SQLException ex) {
			Main.Log("[ERROR]", "SQLException: " + ex.getMessage());
			Main.Log("[ERROR]", "SQLState: " + ex.getSQLState());
			Main.Log("[ERROR]", "VendorError: " + ex.getErrorCode());
			return null;
		}
	}

}
