package edu.wpi.always.checkers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import edu.wpi.sgf.comment.CommentLibraryHandler;
import edu.wpi.sgf.comment.CommentingManager;

public class CheckersCommentingManager extends CommentingManager {

   private static final String CommentLibraryFilePath = 
         "CheckersCommentLibraryCoupled.xml";

   public CheckersCommentingManager(){

      libHandler = new CommentLibraryHandler();

      SAXBuilder builder = new SAXBuilder();
      InputStreamReader is = null;

      try {
         is = new InputStreamReader(
               CheckersCommentingManager.class.getResourceAsStream(
                     "/edu/wpi/always/checkers/resources/" 
                           + CommentLibraryFilePath), "UTF-8");
      } catch (UnsupportedEncodingException e) {
         System.out.println(
               "Resource loading error in loading Checkers Comment Library."
                     + "The .xml file(s) should be in "
                     + "/edu/wpi/always/srummy/resources "
                     + "package.");
         e.printStackTrace();
      }

      try{

         Document xmldoc = (Document) builder.build(is);
         Element rootNode = xmldoc.getRootElement();

         libHandler.addTheseGameSpecificComments(rootNode);
         libHandler.importComments();

      }catch(JDOMException e) {
         System.out.println("Checkers Comment library parse error.");
         e.printStackTrace();
      }catch(IOException e){
         System.out.println("Checkers Comment library load error.");
         e.printStackTrace();
      }
   }
}
