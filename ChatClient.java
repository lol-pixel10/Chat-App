import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient extends Frame implements ActionListener, Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private TextField textField;
    private TextArea textArea;

    public ChatClient(String serverAddress) throws IOException {
        // Set up GUI
        setLayout(new BorderLayout());
        
        textField = new TextField();
        textArea = new TextArea();
        textArea.setEditable(false);

        add(textField, BorderLayout.SOUTH);
        add(textArea, BorderLayout.CENTER);

        textField.addActionListener(this);

        setTitle("Chat Client");
        setSize(400, 400);
        setVisible(true);

        // Connect to the server
        socket = new Socket(serverAddress, 5000);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Start a new thread to handle incoming messages
        new Thread(this).start();
    }

    // When the user hits "Enter", send the message to the server
    public void actionPerformed(ActionEvent e) {
        String message = textField.getText();
        textField.setText("");
        out.println(message); // Send message to the server
    }

    @Override
    public void run() {
        String message;
        try {
            // Continuously listen for messages from the server
            while ((message = in.readLine()) != null) {
                textArea.append(message + "\n"); // Display message in the text area
            }
        } catch (IOException e) {
            System.out.println("Connection closed.");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new ChatClient("localhost"); // Change to server address if not localhost
    }
}
