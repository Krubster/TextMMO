package ru.alastar.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.alastar.main.net.Server;

public class Main
{

    public static File             logFile;
    public static BufferedWriter   writer = null;
    public static SimpleDateFormat dateFormat;
    public static ExecutorService  service;
    public static String           version = "1.15.1";
    public static ArrayList<String>authors = new ArrayList<String>();
    
    public static void main(String[] args)
    {
        try
        {
            service = Executors.newCachedThreadPool(); 
            CreateLogFile();
            authors.add("Old Man(Alex) - idea creator");
            authors.add("Alastar(Michael Gess) - programmer");
            Log("[SERVER]", "Game server version " + version + " starting");
            Log("[SERVER]", "Dont forget to give thanks to the authors: ");
            for(String s: Main.authors)
            {
                Log("[AUTHOR]", s);
            }
            Server.startServer();
        } catch (Exception e)
        {
            Log("[SERVER]", e.getMessage());
        }
    }

    private static void CreateLogFile()
    {
        try
        {
            File theDir = new File("logs");
            if (!theDir.exists())
            {
                try
                {
                    theDir.mkdir();
                } catch (SecurityException se)
                {
                }
            }
            dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String timeLog = dateFormat
                    .format(Calendar.getInstance().getTime());

            logFile = new File("logs/log-" + timeLog + ".txt");
            writer = new BufferedWriter(new FileWriter(logFile));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void Log(String prefix, String msg)
    {
        try
        {
            writer.write("["
                    + dateFormat.format(Calendar.getInstance().getTime()) + "]"
                    + prefix + ":" + msg + "\n");
            System.out.println(prefix + ":" + msg);
            writer.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
