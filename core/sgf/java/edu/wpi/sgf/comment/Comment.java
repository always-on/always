package edu.wpi.sgf.comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a comment for use by
 * Social GamePlay Framework
 * 
 * @version 3.0
 * @author Morteza Behrooz
 */
public class Comment implements Comparable<Comment> {

   private String content;
   private String maker;
   private String madeOn;
   private List<String> tags = 
         new ArrayList<String>();
   private double strength;
   private double competitiveness;
   private double strengthSafeMargin = 0.1; //default
   private String gameType; //could be replaced by an enum {board, card, ...}
   private String gameName;
   private GameCommentingState gameState;
   
   public Comment(String someContent, String someMaker, 
         String someMadeOn, String[] someTags, double someStrength, 
         double someComepetitiveness, String someGameType, 
         String someGameName, GameCommentingState someGameState){
      content = someContent;
      maker = someMaker;
      madeOn = someMadeOn;
      for(String eachTag : someTags)
         tags.add(eachTag);
      strength = someStrength;
      competitiveness = someComepetitiveness;
      gameType = someGameType;
      gameName = someGameName;
      gameState = someGameState;
   }
   
   //constructor with strength safe margin
   public Comment(String someContent, String someMaker, 
         String someMadeOn, String[] someTags, double someStrength, 
         double someComepetitiveness, String someGameType, 
         String someGameName, double someStrengthSafeMargin, 
         GameCommentingState gameState){
      this(someContent, someMaker, someMadeOn, someTags, someStrength, 
            someComepetitiveness, someGameType, someGameName, gameState);
      strengthSafeMargin = someStrengthSafeMargin;
   }
   
   public Comment(String someContent){
      content = someContent;
   }

   public String getContent(){
      return content;
   }
   
   public String getMaker(){
      return maker;
   }
   
   public String getMadeOn(){
      return madeOn;
   }
   
   public List<String> getTags(){
      return tags;
   }
   
   public void addTag(String tag){
      tags.add(tag);
   }
   
   public double getStrength(){
      return strength;
   }
   
   public double getComepetitiveness(){
      return competitiveness;
   }
   
   public double getStrengthSafeMargin(){
      return strengthSafeMargin;
   }
   
   public String getGameType(){
      return gameType;
   }
   
   public String getGameName(){
      return gameName;
   }
   
   public GameCommentingState getGameState(){
      return gameState;
   }

   @Override
   public int compareTo(Comment other) {
      if(this.tags.size() 
            > other.tags.size())
         return -1;
      return 1;
   }
   
}
