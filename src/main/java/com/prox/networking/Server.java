package com.prox.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {

        try {

            System.out.println("[SERVER]: Server Instantiated...");

            while(!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();
                System.out.println("\nA new client has connected!");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(4099);
        Server server = new Server(serverSocket);
        server.startServer();
        System.out.println("[SERVER]: ...Server Terminated");
        
    }

}
