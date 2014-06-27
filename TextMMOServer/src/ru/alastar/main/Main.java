package ru.alastar.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ru.alastar.main.net.Server;

public class Main {

    public static File logFile;
    public static BufferedWriter writer = null;
    public static SimpleDateFormat dateFormat;
    
	public static void main(String[] args) {
		try {
		    CreateLogFile();
			Server.startServer();
		} catch (Exception e) {
			Log("[SERVER]", e.getMessage());
		}
	}

	private static void CreateLogFile()
    {
        try {
            dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String timeLog = dateFormat.format(Calendar.getInstance().getTime());
            
            logFile = new File("logs/log-"+timeLog+".txt");
            writer = new BufferedWriter(new FileWriter(logFile));
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    public static void Log(String prefix, String msg) {
        try
        {
            writer.write("["+dateFormat.format(Calendar.getInstance().getTime())+"]" + prefix + ":" + msg + "\n");
            System.out.println(prefix + ":" + msg);
            writer.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
	}
}
