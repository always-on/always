package edu.wpi.sgf.comment;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
import edu.wpi.cetask.Utils;

/**
 * Comment Library handler for Social Gameplay Framework.
 * Retrieves comments from library file.
 * @author Morteza Behrooz
 * @version 3.0
 */
public class CommentLibraryHandler {


   private final static String CommentLibraryFilePath =
         "CommentLibraryCoupled.xml";
   private File commentLibraryFile;


   private List<Element> 
   retrievedAgentCommentsRootNode
   , retrievedHumanCommentsRootNode;
   private List<Comment> agentComments = 
         new ArrayList<Comment>();
   private List<Comment> humanComments = 
         new ArrayList<Comment>();

   public CommentLibraryHandler(){

      retrievedAgentCommentsRootNode = 
            new ArrayList<Element>();
      retrievedHumanCommentsRootNode = 
            new ArrayList<Element>();
     
      //uncomment for this.main
      //importComments(); 
   }

   public void importComments(){

      SAXBuilder builder = new SAXBuilder();
      try {
         commentLibraryFile = new File(
               Utils.toURL("edu/wpi/sgf/resources/"+
                     CommentLibraryFilePath).toURI());
      } catch (MalformedURLException|URISyntaxException e) {
         System.out.println(
               "Resource loading error in loading Comment Library."
                     + "The .xml file(s) should be in edu/wpi/sgf/resources/ "
                     + "package which should be in the sgf classpath.");
         e.printStackTrace();
      }

      try{
         
         Document xmldoc = builder.build(commentLibraryFile);
         Element rootNode = xmldoc.getRootElement();

         retrievedAgentCommentsRootNode.addAll( 
               rootNode.getChild("agent").getChildren("comment"));
         retrievedHumanCommentsRootNode.addAll( 
               rootNode.getChild("human").getChildren("comment"));        

         parseComments("agent", retrievedAgentCommentsRootNode);
         parseComments("human", retrievedHumanCommentsRootNode);

      }catch(JDOMException e) {
         System.out.println("Comment library parse error.");
         e.printStackTrace();
      }catch(IOException e){
         System.out.println("Comment library load error.");
         e.printStackTrace();
      }

   }

   public void addTheseGameSpecificComments(
         Element rootNode){

      List<Element> retrievedGameSpecificAgentComments = 
            rootNode.getChild("agent").getChildren("comment");
      List<Element> retrievedGameSpecificHumanComments = 
            rootNode.getChild("human").getChildren("comment");  
      retrievedAgentCommentsRootNode.addAll(
            retrievedGameSpecificAgentComments);
      retrievedHumanCommentsRootNode.addAll(
            retrievedGameSpecificHumanComments);
   }

   /**
    * Parses the comments for any given player and puts them in
    * respected lists as Comment objects. 
    * Could be easily changed to create and return new list for 
    * unexpected new players if multi-player.
    * @param player
    * @param playerCommentsElements
    */
   private void parseComments(String player, List<Element> playerCommentsElements){

      GameCommentingState tempGameCmState = null;

      for(Element eachCommentElement : playerCommentsElements){

         String content;
         
         try{
         content= eachCommentElement.getChild("content").getTextTrim();
         }catch(Exception e){
            content = eachCommentElement.getTextTrim();
         }
         
         List<Element> responsesElements = eachCommentElement.getChildren("response");
         List<String> responses = new ArrayList<String>();
         
         for(Element el : responsesElements)
            responses.add(el.getTextTrim());

         Attribute tagsAtt = eachCommentElement.getAttribute("tags");
         Attribute madeOnAtt = eachCommentElement.getAttribute("madeOn");
         Attribute gameTypeAtt = eachCommentElement.getAttribute("gameType");
         Attribute gameNameAtt = eachCommentElement.getAttribute("gameName");
         Attribute gameCmStateAtt = eachCommentElement.getAttribute("gameState");
         Attribute strengthValueAtt = eachCommentElement.getAttribute("strength");
         Attribute competitivenessValueAtt = eachCommentElement.getAttribute("competitiveness");

         if(gameCmStateAtt != null){
            switch(gameCmStateAtt.getValue().toLowerCase().trim()){
               case "humanwon":
                  tempGameCmState = GameCommentingState.humanWon;
                  break;
               case "agentwon":
                  tempGameCmState = GameCommentingState.agentWon;
                  break;
               case "tie":
                  tempGameCmState = GameCommentingState.tie;
                  break;
            }
         }
         else 
            tempGameCmState = GameCommentingState.playing;

         String tags[] = null;
         String gameType = null, gameName = null, madeOn = null;
         Double strength = -1.0, competitiveness = -1.0; 
         if(tagsAtt != null) tags = tagsAtt.getValue().trim().split("/");
         if(strengthValueAtt != null) strength = 
               Double.valueOf(strengthValueAtt.getValue().trim());
         if(competitivenessValueAtt != null) competitiveness = 
               Double.valueOf(competitivenessValueAtt.getValue().trim());
         if(gameTypeAtt != null) gameType = gameTypeAtt.getValue().trim();
         if(gameNameAtt != null) gameName = gameNameAtt.getValue().trim();
         if(madeOnAtt != null) madeOn = madeOnAtt.getValue().trim();

         if(player.equals("agent"))
            agentComments.add( new Comment(
                  content, player, madeOn, tags, strength, 
                  competitiveness, gameType, gameName, 
                  tempGameCmState, responses));
         else if(player.equals("human"))
            humanComments.add( new Comment(
                  content, player, madeOn, tags,strength, 
                  competitiveness, gameType, gameName, 
                  tempGameCmState, responses));
      }

   }

   public List<Comment> getAgentComments(){return agentComments;}
   public List<Comment> getHumanComments(){return humanComments;}

   //utilities methods:

   public List<Comment> getAgentWinningCommentsAmong(
         List<Comment> someComments){
      List<Comment> agentWinningComments = 
            new ArrayList<Comment>();
      for(Comment eachComment : someComments)
         if(eachComment.getGameState()
               .equals(GameCommentingState.agentWon))
            agentWinningComments.add(eachComment);
      return agentWinningComments;
   }

   public List<Comment> getHumanWinningCommentsAmong(
         List<Comment> someComments){
      List<Comment> humanWinningComments = 
            new ArrayList<Comment>();
      for(Comment eachComment : someComments)
         if(eachComment.getGameState()
               .equals(GameCommentingState.humanWon))
            humanWinningComments.add(eachComment);
      return humanWinningComments;
   }

   public List<Comment> getTieCommentsAmong(
         List<Comment> someComments){
      List<Comment> tieComments = 
            new ArrayList<Comment>();
      for(Comment eachComment : someComments)
         if(eachComment.getGameState()
               .equals(GameCommentingState.tie))
            tieComments.add(eachComment);
      return tieComments;
   }

   public List<Comment> getStillPlayingCommentsAmong(
         List<Comment> someComments){
      List<Comment> playingComments = 
            new ArrayList<Comment>();
      for(Comment eachComment : someComments)
         if(eachComment.getGameState()
               .equals(GameCommentingState.playing))
            playingComments.add(eachComment);
      return playingComments;
   }

   public List<Comment> getGameSpecificsAmong(
         List<Comment> someComments, String gameName){
      List<Comment> gameSpecifics = 
            new ArrayList<Comment>();
      for(Comment eachComment : someComments)
         if(eachComment.getGameName().toLowerCase().trim()
               .equals(gameName.toLowerCase().trim()))
            gameSpecifics.add(eachComment);
      return gameSpecifics;
   }

   public List<Comment> getGameTypeSpecificsAmong(
         List<Comment> someComments, String gameType){
      List<Comment> gameTypeSpecifics = 
            new ArrayList<Comment>();
      for(Comment eachComment : someComments)
         if(eachComment.getGameType().toLowerCase().trim()
               .equals(gameType.toLowerCase().trim()))
            gameTypeSpecifics.add(eachComment);
      return gameTypeSpecifics;
   }

   public List<Comment> getCmForMakingOnPlayerAmong(
         List<Comment> someComments, String player){
      //player valid values: agent, human, both, null 
      //(null (no madeOn attribute) is assumed both)
      List<Comment> cmForMakingOnSomePlayer = 
            new ArrayList<Comment>();
      for(Comment eachComment : someComments)
         if(eachComment.getMadeOn().toLowerCase().trim()
               .equals(player.toLowerCase().trim()) ||
               eachComment.getMadeOn().toLowerCase().trim()
               .equals("both") || eachComment.getMadeOn() == null)
            cmForMakingOnSomePlayer.add(eachComment);
      return cmForMakingOnSomePlayer;
   }

   public List<Comment> getValidCommentsByStrength(
         List<Comment> someComments, double someMoveStrength){
      List<Comment> validComments = new ArrayList<Comment>();
      for(Comment eachComment : someComments)
         if(someMoveStrength < eachComment.getStrength() 
               + eachComment.getStrengthSafeMargin()
               && someMoveStrength > eachComment.getStrength() 
               - eachComment.getStrengthSafeMargin())
            validComments.add(eachComment);
      return validComments;
   }

   /**
    * This method aims to prioritize input comments by
    * tag covering criteria. More specific and accurate
    * tag coverings gives higher priority, valid game 
    * specific and gameType specific comments also get 
    * higher priorities. 
    * @param someComments
    * @param someTags
    * @param someGameSpecificTags
    * @return
    */
   public List<Comment> prioritizeByTags(
         List<Comment> someComments, List<String> someTags, 
         List<String> someGameSpecificTags, String someGameType){

      //b robust
      if(someGameSpecificTags != null)
         if(!someGameSpecificTags.isEmpty())
            for(String each : someGameSpecificTags){
               someGameSpecificTags.add(each.toLowerCase().trim());
               someGameSpecificTags.remove(each);
            }
      if(someTags != null)
         if(!someTags.isEmpty())
            for(String each : someTags){
               someTags.add(each.toLowerCase().trim());
               someTags.remove(each);
            }
      
      Map<Comment, Integer> genericCommentsTagCovering = 
            new HashMap<Comment, Integer>();
      Map<Comment, Integer> sortedGenericCommentsTagCovering = 
            new HashMap<Comment, Integer>();
      Map<Comment, Integer> gameSpecificCommentsTagCovering = 
            new HashMap<Comment, Integer>();
      Map<Comment, Integer> sortedGameSpecificCommentsTagCovering = 
            new HashMap<Comment, Integer>();
      Map<Comment, Integer> gameTypeSpecificCommentsTagCovering = 
            new HashMap<Comment, Integer>();
      @SuppressWarnings("unused")
      Map<Comment, Integer> sortedGameTypeSpecificCommentsTagCovering = 
      new HashMap<Comment, Integer>();

      List<Comment> results = new ArrayList<Comment>();
      int genericCovering = 0, gameSpecificCovering = 0;
      for(Comment eachComment : someComments){
         for(String eachTag : eachComment.getTags()){
            eachTag = eachTag.toLowerCase().trim();
            if(someTags != null)
               if(someTags.contains(eachTag))
                  genericCovering ++;
            if(someGameSpecificTags != null)
               if(someGameSpecificTags.contains(eachTag))
                  gameSpecificCovering ++;
         }
         if(eachComment.getGameName() != null 
               && gameSpecificCovering > 0)
            gameSpecificCommentsTagCovering.put(
                  eachComment, genericCovering + gameSpecificCovering);
         else if(eachComment.getGameType() != null 
               && eachComment.getGameType() == someGameType
               && eachComment.getGameName() == null)
            gameTypeSpecificCommentsTagCovering.put(
                  eachComment, genericCovering);
         else if(eachComment.getGameName() == null)
            genericCommentsTagCovering.put(
                  eachComment, genericCovering);

         genericCovering = gameSpecificCovering = 0;
      }
      /*
      //refining

      Map<Comment, Integer> genericMaxCoverings = 
            new HashMap<Comment, Integer>();
      Map<Comment, Integer> gameSpecificMaxCoverings = 
            new HashMap<Comment, Integer>();
      Map<Comment, Integer> gameTypeSpecificMaxCoverings = 
            new HashMap<Comment, Integer>();
      int genericMaxCover = 0,
      gameSpecificMaxCover = 0,
      gameTypeSpecificMaxCover = 0;
      boolean noCoveringForAnyGenericComment = false,
      noCoveringForAnyGameSpecificComment = false,
      noCoveringForAnyGameTypeSpecificComment = false;

      //getting maxCovering values
      genericMaxCover = Collections.max(
            genericCommentsTagCovering.values());
      gameSpecificMaxCover = Collections.max(
            gameSpecificCommentsTagCovering.values());
      gameTypeSpecificMaxCover = Collections.max(
            gameTypeSpecificCommentsTagCovering.values());


      //getting max covering comments
      if(genericMaxCover != 0){
         for(Entry<Comment, Integer> each 
               : genericCommentsTagCovering.entrySet())
            if(each.getValue() >= genericMaxCover)
               genericMaxCoverings.put(
                     each.getKey(), each.getValue());
      }else{noCoveringForAnyGenericComment = true;}
      if(gameSpecificMaxCover != 0){
         for(Entry<Comment, Integer> each 
               : gameSpecificCommentsTagCovering.entrySet())
            if(each.getValue() >= gameSpecificMaxCover)
               gameSpecificMaxCoverings.put(
                     each.getKey(), each.getValue());
      }else{noCoveringForAnyGameSpecificComment = true;}
      if(gameTypeSpecificMaxCover != 0){
         for(Entry<Comment, Integer> each 
               : gameTypeSpecificCommentsTagCovering.entrySet())
            if(each.getValue() >= gameTypeSpecificMaxCover)
               gameTypeSpecificMaxCoverings.put(
                     each.getKey(), each.getValue());
      }else{noCoveringForAnyGameTypeSpecificComment = true;}


      if(!noCoveringForAnyGameSpecificComment)
         results.addAll(gameSpecificMaxCoverings.keySet());
      else if(!noCoveringForAnyGameTypeSpecificComment)
         results.addAll(gameTypeSpecificMaxCoverings.keySet());
      else if(!noCoveringForAnyGenericComment)
         results.addAll(genericMaxCoverings.keySet());*/


      //sorting the generic map based on values by Guava
      //      Ordering<Comment> valueComparator = Ordering.natural()
      //            .onResultOf(Functions.forMap(
      //                  genericCommentsTagCovering)).reverse();
      //      sortedGenericCommentsTagCovering = 
      //            ImmutableSortedMap.copyOf(
      //                  genericCommentsTagCovering, valueComparator);


      sortedGenericCommentsTagCovering.putAll(genericCommentsTagCovering);//temp
      //sorting the game specific comments' map too
      //      Ordering<Comment> anotherValueComparator = Ordering.natural()
      //            .onResultOf(Functions.forMap(
      //                  gameSpecificCommentsTagCovering))
      //                  .compound(Ordering.<Comment> natural()).reverse();
      //      sortedGameSpecificCommentsTagCovering = 
      //            ImmutableSortedMap.copyOf(
      //                  gameSpecificCommentsTagCovering, anotherValueComparator);
      sortedGameSpecificCommentsTagCovering.putAll(gameSpecificCommentsTagCovering);//temp
      //sorting the game type specific comments' map too
      //      Ordering<Comment> yetAnotherValueComparator = Ordering.natural()
      //            .onResultOf(Functions.forMap(
      //                  gameTypeSpecificCommentsTagCovering))
      //                  .compound(Ordering.<Comment> natural()).reverse();
      //      sortedGameTypeSpecificCommentsTagCovering = 
      //            ImmutableSortedMap.copyOf(
      //                  gameTypeSpecificCommentsTagCovering, yetAnotherValueComparator);

      //getting the max covering
      //      int genericMaxCover = (int) 
      //            sortedGenericCommentsTagCovering.values().toArray()[0];
      //      int gameSpecificMaxCover = (int) 
      //            sortedGameSpecificCommentsTagCovering.values().toArray()[0];
      //      int gameTypeSpecificMaxCover = (int) 
      //            sortedGameTypeSpecificCommentsTagCovering.values().toArray()[0];

      //filling in the results list by gameName comments, and if empty, game types
      //and if empty again, generic. In all cases only those with max covering values.
      if(!sortedGameSpecificCommentsTagCovering.isEmpty()){
         for(Entry<Comment, Integer> each 
               : sortedGameSpecificCommentsTagCovering.entrySet())
            // if(each.getValue() >= gameSpecificMaxCover)
            results.add(each.getKey());
      }
      //      else if(!sortedGameTypeSpecificCommentsTagCovering.isEmpty()){
      //         for(Entry<Comment, Integer> each 
      //               : sortedGameTypeSpecificCommentsTagCovering.entrySet())
      //            if(each.getValue() >= gameTypeSpecificMaxCover)
      //               results.add(each.getKey());
      //      }
      else{
         for(Entry<Comment, Integer> each 
               : sortedGenericCommentsTagCovering.entrySet())
            //if(each.getValue() >= genericMaxCover)
            if(!each.getKey().getTags().contains("regret"))
               results.add(each.getKey());
      }

      return results;

   }


   public static List<String> getContentsOfTheseComments(
         List<Comment> someComments){
      List<String> contents = new ArrayList<String>();
      for(Comment eachComment: someComments)
         contents.add(eachComment.getContent());
      return contents;
   }

   //A main method for testing only
   public static void main(String[] args) {
      CommentLibraryHandler cmlibh = 
            new CommentLibraryHandler();
      for(Comment cm : cmlibh.getAgentComments()){
         System.out.println(cm.getContent() + " >> " 
               + cm.getStrength() + " - "
               + cm.getComepetitiveness() + " - "
               + cm.getTags().get(0) + " - "
               + cm.getGameType() + " - "
               + cm.getGameName() + " - "
               + cm.getGameState() + " - "
               + cm.getMadeOn());
         for(String each : cm.getPossibleResponses())
            System.out.print(" >>> " + each);
         System.out.println();
      }
      System.out.println("\n---\n");
      for(Comment cm : cmlibh.getHumanComments()){
         System.out.println(cm.getContent() + " >> " 
               + cm.getStrength() + " - "
               + cm.getComepetitiveness() + " - "
               + cm.getTags().get(0) + " - "
               + cm.getGameType() + " - "
               + cm.getGameName() + " - "
               + cm.getGameState() + " - "
               + cm.getMadeOn());
         for(String each : cm.getPossibleResponses())
            System.out.print(" >>> " + each);
         System.out.println();
      }
   }

}

