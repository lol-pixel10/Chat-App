import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    // List to store all connected client threads
    private static Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Server started. Waiting for clients...");

        while (true) {
            // Accept client connection
            Socket socket = serverSocket.accept();
            System.out.println("New client connected");

            // Create a new handler for this client
            ClientHandler clientHandler = new ClientHandler(socket);
            clientHandlers.add(clientHandler);

            // Start a new thread for the client
            Thread thread = new Thread(clientHandler);
            thread.start();
        }
    }

    // Broadcasts a message to all clients
    public static void broadcastMessage(String message, ClientHandler excludeClient) {
        for (ClientHandler client : clientHandlers) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }

    // Removes a client from the handler list
    public static void removeClient(ClientHandler clientHandler) {
        clientHandlers.remove(clientHandler);
        System.out.println("Client disconnected");
    }

    // Client handler class that manages individual clients
    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }

        @Override
        public void run() {
            String message;
            try {
                // Continuously listen for client messages
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);
                    ChatServer.broadcastMessage(message, this);
                }
            } catch (IOException e) {
                System.out.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ChatServer.removeClient(this);
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }
}
