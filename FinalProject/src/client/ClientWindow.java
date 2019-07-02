package client;

import javax.swing.*;
import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.DefaultCaret;

public class ClientWindow{
    private JFrame frame;
    private JTextField textField;
    private static JTextArea textArea = new JTextArea();
    private static Client client;
    private static String name;


      //Launch the application.

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (name != null && (client.isLoggedin()))
                    client.send("\\dis:" + name, InetAddress.getByName("localhost"), 1218);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }));
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    ClientWindow window = new ClientWindow();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


     //Create the application.


    public ClientWindow() {
        initialize();
        frame.setTitle("Leagrove's ChatRoom");
        name = JOptionPane.showInputDialog("Username : ");
        if (!name.equals(null))
            client = new Client(name,"localhost",1218, this);
        else System.exit(0);
    }


     //Initialize the contents of the frame.

    private void initialize() {
        frame = new JFrame();
        frame.setResizable(false);
        frame.setTitle("Leagrove's ChatRoom");
        frame.setBounds(100, 100, 604, 433);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textArea.setEditable(false);
        textArea.setBackground(new Color(15, 15, 15));
        textArea.setForeground(new Color(32, 255, 14));
        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.getContentPane().add(scrollPane);

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.SOUTH);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));



        textField = new JTextField();
        panel.add(textField);
        textField.setColumns(35);
        textField.addActionListener(new textFieldActionListener());

        JButton btnNewButton = new JButton("->");
        btnNewButton.addActionListener(e -> {
            if (!textField.getText().equals("")){
                try {
                    client.send(textField.getText(), InetAddress.getByName("localhost"), 1218);
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                }
                textField.setText("");
            }}
        );
        panel.add(btnNewButton);

        JButton btnRecord = new JButton("R");
        btnRecord.addActionListener(new btnRecordActionListener());
        panel.add(btnRecord);


        frame.setLocationRelativeTo(null);
    }
    public static void printToConsole(String message) {
        textArea.setText(textArea.getText()+message+"\n");
    }


    private class textFieldActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!textField.getText().equals("")){
                try {
                    client.send(textField.getText(), InetAddress.getByName("localhost"), 1218);
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                }
                textField.setText("");
            }
        }
    }

    private class btnRecordActionListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e) {
            Audio a = new Audio();
            try {
                client.sendBytes(("\\audio:" + a.getAudioData() + "\\e").getBytes(), InetAddress.getByName("localhost"), 1218);
                File f = new File("record.wav");
                f.delete();
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            }
        }
    }

    public JFrame getFrame() {
        return frame;
    }
}
