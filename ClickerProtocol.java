/*
  Keep track of the current state of the protocol, and responds accordingly.
  This is the agreed upon method of communication between the server, and
  the client.
*/

import java.net.*;
import java.io.*;
import java.util.*;

public class ClickerProtocol {
  private static int options = 0;
  private static String[] class_list;

  /* different states in protocol */
  private static final int WAITING = 0;
  private static final int AUTHENTICATING = 1;
  private static final int AUTHENITCATED = 2;
  private static final int ANSWERING = 3;
  private static final int INVALID = 4;

  private static String student_number = ""; //stores the student number when it gets it.
  private static String student_response = ""; //stores the response when it gets it.

  private int state = WAITING; //current state the user is on.

  public ClickerProtocol(int number_of_options, String[] class_list) {
    options = number_of_options;
    this.class_list = class_list;
  }

  /*
    Depending on the given input and state, the protocol will reply differently.
    It ends when it says "Bye."
  */
  public String processInput(String theInput) {
    String theOutput = null;

    if (state == WAITING) {
        theOutput = "please authenticate via your student number.";
        state = AUTHENTICATING;
    } else if (state == AUTHENTICATING) {
      if (Arrays.asList(class_list).contains(theInput)) {
        student_number = theInput;
        //get the letter list from the number of options.
        //example: 5 = [A, B, C, D, E]
        String[] options_letter_list = ClickerServerHelp.numberToArrayOfChars(options);
        theOutput = "Please select from options: " + Arrays.toString(options_letter_list);
        state = ANSWERING;
      } else {
        theOutput = "Invalid student number. Connection closed.";
        state = WAITING;
      }
    } else if (state == ANSWERING) {
      //get an array of options ["A", "B", ...] for a given number.
      String[] options_letter_list = ClickerServerHelp.numberToArrayOfChars(options);

      //check if the answer is even in the list.
      if (!Arrays.asList(options_letter_list).contains(theInput.toUpperCase())) {
        student_response = theInput.toUpperCase();
        theOutput = "Bad input. Try again.";
        state = ANSWERING;
      } else {
        /*
          Reply accordingly, convert user input to upper case, and store into file.
        */
        theOutput = "Bye.";
        student_response = theInput.toUpperCase();
        ClickerServerHelp.putResponse(ClickerServer.class_response_file, student_number, student_response);
        state = WAITING;
      }
    }
    return theOutput;
  } //end process input.
}
