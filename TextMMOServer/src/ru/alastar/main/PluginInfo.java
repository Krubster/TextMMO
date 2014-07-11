package ru.alastar.main;

public class PluginInfo
{
    public String name;
    public String pathToMainClass;
    public String[] authors;
    public String[] dependencies;
    
    public PluginInfo(String n, String p, String[] a, String[] d)
    {
        this.name = n;
        this.pathToMainClass = p;
        this.authors = a;
        this.dependencies = d;
    }

    public String getPathToMainClass()
    {
        return pathToMainClass;
    }

    public String getName()
    {
        return name;
    }
    
}
