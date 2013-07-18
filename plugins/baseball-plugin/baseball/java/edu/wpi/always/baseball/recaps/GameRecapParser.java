package edu.wpi.always.baseball.recaps;

/**
 * This class accesses espn's game recap pages given a link and returns a game recap of the game.
 * 
 * @author Frederik Clinckemaillie
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.io.*;
import java.util.*;

public class GameRecapParser {

   // Will contain the full html source code for the recap file.

   Document doc;

   /* GAME SCORE VARIABLES */
   // private String fanTeam = "Red Sox";
   private int numberOfInnings;

   private String otherTeam = "";

   private int[] score = new int[2];

   private int[] hits = new int[2];

   @SuppressWarnings("unchecked")
   private List<Integer>[] scoreByInnings = new List[2];
   {
      scoreByInnings[0] = new ArrayList<Integer>();
      scoreByInnings[1] = new ArrayList<Integer>();
   }

   // private boolean wasGameClose;
   // If the team was away, then it shows up first, and is in the first value(0)
   // of all arrays.
   private boolean wasBostonAway;

   /* TYPE OF GAME VARIABLES */
   /*
    * private int typeOfGame; 0-Perfect Game 1-No-Hit Game 2-Low Scoring
    * Game/Pitcher Duel 3-High Scoring Game
    */

   /* HOME RUNS */
   // First array location contains the name of the hitter, second contains the
   // number of hits
   private ArrayList<String[]> homerunHitters = new ArrayList<String[]>();

   int otherTeamHomerunCount;

   /* RBI */
   // First array location contains the name of the hitter, second contains the
   // number of hits
   private ArrayList<String[]> RBIHitters = new ArrayList<String[]>();

   /* Pitchers */
   private ArrayList<String[]> BosPitchers = new ArrayList<String[]>();

   /*
    * 0-pitcher name 1-Innings Played 2-Hits 3-Runs 4-Walk 5-ERA
    */
   private ArrayList<String[]> otherPitchers = new ArrayList<String[]>();

   /* Injury */
   // private ArrayList<String> playersInjured = new ArrayList<String>();

   public GameRecapParser (String recapURL) throws IOException {
      /**
       * Online
       */
      doc = Jsoup.connect(recapURL).data("query", "Java").userAgent("Mozilla")
            .cookie("auth", "token").timeout(10000).get();

      /**
       * Offline
       */
      /*
       * String newRecapURL = recapURL.substring(recapURL.indexOf("gameId="));
       * String fileName = "OfflineData_GameRecap/"; newRecapURL =
       * fileName.concat(newRecapURL); File input = new File(newRecapURL); doc =
       * Jsoup.parse(input, "UTF-8"); String title = doc.title();
       */

      numberOfInnings = Integer.parseInt(findNumberOfInnings());
      setOtherTeam();
      setInningsAndScore();
      setHRAndRBI();
      setPitcherStats(true);
      setPitcherStats(false);

   }

   public GameRecapParser (String recapURL, int useFiles) throws IOException {
      if ( useFiles == 0 ) {// Online
         doc = Jsoup.connect(recapURL).data("query", "Java")
               .userAgent("Mozilla").cookie("auth", "token").timeout(10000)
               .get();
      } else {// Offline
         String newRecapURL = recapURL.substring(recapURL.indexOf("gameId="));
         String fileName = "OfflineFiles/";
         newRecapURL = fileName.concat(newRecapURL);

         File input = new File(newRecapURL);
         doc = Jsoup.parse(input, "UTF-8");
         // String title = doc.title();
      }

      numberOfInnings = Integer.parseInt(findNumberOfInnings());
      setOtherTeam();
      setInningsAndScore();
      setHRAndRBI();
      setPitcherStats(true);
      setPitcherStats(false);

   }

   /**
    * This method finds who hit homeruns and RBIs for the Red Sox this game.
    */
   private void setHRAndRBI () {
      Elements links = doc.select("td[colspan][class][style");
      boolean isVisitors = true;
      String parsedBatting = "";
      for (Element link : links) {
         if ( link.toString().indexOf(">BATTING<") != -1 )
            if ( isVisitors && wasBostonAway )
               parsedBatting = parseString(link.toString());
            else if ( !isVisitors && !wasBostonAway )
               parsedBatting = parseString(link.toString());
         isVisitors = false;

      }
      if ( parsedBatting.indexOf("HR:") > -1 )
         parseNames(parsedBatting.substring(parsedBatting.indexOf("HR:") + 3),
               "HR");
      if ( parsedBatting.indexOf("RBI:") > -1 )
         parseNames(parsedBatting.substring(parsedBatting.indexOf("RBI:") + 4),
               "RBI");
   }

   /**
    * Helper Function that takes in a string containing names, parses it, and
    * places the names in the right location depending on the type, as defined
    * by type.
    * 
    * @param str, the input string.
    * @param type, defines what type of statistic it is, HR or RBI
    */
   private void parseNames (String str, String type) {
      int index = 0;
      int indexOfEnd = str.indexOf(':');
      int indexOfNumber = 0;
      String numbers = "0123456789";
      String name;

      while (str.indexOf(')') + 1 < indexOfEnd && str.indexOf(')') > -1) {
         if ( !str.substring(0, 5).equals("2-out") ) {
            if ( str.indexOf('(') > 0 && str.indexOf(',') > 0 )
               name = str.substring(0,
                     Math.min(str.indexOf('('), str.indexOf(','))).trim();
            else if ( str.indexOf('(') > 0 )
               name = str.substring(0, str.indexOf('(')).trim();
            else if ( str.indexOf(',') > 0 )
               name = str.substring(0, str.indexOf(',')).trim();
            else
               name = str;

            indexOfNumber = 0;
            index = 0;
            for (int count = name.length() - 1; count >= 0; count--) {
               if ( numbers.indexOf(name.charAt(count)) != -1 )
                  indexOfNumber = count;
            }
            String[] temp = new String[2];
            if ( indexOfNumber == 0 ) {
               temp[0] = name;
               temp[1] = "1";
            } else {
               temp[0] = name.substring(0, indexOfNumber);
               temp[1] = name.substring(indexOfNumber);
            }
            if ( type.equals("HR") )
               homerunHitters.add(temp);
            else if ( type.equals("RBI") )
               RBIHitters.add(temp);
         }
         index += str.indexOf(')') + 1;
         if ( str.charAt(index) == ';' || str.charAt(index) == ','
            || str.charAt(index) == ')' )
            index++;
         str = str.substring(index);
         indexOfEnd -= index;
      }
   }

   /**
    * This method finds the names and stats of the pitchers who pitched in that
    * game.
    */
   private void setPitcherStats (boolean Boston) {
      Elements tables = doc.select("table[border][width][class]");
      for (Element table : tables) {
         /**
          * Boston Red Sox
          */
         /*
          * if(parseString(table.select("thead").toString()).indexOf("Pitchers")
          * != -1 && ((Boston &&
          * parseString(table.select("thead").toString()).indexOf
          * ("Boston Red Sox") != -1 ) || (!Boston &&
          * parseString(table.select("thead"
          * ).toString()).indexOf("Boston Red Sox") == -1 )))
          */
         /**
          * New York Yankees
          */
         if ( parseString(table.select("thead").toString()).indexOf("Pitchers") != -1
            && ((Boston && parseString(table.select("thead").toString())
                  .indexOf("New York Yankees") != -1) || (!Boston && parseString(
                  table.select("thead").toString()).indexOf("New York Yankees") == -1)) ) {
            Elements pitchersEle = table.select("tbody").get(0).select("tr");
            for (Element pitcher : pitchersEle) {
               String[] aPitcher = new String[6];
               String pitcherStats = (parseString(pitcher.toString()));
               // Pitcher name
               if ( pitcherStats.indexOf('(') > 0 ) {
                  aPitcher[0] = pitcherStats.substring(0,
                        pitcherStats.indexOf('('));
                  pitcherStats = pitcherStats.substring(pitcherStats
                        .indexOf(')') + 2) + " ";
               } else {
                  aPitcher[0] = pitcherStats.substring(0,
                        pitcherStats.indexOf('.') - 1);
                  pitcherStats = pitcherStats.substring(pitcherStats
                        .indexOf('.') - 1) + " ";
               }
               for (int index = 1; index < 10; index++) {
                  if ( index < 4 ) {
                     // Stores Pitcher Innings, Runs, and Hits
                     aPitcher[index] = pitcherStats.substring(0,
                           pitcherStats.indexOf(' '));
                  } else if ( index == 5 ) {
                     // Stores pitcher walks
                     aPitcher[4] = pitcherStats.substring(0,
                           pitcherStats.indexOf(' '));
                  } else if ( index == 9 ) { // Stores pitcher ERA
                     aPitcher[5] = pitcherStats.substring(0,
                           pitcherStats.indexOf(' '));
                  }
                  pitcherStats = pitcherStats.substring(pitcherStats
                        .indexOf(' ') + 1);
               }
               if ( Boston )
                  BosPitchers.add(aPitcher);
               else
                  otherPitchers.add(aPitcher);
            }
         }
      }
   }

   /**
    * This method finds the team the Red Sox are playing in this game and sets
    * the name to the otherTeam variable. It also sets the wasBostonAway
    * variable to mark whether or not Boston was the away team.
    * 
    * @param doc the document containing the source code of the game recap
    */
   private void setOtherTeam () {
      Elements links = doc.getElementsByClass("team-info");
      boolean hasBeenSet = false;
      for (Element link : links) {
         // if(parseString(link.toString()).substring(0,9).equals(" Red Sox "))
         if ( parseString(link.toString()).substring(0, 9).equals(" Yankees ") ) {
            if ( !hasBeenSet ) {
               hasBeenSet = true;
               wasBostonAway = true;
            }
         } else {
            if ( !hasBeenSet ) {
               hasBeenSet = true;
               wasBostonAway = false;
            }
            String sContainsName = parseString(link.toString());
            String numbers = "0123456789";
            int index = 1;
            while (numbers.indexOf(sContainsName.charAt(index)) == -1) {
               otherTeam += sContainsName.charAt(index);
               index++;
            }
         }
      }
   }

   /**
    * This method finds the winner of the game and how many points were scored
    * in the game and stores that information in the scoreByInnings variable.
    */
   private void setInningsAndScore () {
      int index = doc.toString().indexOf("<td style=\"text-align:center\">");
      String unparsedScores = doc.toString().substring(index, index + 1700);
      {
         // Important: ESPN doesnt keep score for more than 10 innings. Starting
         // innings are lost first. Use score.
         for (int team = 0; team < 2; team++) {
            for (int inning = 0; inning < numberOfInnings && inning < 11; inning++) {
               index = unparsedScores
                     .indexOf("<td style=\"text-align:center\">");
               if ( parseString(unparsedScores.substring(index, index + 37))
                     .indexOf('-') != -1 )
                  scoreByInnings[team].add(new Integer(0));
               else
                  scoreByInnings[team].add(Integer.parseInt(parseString(
                        unparsedScores.substring(index, index + 37)).trim()));

               unparsedScores = unparsedScores.substring(index + 37);

            }
            index = unparsedScores.indexOf("<td style=\"font-weight:bold;\">");
            score[team] = Integer.parseInt(parseString(
                  unparsedScores.substring(index, index + 37)).trim());
            unparsedScores = unparsedScores.substring(index + 37);

            index = unparsedScores.indexOf("<td style=\"font-weight:bold;\">");
            hits[team] = Integer.parseInt(parseString(
                  unparsedScores.substring(index, index + 37)).trim());

         }
      }
   }

   /**
    * This method finds the length of the game.
    * 
    * @return the number of innings the game lasted
    */
   private String findNumberOfInnings () {
      String tostring = doc.toString();
      int index = tostring
            .indexOf("td style=\"font-weight:bold; background-color:#d8d8d8;\"");
      if ( tostring.substring(index - 23, index - 22).equals("1") )
         return tostring.substring(index - 23, index - 21);
      return tostring.substring(index - 22, index - 21);
   }

   /**
    * This method takes in a string and removes all writing found between <>
    * signs. This removes all the html code found in the string and leaves all
    * the valuable information.
    * 
    * @param in, an unparsed string
    * @return a parsed string
    */
   public static String parseString (String in) {
      int isInBrackets = 0;
      String out = "";
      for (int count = 0; count < in.length(); count++) {
         if ( in.charAt(count) == '<' )
            isInBrackets++;
         if ( in.charAt(count) == '>' )
            isInBrackets--;
         if ( isInBrackets == 0 && in.charAt(count) != '>'
            && in.charAt(count) != '\n' )
            out += in.charAt(count);
      }
      return out;
   }

   /**
    * This method creates the final recap string from the information stored in
    * the class variables.
    */
   @Override
   public String toString () {
      return "\n" + createRecap();
   }

   /**
    * Helper function to the toString function which creates the recap.
    * 
    * @return the recap string
    */
   private String createRecap () {
      String recap = "";

      /* Scenario 1: Perfect Game or Shutout */
      if ( score[0] == 0 || score[1] == 0 ) {
         recap += createShutoutAndPerfectRecap();
      }

      /* Scenario 2: Victory */

      else if ( (wasBostonAway && score[0] > score[1])
         || (!wasBostonAway && score[1] > score[0]) ) {
         /*
          * //Pseudo random to make the beginning not always be the same.
          * if((score[0]+score[1]) /4 == ((double)(score[0]+score[1])) /4) recap
          * = "Boston"; else recap = "The Red Sox"; recap+= " beat the "+
          * otherTeam.trim()+" " + Math.max(score[1],score[0]) + " to "+
          * Math.min(score[1],score[0]); //Boston's score is low, and the score
          * differential is low as well if((wasBostonAway && score[1]< 3
          * ||!wasBostonAway && score[0]<3)&&(Math.abs(score[1]-score[0])<3)) {
          * recap += " in a close pitcher duel. "+
          * BosPitchers.get(0)[0].trim()+" defeated the "
          * +otherTeam+" bullpen, letting only "+ BosPitchers.get(0)[3];
          * if(!BosPitchers.get(0)[3].trim().equals("1")) recap+=" runs "; else
          * recap+=" run "; recap+= "and "+BosPitchers.get(0)[4];
          * if(!BosPitchers.get(0)[4].trim().equals("1")) recap+=" hits. "; else
          * recap+=" hit. "; recap+=
          * "  The offense managed to get just enough runs in to take the game, with "
          * ; } //Very high score differential. else if
          * ((Math.abs(score[1]-score[0])>5)) recap+=
          * " in a game that rapidly turned into a rout.  The Red Sox offense dominated the "
          * + otherTeam.trim()+", with "; else if (wasBostonAway && score[1]< 5
          * ||!wasBostonAway && score[0]<5) recap+=
          * " due to a solid performance from "+ BosPitchers.get(0)[0].trim() +
          * " and the Red Sox offense, which had "; else { recap+=
          * " in a close game.  The Red Sox offense performed as needed, with ";
          * } recap +=createHRAndRBIRecap(true);
          */

         // Pseudo random to make the beginning not always be the same.
         if ( (score[0] + score[1]) / 4 == ((double) (score[0] + score[1])) / 4 )
            recap = "Yankees";
         else
            recap = "The New York Yankees";

         recap += " beat the " + otherTeam.trim() + " "
            + Math.max(score[1], score[0]) + " to "
            + Math.min(score[1], score[0]);
         // Boston's score is low, and the score differential is low as well
         if ( (wasBostonAway && score[1] < 3 || !wasBostonAway && score[0] < 3)
            && (Math.abs(score[1] - score[0]) < 3) ) {
            recap += " in a close pitcher duel. "
               + BosPitchers.get(0)[0].trim() + " defeated the " + otherTeam
               + " bullpen, letting only " + BosPitchers.get(0)[3];
            if ( !BosPitchers.get(0)[3].trim().equals("1") )
               recap += " runs ";
            else
               recap += " run ";

            recap += "and " + BosPitchers.get(0)[4];

            if ( !BosPitchers.get(0)[4].trim().equals("1") )
               recap += " hits. ";
            else
               recap += " hit. ";

            recap += "  The offense managed to get just enough runs in to take the game, with ";
         }
         // Very high score differential.
         else if ( (Math.abs(score[1] - score[0]) > 5) )
            recap += " in a game that rapidly turned into a rout.  The Yankees offense dominated the "
               + otherTeam.trim() + ", with ";

         else if ( wasBostonAway && score[1] < 5 || !wasBostonAway
            && score[0] < 5 )
            recap += " due to a solid performance from "
               + BosPitchers.get(0)[0].trim()
               + " and the Yankees offense, which had ";
         else {
            recap += " in a close game.  The Yankees offense performed as needed, with ";
         }
         recap += createHRAndRBIRecap(true);

      }

      /* Scenario 3: Loss */

      else {
         /*
          * if((score[0]+score[1]) /2 == ((double)(score[0]+score[1])) /2) recap
          * = "Boston"; else recap = "The Red Sox";
          * if((Math.abs(score[1]-score[0])>5)) recap+= " were routed by the "+
          * otherTeam+ Math.min(score[1],score[0]) + " to "+
          * Math.max(score[1],score[0])
          * +". The Boston bullpen were not able to keep up with the " +
          * otherTeam+ " offense"; else recap += " lost to the "+ otherTeam+
          * Math.min(score[1],score[0]) + " to "+ Math.max(score[1],score[0]);
          * // Innings if(numberOfInnings> 9) recap += " in a close " +
          * numberOfInnings+ "-inning game."; else recap+= ". ";
          * if(Math.min(score[1],score[0])<5 &&Math.min(score[1],score[0])>0 )
          * recap+=
          * "  The Red Sox offense was underwhelming this game, with only ";
          * else if (homerunHitters.size()>0) recap+=
          * "  The Red Sox offense was still able to have a few solid hits, with "
          * ; recap+= createHRAndRBIRecap(false);
          */

         if ( (score[0] + score[1]) / 2 == ((double) (score[0] + score[1])) / 2 )
            recap = "Yankees";
         else
            recap = "The New York Yankees";

         if ( (Math.abs(score[1] - score[0]) > 5) )
            recap += " were routed by the " + otherTeam
               + Math.min(score[1], score[0]) + " to "
               + Math.max(score[1], score[0])
               + ". The Yankees bullpen were not able to keep up with the "
               + otherTeam + " offense";
         else
            recap += " lost to the " + otherTeam + Math.min(score[1], score[0])
               + " to " + Math.max(score[1], score[0]);

         // Innings
         if ( numberOfInnings > 9 )
            recap += " in a close " + numberOfInnings + "-inning game.";
         else
            recap += ". ";

         if ( Math.min(score[1], score[0]) < 5
            && Math.min(score[1], score[0]) > 0 )
            recap += "  The Yankees offense was underwhelming this game, with only ";
         else if ( homerunHitters.size() > 0 )
            recap += "  The Yankees offense was still able to have a few solid hits, with ";

         recap += createHRAndRBIRecap(false);

      }

      return recap;
   }

   private String createShutoutAndPerfectRecap () {
      String recap = "";
      // Boston Wins
      if ( (wasBostonAway && score[0] > score[1])
         || (!wasBostonAway && score[1] > score[0]) ) {
         // Boston had a perfect game.
         if ( noWalk(BosPitchers)
            && ((wasBostonAway && hits[1] == 0) || (!wasBostonAway && hits[0] == 0)) ) {

            if ( Double.parseDouble(BosPitchers.get(0)[1]) == numberOfInnings )// One
                                                                               // pitcher
                                                                               // played
                                                                               // the
                                                                               // entire
                                                                               // game.
            {
               /*
                * recap += BosPitchers.get(0)[0].trim() +
                * " had a perfect game in the Red Sox's " +
                * Math.max(score[1],score[0]) + " to "+
                * Math.min(score[1],score[0]) + "victory over the "+ otherTeam
                * +".";
                */
               recap += BosPitchers.get(0)[0].trim()
                  + " had a perfect game in the Yankees' "
                  + Math.max(score[1], score[0]) + " to "
                  + Math.min(score[1], score[0]) + "victory over the "
                  + otherTeam + ".";

               if ( Math.abs(score[0] - score[1]) < 4 || numberOfInnings > 9 ) {
                  recap += "  The game was an intense pitching duel until the end, with "
                     + BosPitchers.get(0)[0].trim()
                     + "  beating the "
                     + otherTeam + " bullpen";
               }
            } else// More than one pitcher played the entire game
            {
               /*
                * recap +=
                * "The Red Sox bullpen had a perfect game in the Red Sox's " +
                * Math.max(score[1],score[0]) + " to "+
                * Math.min(score[1],score[0]) + "victory over the "+ otherTeam
                * +".";
                */
               recap += "The Yankees bullpen had a perfect game in the Yankees' "
                  + Math.max(score[1], score[0])
                  + " to "
                  + Math.min(score[1], score[0])
                  + "victory over the "
                  + otherTeam + ".";

               if ( Math.abs(score[0] - score[1]) < 4 || numberOfInnings > 9 ) {
                  recap += "  The game was an intense pitching duel until the end, with Boston's pitching beating the "
                     + otherTeam + " bullpen";
               }
            }
         } else
            /*
             * recap += "The Red Sox shut out the  "+ otherTeam +" " +
             * Math.max(score[1],score[0]) + " to "+
             * Math.min(score[1],score[0])+" due to an amazing" +
             * " performance from "+BosPitchers.get(0)[0]+", who allowed only ";
             */
            recap += "The Yankees shut out the  " + otherTeam + " "
               + Math.max(score[1], score[0]) + " to "
               + Math.min(score[1], score[0]) + " due to an amazing"
               + " performance from " + BosPitchers.get(0)[0]
               + ", who allowed only ";

         if ( BosPitchers.get(0)[4].trim().equals("1") )
            recap += BosPitchers.get(0)[4] + " walk and ";
         else if ( !BosPitchers.get(0)[4].trim().equals("0") )
            recap += BosPitchers.get(0)[4] + " walks and ";

         if ( BosPitchers.get(0)[2].trim().equals("1") )
            recap += BosPitchers.get(0)[2] + " hit";
         else
            recap += BosPitchers.get(0)[2] + " hits";

         if ( Math.abs(score[0] - score[1]) < 4 || numberOfInnings > 9 ) {
            if ( numberOfInnings > 9 )
               recap += " after " + numberOfInnings + " innings.";
            else
               recap += ".";
         }

         // recap+="  The Red Sox offense secured the victory with ";
         recap += "  The Yankees offense secured the victory with ";
         recap += createHRAndRBIRecap(true);

      } else // The other team had a perfect game.
      {
         /*
          * recap+= "  The Red Sox lost to the "+ otherTeam+ " " +
          * Math.min(score[1],score[0]) + " to "+
          * Math.max(score[1],score[0])+" due of an impressive performance " +
          * "from "+otherPitchers.get(0)[0]+ " who "; if(noWalk(otherPitchers)&&
          * ((wasBostonAway && hits[0] == 0) || (!wasBostonAway && hits[1]
          * ==0))) recap+= "gave his team a perfect game."; else
          * recap+="shutout the Boston offense.";
          */
         recap += "  The Yankees lost to the " + otherTeam + " "
            + Math.min(score[1], score[0]) + " to "
            + Math.max(score[1], score[0])
            + " due of an impressive performance " + "from "
            + otherPitchers.get(0)[0] + " who ";
         if ( noWalk(otherPitchers)
            && ((wasBostonAway && hits[0] == 0) || (!wasBostonAway && hits[1] == 0)) )
            recap += "gave his team a perfect game.";
         else
            recap += "shutout the New York Yankees offense.";

      }
      return recap;
   }

   private boolean noWalk (ArrayList<String[]> pitchers) {
      boolean perfect = true;
      for (String[] pitcher : pitchers) {
         if ( !pitcher[4].trim().equals("0") )
            perfect = false;
      }
      return perfect;
   }

   private String createHRAndRBIRecap (boolean soxWin) {
      String recap = "";
      if ( homerunHitters.size() > 0 ) {
         recap += " ";
         for (int index = 0; index < homerunHitters.size(); index++) {
            String[] hH = homerunHitters.get(index);
            recap += hH[1].trim();
            if ( !hH[1].trim().equals("1") )
               recap += " homeruns from ";
            else
               recap += " homerun from ";
            recap += hH[0].trim();
            if ( index < homerunHitters.size() - 2 )
               recap += ", ";
            else if ( index < homerunHitters.size() - 1
               && homerunHitters.size() != 1 )
               recap += ", and ";
            else
               recap += ".";

         }
         recap = recap.substring(0, recap.length() - 1) + ".  ";
         if ( homerunHitters.size() > 1 )
            recap += "\n These homeruns were part of the ";
         else
            recap += "\n That homerun was part of the ";

         if ( soxWin ) {
            recap = recap + (Math.max(score[0], score[1]));
            if ( (Math.max(score[0], score[1])) > 1 )
               recap += " runs";
            else
               recap += " run";
            // recap+=" scored by the Red Sox offense to secure the win: ";
            recap += " scored by the Yankees offense to secure the win: ";
         } else {
            recap = recap + (Math.min(score[0], score[1]));
            if ( (Math.min(score[0], score[1])) > 1 )
               recap += " runs";
            else
               recap += " run";
            // recap+=" scored by the Red Sox offense: ";
            recap += " scored by the Yankees offense: ";
         }

      } else {
         recap += "the following players scoring runs: ";
      }

      /* RBI */
      if ( RBIHitters.size() > 0 ) {
         for (int index = 0; index < RBIHitters.size(); index++) {
            if ( RBIHitters.size() != 1 && RBIHitters.size() == index + 1 )
               recap += "and";

            String[] RBIHitter = RBIHitters.get(index);
            if ( !RBIHitter[0].equals("") ) {
               recap += " " + RBIHitter[0] + " scored " + RBIHitter[1];
               if ( RBIHitter[1].equals("1") )
                  recap += " run, ";
               else
                  recap += " runs, ";
            }
         }
         recap = recap.substring(0, recap.length() - 2) + ".";
      } else
         // recap+=" \nThe Red Sox did not score any runs.";
         recap += " \nThe Yankees did not score any runs.";

      return recap;
   }

   /**
    * This method returns the score of the game in the form of an array. [0]
    * contains boston's score. [1] contains the other team's score
    * 
    * @return
    */
   public int[] getScore () {
      int[] gameScore = new int[2];

      if ( wasBostonAway )
         return score;
      gameScore[0] = score[1];
      gameScore[1] = score[0];
      return gameScore;
   }

   /**
    * This function returns a line commenting on the action that happened in a
    * game depending on what type of game it was
    * 
    * @param bosScore: the number of runs boston scored in the game.
    * @param otherScore: the number of runs the other team scored in the game.
    * @return
    */
   public static String getTypeOfGameString (int bosScore, int otherScore,
         String otherTeam) {
      String returnString = "";
      if ( wasGameClose(bosScore, otherScore) ) {
         if ( wasGameLowScoring(bosScore, otherScore) )
            returnString += "It was a great pitcher duel.";
         else
            returnString += " It had a lot of runs from both sides.";

         if ( bosScore > otherScore )
            // returnString+=" I was glad to see the Red Sox victorious at the end.";
            returnString += " I was glad to see the Yankees victorious at the end.";
         else
            returnString += " Its a shame the Red Sox lost.  It was a close game.";

      } else {
         if ( bosScore > otherScore )
            // returnString+=" It was nice to see the Red Sox playing so well.  They destroyed the "
            // +otherTeam+".";
            returnString += " It was nice to see the Yankees playing so well.  They destroyed the "
               + otherTeam + ".";
         else
            // returnString+="It was a fun game, but it is a shame that the Red Sox fell appart. I thought they had a good shot at beating the "+otherTeam+".";
            returnString += "It was a fun game, but it is a shame that the Yankees fell appart. I thought they had a good shot at beating the "
               + otherTeam + ".";
      }
      return returnString;
   }

   /**
    * This method determines if a game was a low scoring game, defined as a game
    * with 6 or less points scored.
    * 
    * @param bosScore
    * @param otherScore
    * @return
    */
   private static boolean wasGameLowScoring (int bosScore, int otherScore) {
      if ( bosScore + otherScore > 6 )
         return false;
      return true;
   }

   /**
    * Returns if a game was close or not, which is defined by the difference in
    * score by the end of the game.
    * 
    * @param bosScore
    * @param otherScore
    * @return
    */
   private static boolean wasGameClose (int bosScore, int otherScore) {
      if ( Math.abs(bosScore - otherScore) > 3 && (bosScore + otherScore) < 6 )
         return false;
      else if ( Math.abs(bosScore - otherScore) > 4 )
         return false;
      return true;
   }

}
