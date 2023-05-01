import java.net.*;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

public class Client extends JFrame {
    Socket socket;
    BufferedReader br;
    BufferedWriter out;
    String clientName;
    JLabel heading = new JLabel("Client Area");
    JTextArea messagArea = new JTextArea();
    JTextField messageInput = new JTextField();
    Font font = new Font("Roboto", Font.PLAIN, 20);
    public Client() {
        try {
            System.out.println("Enter Username: ");
            Scanner sc = new Scanner(System.in);
            clientName = sc.nextLine();
            socket = new Socket("127.0.0.1", 7777);
            System.out.println("Connected to Server");
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            createGUI();
            handleEvents();
            startReading();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    String sendMessage = messageInput.getText();
                    messagArea.append("Me: " + sendMessage + "\n");
                    try {
                        out.write(clientName + ": " + sendMessage);
                        out.newLine();
                        out.flush();
                        messageInput.setText("");
                        messageInput.requestFocus();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        });
    }

    public void createGUI() {
        this.setTitle("Client Messenger");
        this.setSize(550, 600);
        this.setLocationRelativeTo(null); 
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        heading.setFont(font);
        messagArea.setFont(font);
        messageInput.setFont(font);
        ImageIcon icon = new ImageIcon("chat.png");
        Image scaleImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
        heading.setIcon(new ImageIcon(scaleImage));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setLayout(new BorderLayout());
        this.add(heading, BorderLayout.NORTH);
        this.add(messagArea, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);

        this.setVisible(true);
    }
    public void startReading() {
        Runnable r1 = () -> {

            System.out.println("Reading Started");
            String msgFromGroupChat;

            while (!socket.isClosed()) {
                try {
                    msgFromGroupChat = br.readLine();
                    messagArea.append(msgFromGroupChat + "\n");
                } catch (IOException e) {
                    closeEverything(socket, br, out);
                }
            }
        };

        new Thread(r1).start();
    }
    public void startWriting() {
        Runnable r2 = () -> {
            System.out.println("Writing Started");
            try {
                out.write(clientName);
                out.newLine();
                out.flush();
                Scanner sc = new Scanner(System.in);

                while (!socket.isClosed()) {
                    String msgToSend = sc.nextLine();
                    out.write(clientName + ": " + msgToSend);
                    out.newLine();
                    out.flush();
                }
            } catch (Exception e) {
                closeEverything(socket, br, out);
            }
        };

        new Thread(r2).start();
    }

    public void closeEverything(Socket socket, BufferedReader br, BufferedWriter out) {
        try {
            if (socket != null) {
                socket.close();
            }
            if (br != null) {
                br.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client c = new Client();
    }
}