import java.net.*;
import java.io.*;

class Server {

    ServerSocket server;
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    public Server() {
        try {
            server = new ServerSocket(7777);
            System.out.println("Waiting for Connection.........");

            while(!server.isClosed()){
            socket = server.accept();
            System.out.println("New Client Connected");

            ClientHandler clientHandler = new ClientHandler(socket);
            Thread thread = new Thread(clientHandler);
            thread.start();
            }
        } 
        catch (Exception e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket(){
        try{
            if(server != null){
                server.close();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server s = new Server();
    }
}