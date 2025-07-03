import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient extends Frame implements ActionListener, Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private TextField inputField;
    private TextArea chatArea;
    private Button sendButton;

    public ChatClient(String serverAddress) {
        setupUI();

        try {
            socket = new Socket(serverAddress, 5000);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            chatArea.append("✅ Connected to server at " + serverAddress + "\n");

            new Thread(this, "Client-Receiver-Thread").start();
        } catch (IOException e) {
            showError("❌ Could not connect to server: " + e.getMessage());
        }
    }

    private void setupUI() {
        setTitle("Java Chat Client");
        setSize(500, 400);
        setLayout(new BorderLayout());

        chatArea = new TextArea();
        chatArea.setEditable(false);

        inputField = new TextField();
        sendButton = new Button("Send");

        Panel bottomPanel = new Panel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(chatArea, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        inputField.addActionListener(this);
        sendButton.addActionListener(this);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                disconnect();
                System.exit(0);
            }
        });

        setVisible(true);
    }

    private void showError(String message) {
        chatArea.append("[Error] " + message + "\n");
    }

    private void disconnect() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            showError("Error closing connection: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            inputField.setText("");
        }
    }

    @Override
    public void run() {
        String incomingMessage;
        try {
            while ((incomingMessage = in.readLine()) != null) {
                chatArea.append(incomingMessage + "\n");
            }
        } catch (IOException e) {
            showError("Disconnected from server.");
        } finally {
            disconnect();
        }
    }

    public static void main(String[] args) {
        new ChatClient("localhost");
    }
}
