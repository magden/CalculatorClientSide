import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client's side of the calculator. Connects to the server, read input from client and sends to server.
 * Displays result on the console.
 */
public class CalculatorClient {
    /** The socket for connection to the  server     */
    private Socket clientSocket;
    /** The writer for representations of objects to a text-output stream   */
    private PrintWriter writer;
    /** Buffer for reading output stream     */
    private BufferedReader reader;
    /** Server's ip    */
    private static final String IP = "127.0.0.1";
    /** Server's port for connecting    */
    private static final int PORT = 7777;
    /** Quantity of attempts to connect after failure   */
    private static final int ATTEMPTS = 5;
    /** Wait between reconnection in milliseconds    */
    private static final long WAIT_BETWEEN_ATTEMPTS = 3000;
    /**  Amount of milliseconds in secods   */
    private static final long MILL_IN_SEC = 1000;

    /**
     * Trying to connect to the server, after the session was established scans client's input and sends to the
     * server.
     */
    private void start() {
        for (int i = 0; i < ATTEMPTS; i++) {
            if (connectedToServer()) {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    System.out.println("Enter arithmetic expression to calculate:");
                    String input = scanner.nextLine();
                    String resp = sendMessage(input);
                    System.out.println(resp);
                }
            }
            System.out.println("Failed connect to the server.\n Will try again in "
                    + WAIT_BETWEEN_ATTEMPTS / MILL_IN_SEC + " seconds.");
            try {
                Thread.sleep(WAIT_BETWEEN_ATTEMPTS);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Connects to the calculator server and reads response
     *
     * @return if connection succeed- true, else false
     */
    private boolean connectedToServer() {
        try {
            System.out.println("Connecting to server...");
            clientSocket = new Socket(CalculatorClient.IP, CalculatorClient.PORT);
            System.out.println("Connection to server established.");
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println(reader.readLine());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * @param msg the message to send
     * @return response from the server
     */
    private String sendMessage(String msg) {
        String resp = "";
        try {
            writer.println(msg);
            resp = reader.readLine();
        } catch (IOException e) {
            System.err.println("Connection was lost, please reconnect");
            stopConnection();
        }
        return resp;
    }

    /**
     * Stop the connection
     */
    public void stopConnection() {
        try {
            reader.close();
            writer.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CalculatorClient client = new CalculatorClient();
        client.start();
    }
}
