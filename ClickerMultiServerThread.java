/*
  This is the thread that's spawned, with the connection of a new client to the
  server.
*/

import java.net.*;
import java.io.*;

/* An instance of Thread. */
public class ClickerMultiServerThread extends Thread {
    public static Socket socket = null;
    private int number_of_options = 0;
    private String[] class_list;

    /*
      The constructor. Accepts:
      socket: holds the socket the client is connected to.
      number_of_options: the options the client has to pick from.
        3 = ['A', 'B', 'C']
      class_list: an array of student numbers. Used for authentication.
    */
    public ClickerMultiServerThread(Socket socket, int number_of_options, String[] class_list) {
        super("ClickerMultiServerThread");
        this.socket = socket;
        this.number_of_options = number_of_options;
        this.class_list = class_list;
    }

    /*
      Over riding the run() method of Thread.
      opens a reader and writer from the sockets input and output stream.
    */
    public void run() {
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            String inputLine;
            String outputLine;
            /*
              instantiates an object (protocol class) that will keep track of
              the current state the client is on, when communicating over the socket.
            */
            ClickerProtocol clicker_protocol = new ClickerProtocol(number_of_options, class_list);
            outputLine = clicker_protocol.processInput(null);
            out.println(outputLine); //prints the response.

            while ((inputLine = in.readLine()) != null) {
                outputLine = clicker_protocol.processInput(inputLine);
                out.println(outputLine); //prints the response.
                if (outputLine.equals("Bye."))
                  break;
                if (outputLine.equals("Invalid student number. Connection closed."))
                  break;
            }
            socket.close();
        } catch (IOException e) {
            e.getMessage();
        }
    }
}
