package edu.wpi.always.srummy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import edu.wpi.sgf.comment.CommentLibraryHandler;
import edu.wpi.sgf.comment.CommentingManager;

public class SrummyCommentingManager extends CommentingManager {

   private static final String CommentLibraryFilePath = 
         "SrummyCommentLibraryCoupled.xml";

   public SrummyCommentingManager(){
      
      libHandler = new CommentLibraryHandler();

      SAXBuilder builder = new SAXBuilder();
      InputStreamReader is = null;
      
      try {
         is = new InputStreamReader(
               SrummyCommentingManager.class.getResourceAsStream(
                     "/edu/wpi/always/srummy/resources/" 
                           + CommentLibraryFilePath), "UTF-8");
      } catch (UnsupportedEncodingException e) {
         System.out.println(
               "Resource loading error in loading Srummy Comment Library."
                     + "The .xml file(s) should be in "
                     + "/edu/wpi/always/srummy/resources "
                     + "package.");
         e.printStackTrace();
      }

      try{

         Document xmldoc = builder.build(is);
         Element rootNode = xmldoc.getRootElement();

         libHandler.addTheseGameSpecificComments(rootNode);
         libHandler.importComments();
         
      }catch(JDOMException e) {
         System.out.println("Srummy Comment library parse error.");
         e.printStackTrace();
      }catch(IOException e){
         System.out.println("Srummy Comment library load error.");
         e.printStackTrace();
      }
   }
}
