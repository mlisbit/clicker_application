/*
  The main program within the Clicker Server suite of classes.
  depends on ClickerServerHelper, which is a static class, that holds helpful
  functions related to file operations and such. Keeps the code cleaner.
  also depends on ClickerServerProtocol: which is a class containing the protocol,
  both client and server agreed upon.

  Author: Maciej Lis.
*/

import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.*;

public class ClickerServer {
    public static int number_of_options = 0;
    public static String class_list_file = "class_list.txt";
    public static String class_response_file = "class_response.txt";
    public static Thread thr1 = new Thread();
    private static ServerSocket serverSocket;

    //only done once within the code, improving performance.
    public static String[] class_list = ClickerServerHelp.getClassListArray("class_list.txt");

    //stores an ArrayList of all socket connections  to the server.
    public static ArrayList<ClickerMultiServerThread> clicker_clients_list = new ArrayList<ClickerMultiServerThread>();

    public static void main(String[] args) throws IOException {

      int portNumber = 1337;

      //output of help command.
      String[] commands = {
        "List of possible commands:",
        "- START_QUESTIONS(n)",
        "- END_QUESTIONS",
        "- LIST",
        "- CLEAR_FILE",
        "- NUM_CONNECTED",
        "- IS_RUNNING",
        "- EXIT"
      };

      System.out.println("Waiting for command. type 'HELP' for list of commands.");

      while (true) {
        //get user input.
        Scanner reader = new Scanner(System.in);
        System.out.print("$> ");
        String input = reader.nextLine();

        //regex string match for "start_questions(x)" where x can be any number.
        //case insensitive for the convenience of the user.
        Matcher m = Pattern.compile("start_questions\\((\\d*)\\)", Pattern.CASE_INSENSITIVE).matcher(input);

        if (input.equalsIgnoreCase("help")) {
          for (int i = 0 ; i < commands.length ; i++) {
            System.out.println(commands[i]);
          }
        } else if (input.equalsIgnoreCase("exit")) {
            System.exit(-1);

        } else if (input.equalsIgnoreCase("is_running")) {
            //simply check if our thread handling the socket threads is running.
            System.out.println(thr1.isAlive());

        } else if (input.equalsIgnoreCase("clear_file")) {
            //removes the contents of the file within class_response_file
            ClickerServerHelp.clearFile(class_response_file);
            System.out.println("Done.");

        } else if (input.equalsIgnoreCase("list")) {
            //print the contents of the class_response_file file.
            System.out.println(ClickerServerHelp.printFile(class_response_file));
            System.out.println("Done.");

        } else if (input.equalsIgnoreCase("num_connected")) {
            int count = 0;
            for (ClickerMultiServerThread client : clicker_clients_list) {
              if(client.isAlive()) count++; //add it to the count only if the thread is currently active.
            }
            System.out.println(count);

        } else if (input.equalsIgnoreCase("end_questions")) {
            if (thr1.isAlive()) {
              System.out.println("Please wait...");
              serverSocket.close(); //close the main socket.
              for (ClickerMultiServerThread client : clicker_clients_list) {
                client.socket.close(); //close all instances of sockets within server thread.
                client.interrupt(); //interrupt those threads.
              }
              thr1.interrupt(); //interupt the internal server thread.
            } else {
              System.out.println("there's nothing to stop.");
            }

        } else if (m.find()) {
            if (Integer.parseInt(m.group(1)) > 26 || Integer.parseInt(m.group(1)) < 2) {
              //dont allow more than 26 options (the number of letters in the alphabet)
              System.out.println("Entered Number is too high. Server did not start.");
            } else if (thr1.isAlive()) {
              //prevents the user from starting the server twice.
              System.out.println("Your server is still running.");
            } else {
              //get the number of options specified.
              number_of_options = Integer.parseInt(m.group(1));

              Runnable r1 = new Runnable() {
                public void run() {
                  /*
                    attempt to listen on a specified port.
                    throws an exception if we cant listen on the specified port.
                  */
                  try {
                    serverSocket = new ServerSocket(portNumber);
                    while (true) {
                      ClickerMultiServerThread hey = new ClickerMultiServerThread(
                        serverSocket.accept(), number_of_options, class_list);
                      hey.start();
                      /*
                        add the thread to an ArrayList, this makes it easy to count total
                        number of responses, and currently connected clients.
                      */
                      clicker_clients_list.add(hey);
                    }
                  } catch (SocketException e) {
                    /*
                      this happens when you end the questions. Because the Socket
                      closes, and raises this exception.
                    */
                    System.out.println("Closed server socket.");
                  } catch (IOException e) {
                    System.err.println("Could not listen on port " + portNumber);
                    System.exit(-1);
                  }
                }
              };//end runnable

              thr1 = new Thread(r1);
              thr1.start();
              System.out.println("Started server thread.");
            } //else
        } else {
          System.out.println("invalid option");
        }
      }


  }
}
