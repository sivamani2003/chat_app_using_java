import java.util.ArrayList;
import java.net.*;
import java.io.*;

public class ClientHandler implements Runnable{ 
    public static ArrayList<ClientHandler> clienthandlers = new ArrayList<>();

    ServerSocket server;
    Socket socket;
    BufferedReader br;
    BufferedWriter out;
    String clientName;

    public ClientHandler(Socket socket){
        try {
            this.socket = socket;
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientName = br.readLine();
            clienthandlers.add(this);
            broadCastMessage("Server: "+clientName+" has entered the chat");
        } 
        catch (Exception e) {
            closeEverything(socket, br, out);
        }
    }

    public void run(){
        String ClientMessage;

        while(socket.isConnected()){
            try {
                ClientMessage = br.readLine();
                broadCastMessage(ClientMessage);
            }
            catch (Exception e) {
                closeEverything(socket, br, out);
                break;
            }
        }
    }

    public void broadCastMessage(String messagetoSend){
        for(ClientHandler clientHandler : clienthandlers){
            try{
                if(!clientHandler.clientName.equals(clientName)){
                    clientHandler.out.write(messagetoSend);
                    clientHandler.out.newLine();
                    clientHandler.out.flush();
                }
            }
            catch(IOException e){
                closeEverything(socket, br, out);
            }
        }
    }
    public void removeClinetHandler(){
        clienthandlers.remove(this);
        broadCastMessage("Server: "+clientName+" has left the Chat"); 
    }
    public void closeEverything(Socket socket,BufferedReader br,BufferedWriter out){
        removeClinetHandler();
        try{
            if(socket != null){
                socket.close();
            }
            if(br != null){
                br.close();
            }
            if(out != null){
                out.close();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}