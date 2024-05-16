/******************************************************************************
 *  
 *  A library from Algorithms optional textbook
 *  to read in data of various types from: stdin, file, URL.
 *
 *  You need to code your own way for your CW3 program to read the input file.
 *
 ******************************************************************************/

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;


class In {
   private BufferedReader br;

   // system independent
   private final static String NEWLINE = System.getProperty("line.separator");

   // access data from file paths
   public In(String filePath) {

      // try to read file from local file path
      try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException("File not found: " + filePath);
            }
            FileInputStream fis = new FileInputStream(file);
            br = new BufferedReader(new InputStreamReader(fis));
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
            e.printStackTrace();
        }
   }
   

   // note read() returns -1 if EOF
   private int readChar() {
      int c = -1;
      try { c = br.read(); }
      catch(IOException ioe) { ioe.printStackTrace(); }
      return c;
   }

   // read a token - delete preceding whitespace and one trailing whitespace character
   public String readString() {
       int c;
       while ((c = readChar()) != -1)
          if (!Character.isWhitespace((char) c)) break;

       if (c == -1) return null;
 
       String s = "" + (char) c;
       while ((c = readChar()) != -1)
          if (Character.isWhitespace((char) c)) break;
          else s += (char) c;

       return s;
   }

   // return rest of line as string and return it, not including newline 
   public String readLine() {
       String s = null;
       try { s = br.readLine(); }
       catch(IOException ioe) { ioe.printStackTrace(); }
       return s;
   }

   // return rest of input as string, use StringBuffer to avoid quadratic run time
   public String readAll() {
       StringBuffer sb = new StringBuffer();
       String s;
       while ((s = readLine()) != null)
          sb.append(s).append(NEWLINE);
       return sb.toString();
   }

   public void close() { 
       try { br.close(); }
       catch (IOException e) { e.printStackTrace(); }
   }
   

}
