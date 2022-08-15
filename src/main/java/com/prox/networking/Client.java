package com.prox.networking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

import com.prox.display.Window;
import com.prox.display.scenes.BasicScene;

import org.joml.Vector3f;

public class Client {
    
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
            // bufferedWriter.write(username);
            // bufferedWriter.newLine();
            // bufferedWriter.flush();

            // Scanner scanner = new Scanner(System.in);
            // while (socket.isConnected()) {
            //     String messageToSend = scanner.nextLine();
            //     bufferedWriter.write(username + ": " + messageToSend);
            //     bufferedWriter.newLine();
            //     bufferedWriter.flush();

        while (socket.isConnected()) {
            try {
                Vector3f currentPos = BasicScene.entities.get(0).getPosition();
                bufferedWriter.write(currentPos.x + " " + currentPos.y + " " + currentPos.z);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (Exception e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }

    }

    public void listenForMessage() {
        String msgFromGroupChat;
        while (socket.isConnected()) {
            try {
                msgFromGroupChat = bufferedReader.readLine();
                System.out.println(msgFromGroupChat);
            } catch (Exception e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void playGame() {
        Window window = Window.get();
        window.run();

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter a username: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 4099);
        Client client = new Client(socket, username);
        client.playGame();

    }
    
}
