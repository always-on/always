package edu.wpi.always.checkers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import edu.wpi.cetask.Utils;
import edu.wpi.sgf.comment.CommentLibraryHandler;
import edu.wpi.sgf.comment.CommentingManager;

public class CheckersCommentingManager extends CommentingManager {

   private static final String CommentLibraryFilePath = 
         "CheckersCommentLibrary.xml";

   public CheckersCommentingManager(){
      
      libHandler = new CommentLibraryHandler();

      SAXBuilder builder = new SAXBuilder();
      File commentLibraryFile = null;
      try {
         commentLibraryFile = new File(
               Utils.toURL("edu/wpi/always/checkers/resources/"+
                     CommentLibraryFilePath).toURI());
      } catch (MalformedURLException|URISyntaxException e) {
         System.out.println(
               "Resource loading error in loading Checkers Comment Library."
                     + "The .xml file(s) should be in "
                     + "edu/wpi/always/Checkers/resources/resources "
                     + "package which should be in the sgf classpath.");
         e.printStackTrace();
      }

      try{

         Document xmldoc = (Document) builder.build(commentLibraryFile);
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
