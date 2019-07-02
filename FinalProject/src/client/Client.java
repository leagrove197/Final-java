package client;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Base64;

public class Client {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    public boolean running;
    private String name;
    private boolean isLoggedin = false;
    private ClientWindow clientWindow;


    public Client(String name, String address, int port, ClientWindow clientWindow){
        this.clientWindow = clientWindow;
        try{
            this.name = name;
            this.address = InetAddress.getByName(address);
            this.port = port;
            running = true;
            socket = new DatagramSocket();
            socket.setSendBufferSize(64000);
            listen();
            send("\\con:"+name, InetAddress.getByName("localhost"), port);
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }


    public void send(String message, InetAddress address, int port){
        try{
            message = name+" : "+message;
            message += "\\e";
            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
            System.out.println("Sent Message to,"+address.getHostAddress()+":"+port);


        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendBytes(byte[] message, InetAddress address, int port){
        try{
            byte[] data = message;
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
            System.out.println("Sent Message to,"+address.getHostAddress()+":"+port);


        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void listen(){
        Thread listenTread = new Thread("ChatProgram Listener"){
            public void run(){
                try {
                    while(running){
                        byte[] data = new byte[64000];
                        DatagramPacket packet = new DatagramPacket(data, data.length);
                        socket.receive(packet);
                        String message = new String(data);
                        message = message.substring(0,message.indexOf("\\e"));
                        if (message.contains("\\audio:"))
                        {
                            message = message.replace("\\audio:", "");
                            saveAudio(Base64.getDecoder().decode(message));
                            ClientWindow.printToConsole("BOT: Audio received. Check file on the jar folder.");
                            continue;
                        }
                        System.out.println(message);
                        if(!isCommand(message,packet )) {
                            //MANAGE MESSAGE
                            ClientWindow.printToConsole(message );
                            System.out.println(message);
                        }
                    }

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }; listenTread.start();

    }

    private boolean isCommand(String message, DatagramPacket packet){
        if (message.startsWith("\\con:")){
            //RUN CONNECTION CODE
            return true;
        }
        if (message.startsWith("\\sound"))
        {
            message = message.substring(message.indexOf(":") + 1);
            return true;
        }
        if (message.contains("\\loginsuccess:"))
        {
            if (message.contains(name)) {
                isLoggedin = true;
            }
            return true;
        }
        if (message.contains("\\loginfail:"))
        {
            if (message.contains(name) && !isLoggedin) {
                JOptionPane.showMessageDialog(null, "Username has been taken", "Error", JOptionPane.ERROR_MESSAGE);
                clientWindow.getFrame().setVisible(false);
                ClientWindow.main(new String[] {});
            }
            return true;
        }
        return false;
    }

    private static void saveAudio(byte[] audio)
    {
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        String fileName = "recording " + localDate.toString() + "-" + localTime.toString().substring(0, localDate.toString().length() - 2) + ".wav";
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(audio);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isLoggedin() {
        return isLoggedin;
    }
}
