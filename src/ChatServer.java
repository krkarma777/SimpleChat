import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

public class ChatServer {
    public static void main(String[] args) throws Exception{

        ServerSocket serverSocket = new ServerSocket(9999);
        List<ChatClient> list = Collections.synchronizedList(new ArrayList<>());

        while (true) {
            Socket socket = serverSocket.accept();
            ChatClient chatClient = new ChatClient(socket, list);
            chatClient.start();
        }
    }
}