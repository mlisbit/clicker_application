/*
  A client application for the clicker program.
  Done for yorkU's cse3214 class.
  This program is completely standalone, and requires no outside dependancies.
*/

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClickerClient {
    public static String server_ip_string = "";
    public static int port = 0;

    /*
      This function takes in a string representation of an IP address,
      and returns it as an array of bytes.
      Returns null if illegal IP address.
      String IP must be seperated by '.', like 127.0.0.1
    */
    public static byte[] convertStringToByteArrayIP(String string_IP) {
      String[] ip_parts = string_IP.split("\\.");
      if (ip_parts.length != 4) {
        return null;
      }
      byte[] final_byte_array = new byte[4];
      for (int i = 0 ; i < 4 ; i++) {
        final_byte_array[i] = (byte)Integer.parseInt(ip_parts[i]);
      }
      return final_byte_array;
    }

    public static void main(String[] args) throws IOException {
        /*
          User has the option to run program via command line. for example:
          java ClickerClient 127.0.0.1 1337
        */
        try {
          server_ip_string = args[0];
          port = Integer.parseInt(args[1]);
        } catch(Exception e) {}

        /*
          If no command line arguments were supplied, than ask for input.
          Error checking is included.
        */
        if (port == 0 && server_ip_string.equals("")) {
          Scanner reader = new Scanner(System.in);
          System.out.print("Enter server IP: ");
          server_ip_string = reader.nextLine();
          if (convertStringToByteArrayIP(server_ip_string) == null) {
            System.out.println("Invalid Server IP.");
            System.exit(1);
          }
          System.out.print("Enter server port: ");
          port = Integer.parseInt(reader.nextLine());
          if (port <= 0 || port > 65535) {
            System.out.println("Invalid Server Port Number.");
            System.exit(1);
          }
        }

        Inet4Address server_ip =(Inet4Address) Inet4Address.getByAddress(convertStringToByteArrayIP(server_ip_string));

        /*
          Now that we have the port and server IP, we can attempt to connect to
          the server.
        */
        try (
          Socket kkSocket = new Socket(server_ip, port);
          PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
          BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        ) {
          BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
          String fromServer;
          String fromUser;

          /*
            Continue the connection to the server until we get a null. or if the
            server reply "Bye."
          */
          while ((fromServer = in.readLine()) != null) {
            System.out.println("Server: " + fromServer);
            
            //if any of these outputs came fromt eh server, end input.
            if (fromServer.equals("Bye."))
              break;
            else if (fromServer.equals("Invalid student number. Connection closed."))
              break;

            //get user input.
            fromUser = stdIn.readLine();
            if (fromUser != null) {
              System.out.println("Client: " + fromUser); //disply to the user what was submited.
              out.println(fromUser);
            }
          }
        } catch (IOException e) {
          System.err.println("Couldn't get I/O for the connection to " + server_ip_string);
          System.exit(1);
        }
    } //
}
