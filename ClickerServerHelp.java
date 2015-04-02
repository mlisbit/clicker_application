import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.*;
import java.text.SimpleDateFormat;

public final class ClickerServerHelp {

  /*
    this will convert the a number to an array of options.
    for example 5 = [A, B, C, D, E], 4 = [A, B, C, D]
  */
  public static String[] numberToArrayOfChars(int options) {
    List<String> option_list = new ArrayList<String>();
    for (int i = 1 ; i <= options ; i++) {
      option_list.add(String.valueOf((char)(i + 'A' - 1)));
    }
    return option_list.toArray(new String[option_list.size()]);
  }


  /*
    converts the class list text file into an array of strings.
    when only done once, this increases the performance of the code.
  */
  public static String[] getClassListArray(String file_location) {
    ArrayList<String> return_class_list = new ArrayList<String>();
     try{
        FileReader inputFile = new FileReader(file_location);
        BufferedReader bufferReader = new BufferedReader(inputFile);
        String line;

        while ((line = bufferReader.readLine()) != null)   {
          return_class_list.add(line);
        }

        bufferReader.close();
     }catch(Exception e){
        System.out.println("Error reading class list file. " + e.getMessage());
     }
    return return_class_list.toArray(new String[return_class_list.size()]);
  }


  /*
    removes the contents of a file.
  */
  public static void clearFile(String file_location) {
    try {
      PrintWriter writer = new PrintWriter(file_location);
      writer.print("");
      writer.close();
    } catch (IOException e) {
      System.out.println("ERROR: " + e.getMessage());
    }
  }

  /*
    puts a students response to file. in the format of:
      student_number : student_response : time_stamp
    this makes it easy to parse the output via grep or other extenal tools.
  */
  public static void putResponse(String file_location, String student_number, String response) {
    //current date and time.
    String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

    try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file_location, true)))) {
      out.println(student_number + " : " + response + " : " + date + "");
    }catch (IOException e) {
      System.out.println("Error writing to file.");
    }

  }

  /*
    returns the contents of a single file as a single string.
  */
  public static String printFile(String file_location) {
    String return_string = "";
    try{
       FileReader inputFile = new FileReader(file_location);
       BufferedReader bufferReader = new BufferedReader(inputFile);
       String line;

       while ((line = bufferReader.readLine()) != null)   {
         return_string = return_string + line + "\n";
       }

       bufferReader.close();
    }catch(Exception e){
       System.out.println("Error reading class list file. " + e.getMessage());
    }

    return return_string;
  }

}
