package server;

import java.net.InetAddress;

public class Client_info {
    private InetAddress address;
    private int port;
    private String name;
    private int id;

    public Client_info(String name, int id,InetAddress address, int port)
    {
        this.name = name;
        this.id = id;
        this.address = address;
        this.port = port;
    }
    public String getName()
    {
        return name;
    }
    public int getId()
    {
        return id;
    }
    public InetAddress getAddress()
    {
        return address;
    }
    public int getPort()
    {
        return port;
    }
}
