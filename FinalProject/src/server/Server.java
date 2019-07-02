package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Server {
    private static DatagramSocket socket;
    private static boolean running;
    private static int clientID;
    private static ArrayList<Client_info> clients = new ArrayList<Client_info>();
    private static ArrayList<String> username = new ArrayList<String>();
    private static Timer30 t = new Timer30();
    private static String[] badwords = new String[] {"shit", "fuck", "bitch","cunt","idiot","motherfucker","ass","dick","hoe","whore","boobs","tits","fucker","pussy","cock","fucking"};


    public static void start(int port){

        try{
            socket = new DatagramSocket(port);
            running = true;
            listen();
            System.out.println("Server Started on Port, " + port);
            t.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void listen(){
        Thread listenTread = new Thread("ChatProgram Listener"){
            public void run(){
                try {
                    while(running){
                        byte[] data = new byte[64000];
                        DatagramPacket packet = new DatagramPacket(data, data.length);
                        socket.receive(packet);
                        String dataTest = new String(data);
                        if (dataTest.contains("\\audio:"))
                        {
                            System.out.println("AUDIO BROADCAST");
                            broadcastBytes(data);
                            continue;
                        }
                        String message = new String(data);
                        System.out.println(message);
                        if (message.contains("\\e"))
                            message = message.substring(0,message.indexOf("\\e"));

                        if(!isCommand(message,packet)){
                            //MANAGE MESSAGE
                            for (String word : badwords)
                            {
                                System.out.println(message + " compare " + word);
                                if (message.toLowerCase().contains(word))
                                {
                                    int index = message.toLowerCase().indexOf(word);
                                    message = message.substring(0, index)+ "****" + message.substring(index + word.length());
                                }
                                else
                                {
                                    System.out.println("false");
                                }
                            }
                            broadcast(message);
                        }
                    }

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }; listenTread.start();

    }

    private static void send(String message, InetAddress address, int port){
        /*adding our ending identify thingy which is backslace e,
         and that's gonna be use when the client receives the message
         then we're coverting our string message into a byte array called data,
         and then we put that data inside of a datagram packet and we're gonna be telling the packet where it's going,
         which is our address and port, and then we re going to tell our packet to send that packet of information
         to this address and port that is inside of our packet*/
        try{
            message += "\\e";
            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
            System.out.println("Sent Message to,"+address.getHostAddress()+":"+port);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void sendBytes(byte[] message, InetAddress address, int port){
        try{
            byte[] data = message;
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
            System.out.println("Sent Message to,"+address.getHostAddress()+":"+port);


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private static void broadcast(String message){
        for(Client_info info : clients) {
            send(message, info.getAddress(),info.getPort());
            System.out.println("Sent " + message + " to " + info.getAddress().toString());
        }

    }

    private static void broadcastBytes(byte[] message) {
        for (Client_info info : clients) {
            sendBytes(message, info.getAddress(), info.getPort());
            System.out.println("Sent " + message + " to " + info.getAddress().toString());
        }
    }
    private static boolean isCommand(String message, DatagramPacket packet){
        String name = message.substring(0, message.indexOf(":"));
        if (message.contains("\\"))
            message = message.substring(message.indexOf("\\"));

        if (message.startsWith("\\con:")){
            //RUN CONNECTION CODE
            Client_info client_info = new Client_info(name, clientID++, packet.getAddress(), packet.getPort());
            clients.add(client_info);
            if (!username.contains(name)) {
                username.add(name);
                broadcast("\\loginsuccess:" + name);
                broadcast(String.format("User %s has Connected! say hi", name));
            }
            else
            {
                broadcast("\\loginfail:" + name);
                clients.remove(client_info);
            }
            return true;
        }
        if (message.startsWith("\\dis:")){
            //RUN DISCONNECTION CODE
            broadcast(String.format("User %s has Disconnected!", name));
            username.remove(name);

            return true;
        }
        if (message.startsWith("\\audio:"))
        {
            System.out.println("Received sound from " + name);
            broadcast(message);
            return true;
        }
        if (message.startsWith("\\"))
        {
            String command = message.substring(1);
            broadcast(name + ": " + message);
            switch(command) {
                case "help":
                    broadcast("BOT: Commands:\n" +
                              "        \\help\n" +
                              "        \\time");
                    return true;
                case "time":
                    LocalDate localDate = LocalDate.now();
                    LocalTime localTime = LocalTime.now();
                    broadcast("BOT: Current date and time is " + localDate.toString() + " " + localTime.toString().substring(0, localDate.toString().length() - 2));
                    return true;
                default:
                    broadcast("BOT: Command " + command + " not found. \\help to see all commands");
                    return true;
            }
        }
        return false;
    }

    private static class Timer30 extends Thread
    {
        @Override
        public void run()
        {
            while (true) {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                broadcast("BOT: Commands:\n" +
                          "        \\help\n" +
                          "        \\time");
            }
        }
    }
}
