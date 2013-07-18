package edu.wpi.always.baseball.standings;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.io.*;
import java.net.URL;
import java.util.*;

public class StandingsParser {

   private Team[] teams = new Team[5];

   public ArrayList<MatchofPlayoffs> playoffsMatch = new ArrayList<MatchofPlayoffs>();

   /**
    * 0: Regular Season 1: Division Series 2: League Champion Series 3: World
    * Series
    */
   public int seasontype = 0;

   /**
    * Whether Yankees' season is over or not true: over false: not over
    */
   public boolean yankeesSeason = false;

   public String showSeasonType = "";

   public String playoffswithYankees = "";

   public String DSwithGeneral = "";

   public String LCwithGeneral = "";

   public String WSwithGeneral = "";

   /*
    * Team Legend Team 0 bostonRedSox; Team 1 newYorkYankees; Team 2
    * tampaBayRays; Team 3 baltimoreOrioles; Team 4 torontoBlueJays;
    */

   // Check playoff data
   public Document docCheck;

   public StandingsParser () throws IOException {
      /**
       * Onlines
       */
      Document doc = Jsoup
            .connect("http://espn.go.com/mlb/standings/_/year/2011")
            .data("query", "Java").userAgent("Mozilla").cookie("auth", "token")
            .timeout(10000).get();

      /**
       * Offline
       */
      // File input = new File("standings.html");
      // Document doc = Jsoup.parse(input, "UTF-8");

      // Start of checking playoff data
      /**
       * Online
       */

      Calendar cal1 = Calendar.getInstance();
      String year = Integer.toString(cal1.get(Calendar.YEAR));

      year = "2011";

      boolean flagExist = false;

      try {
         URL aURL = new URL("http://espn.go.com/mlb/playoffs/" + year
            + "/matchup/_/teams/");
         aURL.getContent();
         flagExist = true;
      } catch (Exception e) {

      }

      if ( flagExist == true ) {
         docCheck = Jsoup
               .connect(
                     "http://espn.go.com/mlb/playoffs/" + year
                        + "/matchup/_/teams/").data("query", "Java")
               .userAgent("Mozilla").cookie("auth", "token").timeout(10000)
               .get();

         /**
          * Offline
          */
         // File inputCheck = new File("playoff.html");
         // docCheck = Jsoup.parse(inputCheck, "UTF-8");

         Elements gamesCheck = docCheck.select("div[id]");

         Element newGamesCheck = null;

         for (Element game : gamesCheck) {
            Elements idPlayoffs = game.getElementsByAttributeValueContaining(
                  "id", "playoffs");
            if ( idPlayoffs.size() != 0 ) {
               for (Element content : idPlayoffs) {
                  newGamesCheck = content;
               }
            }
         }

         Elements liClass = newGamesCheck.select("li[class]");
         for (Element aLiClass : liClass) {
            String aString = parseString(aLiClass.toString());
            if ( aString.indexOf("vs") != -1 && aString.indexOf("-") != -1 ) {
               this.playoffsTeam(aString.trim());
            }
         }

         // Check the seasontype in playoff and whether Yankees' season is over
         // or not
         if ( playoffsMatch.size() == 4 ) {
            seasontype = 1;

            boolean flagOver = true;

            for (int i = 0; i < 4; i++) {
               if ( playoffsMatch.get(i).nameFirstTeam.equals("NYY")
                  || playoffsMatch.get(i).nameFirstTeam
                        .equals("New York Yankees")
                  || playoffsMatch.get(i).nameSecondTeam.equals("NYY")
                  || playoffsMatch.get(i).nameSecondTeam
                        .equals("New York Yankees") ) {
                  flagOver = false;
                  break;
               }
            }

            if ( flagOver == true )
               yankeesSeason = true;
         } else if ( playoffsMatch.size() == 6 ) {
            seasontype = 2;
            boolean flagOver = true;

            for (int i = 4; i < 6; i++) {
               if ( playoffsMatch.get(i).nameFirstTeam.equals("NYY")
                  || playoffsMatch.get(i).nameFirstTeam
                        .equals("New York Yankees")
                  || playoffsMatch.get(i).nameSecondTeam.equals("NYY")
                  || playoffsMatch.get(i).nameSecondTeam
                        .equals("New York Yankees") ) {
                  flagOver = false;
                  break;
               }
            }

            if ( flagOver == true )
               yankeesSeason = true;
         } else if ( playoffsMatch.size() == 7 ) {
            seasontype = 3;
            boolean flagOver = true;

            for (int i = 6; i < 7; i++) {
               if ( playoffsMatch.get(i).nameFirstTeam.equals("NYY")
                  || playoffsMatch.get(i).nameFirstTeam
                        .equals("New York Yankees")
                  || playoffsMatch.get(i).nameSecondTeam.equals("NYY")
                  || playoffsMatch.get(i).nameSecondTeam
                        .equals("New York Yankees") ) {
                  flagOver = false;
                  break;
               }
            }

            if ( flagOver == true )
               yankeesSeason = true;

         }
         // End of checking playoff data
         getPlayoffsString();
      }

      // String title = doc.title();
      Elements links = doc.select("tr[align][class]");

      for (Element link : links) {

         if ( !link.getElementsContainingText("Boston").toString().equals("") ) {
            teams[0] = toTeam(parseString(link.getElementsContainingText(
                  "Boston").toString()));
         } else if ( !link.getElementsContainingText("NY Yankees").toString()
               .equals("") ) {
            teams[1] = toTeam(parseString(link.getElementsContainingText(
                  "NY Yankees").toString()));
         } else if ( !link.getElementsContainingText("Tampa Bay").toString()
               .equals("") )
            teams[2] = toTeam(parseString(link.getElementsContainingText(
                  "Tampa Bay").toString()));
         else if ( !link.getElementsContainingText("Baltimore").toString()
               .equals("") )
            teams[3] = toTeam(parseString(link.getElementsContainingText(
                  "Baltimore").toString()));
         else if ( !link.getElementsContainingText("Toronto").toString()
               .equals("") )
            teams[4] = toTeam(parseString(link.getElementsContainingText(
                  "Toronto").toString()));
      }
      setStandings();

   }

   public StandingsParser (int useFiles) throws IOException {
      Document doc;

      Calendar cal1 = Calendar.getInstance();
      String year = Integer.toString(cal1.get(Calendar.YEAR));

      year = "2011";

      boolean flagExist = false;

      if ( useFiles == 0 ) {// Online
         doc = Jsoup.connect("http://espn.go.com/mlb/standings/_/year/2011")
               .data("query", "Java").userAgent("Mozilla")
               .cookie("auth", "token").timeout(10000).get();
         try {
            URL aURL = new URL("http://espn.go.com/mlb/playoffs/" + year
               + "/matchup/_/teams/");
            aURL.getContent();
            flagExist = true;
            if ( flagExist == true ) {
               docCheck = Jsoup
                     .connect(
                           "http://espn.go.com/mlb/playoffs/" + year
                              + "/matchup/_/teams/").data("query", "Java")
                     .userAgent("Mozilla").cookie("auth", "token")
                     .timeout(10000).get();
            }

         } catch (Exception e) {
         }

      } else {// Offline
         File input = new File("OfflineFiles/standings.html");
         doc = Jsoup.parse(input, "UTF-8");

         File inputCheck = new File("OfflineFiles/playoff.html");
         docCheck = Jsoup.parse(inputCheck, "UTF-8");

         flagExist = true;

      }

      // Start of checking playoff data
      if ( flagExist == true ) {
         Elements gamesCheck = docCheck.select("div[id]");

         Element newGamesCheck = null;

         for (Element game : gamesCheck) {
            Elements idPlayoffs = game.getElementsByAttributeValueContaining(
                  "id", "playoffs");
            if ( idPlayoffs.size() != 0 ) {
               for (Element content : idPlayoffs) {
                  newGamesCheck = content;
               }
            }
         }

         Elements liClass = newGamesCheck.select("li[class]");
         for (Element aLiClass : liClass) {
            String aString = parseString(aLiClass.toString());
            if ( aString.indexOf("vs") != -1 && aString.indexOf("-") != -1 ) {
               this.playoffsTeam(aString.trim());
            }
         }

         // Check the seasontype in playoff and whether Yankees' season is over
         // or not
         if ( playoffsMatch.size() == 4 ) {
            seasontype = 1;

            boolean flagOver = true;

            for (int i = 0; i < 4; i++) {
               if ( playoffsMatch.get(i).nameFirstTeam.equals("NYY")
                  || playoffsMatch.get(i).nameFirstTeam
                        .equals("New York Yankees")
                  || playoffsMatch.get(i).nameSecondTeam.equals("NYY")
                  || playoffsMatch.get(i).nameSecondTeam
                        .equals("New York Yankees") ) {
                  flagOver = false;
                  break;
               }
            }

            if ( flagOver == true )
               yankeesSeason = true;
         } else if ( playoffsMatch.size() == 6 ) {
            seasontype = 2;
            boolean flagOver = true;

            for (int i = 4; i < 6; i++) {
               if ( playoffsMatch.get(i).nameFirstTeam.equals("NYY")
                  || playoffsMatch.get(i).nameFirstTeam
                        .equals("New York Yankees")
                  || playoffsMatch.get(i).nameSecondTeam.equals("NYY")
                  || playoffsMatch.get(i).nameSecondTeam
                        .equals("New York Yankees") ) {
                  flagOver = false;
                  break;
               }
            }

            if ( flagOver == true )
               yankeesSeason = true;
         } else if ( playoffsMatch.size() == 7 ) {
            seasontype = 3;
            boolean flagOver = true;

            for (int i = 6; i < 7; i++) {
               if ( playoffsMatch.get(i).nameFirstTeam.equals("NYY")
                  || playoffsMatch.get(i).nameFirstTeam
                        .equals("New York Yankees")
                  || playoffsMatch.get(i).nameSecondTeam.equals("NYY")
                  || playoffsMatch.get(i).nameSecondTeam
                        .equals("New York Yankees") ) {
                  flagOver = false;
                  break;
               }
            }

            if ( flagOver == true )
               yankeesSeason = true;

         }
         // End of checking playoff data
         getPlayoffsString();
      }

      // String title = doc.title();
      Elements links = doc.select("tr[align][class]");

      for (Element link : links) {

         if ( !link.getElementsContainingText("Boston").toString().equals("") ) {
            teams[0] = toTeam(parseString(link.getElementsContainingText(
                  "Boston").toString()));
         } else if ( !link.getElementsContainingText("NY Yankees").toString()
               .equals("") ) {
            teams[1] = toTeam(parseString(link.getElementsContainingText(
                  "NY Yankees").toString()));
         } else if ( !link.getElementsContainingText("Tampa Bay").toString()
               .equals("") )
            teams[2] = toTeam(parseString(link.getElementsContainingText(
                  "Tampa Bay").toString()));
         else if ( !link.getElementsContainingText("Baltimore").toString()
               .equals("") )
            teams[3] = toTeam(parseString(link.getElementsContainingText(
                  "Baltimore").toString()));
         else if ( !link.getElementsContainingText("Toronto").toString()
               .equals("") )
            teams[4] = toTeam(parseString(link.getElementsContainingText(
                  "Toronto").toString()));
      }
      setStandings();

   }

   /**
    * Set the playoffs data
    * 
    * @param aStr
    * @author Keiichi
    */
   private void playoffsTeam (String aStr) {
      // TODO increase name of Teams
      String[] nameofTeams = { "TB", "TEX", "DET", "NYY", "STL", "PHI", "ARI",
         "MIL" };
      String[] nameofTeamsinLarge = { "Tampa Bay Rays", "Texas Rangers",
         "Detroit Tigers", "New York Yankees", "St. Louis Cardinals",
         "Philadelphia Phillies", "Arizona Diamondbacks", "Milwaukee Brewers" };
      MatchofPlayoffs aMatch = new MatchofPlayoffs();
      while (aStr.indexOf("vs") != 0) {

         // Set the name
         for (int n = 0; n < nameofTeams.length; n++) {
            if ( (aStr.indexOf(nameofTeams[n]) == 0 || aStr
                  .indexOf(nameofTeamsinLarge[n]) == 0)
               && aMatch.nameSecondTeam == null ) {
               if ( aMatch.nameFirstTeam == null ) {
                  aMatch.nameFirstTeam = nameofTeams[n];
                  aMatch.nameFirstTeaminLarge = nameofTeamsinLarge[n];
                  if ( aStr.indexOf(nameofTeamsinLarge[n]) == 0 )
                     aStr = aStr
                           .substring(aMatch.nameFirstTeaminLarge.length())
                           .trim();
                  else
                     aStr = aStr.substring(aMatch.nameFirstTeam.length())
                           .trim();
               } else {
                  aMatch.nameSecondTeam = nameofTeams[n];
                  aMatch.nameSecondTeaminLarge = nameofTeamsinLarge[n];
                  if ( aStr.indexOf(nameofTeamsinLarge[n]) == 0 )
                     aStr = aStr.substring(
                           aMatch.nameSecondTeaminLarge.length()).trim();
                  else
                     aStr = aStr.substring(aMatch.nameSecondTeam.length())
                           .trim();
               }
               break;
            }

         }

      }

      // Set the win/defeat/leads
      aStr = aStr.substring(aStr.indexOf("vs") + 2).trim();

      // Which team does leads?
      if ( aStr.indexOf(aMatch.nameFirstTeam) == 0 )
         aMatch.toLead = 1;
      else if ( aStr.indexOf(aMatch.nameSecondTeam) == 0 )
         aMatch.toLead = 2;

      // Is the match over?
      if ( aStr.indexOf("defeat") != -1 )
         aMatch.defeat = true;

      // About win
      if ( aStr.indexOf("-") != -1 ) {
         int win1 = Integer.valueOf(aStr.substring(aStr.indexOf("-") - 1,
               aStr.indexOf("-")));
         int win2 = Integer.valueOf(aStr.substring(aStr.indexOf("-") + 1,
               aStr.indexOf("-") + 2));

         if ( aMatch.toLead == 2 ) {
            aMatch.winFirstTeam = win2;
            aMatch.winSecondTeam = win1;
         } else {
            aMatch.winFirstTeam = win1;
            aMatch.winSecondTeam = win2;
         }
      }

      playoffsMatch.add(aMatch);

   }

   /**
    * Create sentences about playoffs result
    * 
    * @return
    */
   public void getPlayoffsString () {
      /*
       * String standings = ""; MatchofPlayoffs currentMatchwithNYY = new
       * MatchofPlayoffs(); MatchofPlayoffs nextMatchwithNYY = new
       * MatchofPlayoffs();
       */

      // System.out.println("About season type: " + showSeasonType);

      // Create playoffs results in general
      switch (seasontype) {
         case 3:
            for (int n = 6; n < 7; n++) {
               MatchofPlayoffs aMatch = playoffsMatch.get(n);
               WSwithGeneral = WSwithGeneral.concat(this
                     .getPlayoffswithGeneral(aMatch));
            }
         case 2:
            for (int n = 4; n < 6; n++) {
               MatchofPlayoffs aMatch = playoffsMatch.get(n);
               LCwithGeneral = LCwithGeneral.concat(this
                     .getPlayoffswithGeneral(aMatch));
            }
         case 1:
            for (int n = 0; n < 4; n++) {
               MatchofPlayoffs aMatch = playoffsMatch.get(n);
               DSwithGeneral = DSwithGeneral.concat(this
                     .getPlayoffswithGeneral(aMatch));
            }
      }

      if ( WSwithGeneral.isEmpty() )
         WSwithGeneral = "Sorry, world series is not holding now";
      if ( LCwithGeneral.isEmpty() )
         LCwithGeneral = "Sorry, league championship is not holding now";
      if ( DSwithGeneral.isEmpty() )
         DSwithGeneral = "Sorry, division series is not holding now";
      // System.out.println("WSwithGeneral: " + WSwithGeneral);
      // System.out.println("LCwithGeneral: " + LCwithGeneral);
      // System.out.println("DSwithGeneral: " + DSwithGeneral);

      // Create current season
      this.getShowSeasonType();

      // Create playoffs results in Yankees
      this.getPlayoffswithYankees();

      // System.out.println("About Yankees: " + this.playoffswithYankees);

   }

   public void getShowSeasonType () {
      String aStr = "";
      switch (seasontype) {
         case 0:
            aStr += "Regular Season ";
            break;
         case 1:
            aStr += "Division series ";
            break;
         case 2:
            aStr += "League Championhip ";
            break;
         case 3:
            aStr += "World Series ";
            break;
      }
      if ( WSwithGeneral.indexOf("defeat") != -1 )
         aStr += "is over. ";
      else
         aStr += "is holding now. ";

      setShowSesonType(aStr);
   }

   public void getPlayoffswithYankees () {
      String aStr;

      if ( yankeesSeason )
         aStr = "The Yankees' season is over. ";
      else
         aStr = "The Yankees is competing now. ";

      int where = 0;

      for (int i = playoffsMatch.size() - 1; i > -1; i--) {
         if ( playoffsMatch.get(i).nameFirstTeam.equals("NYY")
            || playoffsMatch.get(i).nameFirstTeam.equals("New York Yankees")
            || playoffsMatch.get(i).nameSecondTeam.equals("NYY")
            || playoffsMatch.get(i).nameSecondTeam.equals("New York Yankees") ) {
            if ( i < 5 ) {
               aStr += "In division series, ";
               if ( where < i )
                  where = i;
            }

            else if ( i == 4 || i == 5 ) {
               aStr += "In league championship, ";
               if ( where < i )
                  where = i;
            } else {
               aStr += "In world series, ";
               if ( where < i )
                  where = i;
            }

            aStr += this.getPlayoffswithGeneral(playoffsMatch.get(i));
         }

      }

      // Which team is opposite if NYY beats current opposite team
      if ( !yankeesSeason ) {
         MatchofPlayoffs nextMatchwithNYY = new MatchofPlayoffs();
         MatchofPlayoffs currentMatchwithNYY = this.playoffsMatch.get(where);

         switch (where) {
            case 0:
               nextMatchwithNYY = this.playoffsMatch.get(1);
               break;
            case 1:
               nextMatchwithNYY = this.playoffsMatch.get(0);
               break;
            case 2:
               nextMatchwithNYY = this.playoffsMatch.get(3);
               break;
            case 3:
               nextMatchwithNYY = this.playoffsMatch.get(2);
               break;
            case 4:
               nextMatchwithNYY = this.playoffsMatch.get(5);
               break;
            case 5:
               nextMatchwithNYY = this.playoffsMatch.get(4);
               break;
            case 6:
               nextMatchwithNYY = this.playoffsMatch.get(6);
               break;
         }
         if ( where != 6 ) {
            switch (nextMatchwithNYY.toLead) {
               case 0:
                  aStr += "I don't know the next opposite team if NYY beats ";
                  if ( currentMatchwithNYY.nameFirstTeam.indexOf("NYY") == -1 ) {
                     aStr += currentMatchwithNYY.nameFirstTeaminLarge + ". ";
                     break;
                  } 
                  aStr += currentMatchwithNYY.nameSecondTeaminLarge + ". ";
                  break;
               case 1:
                  aStr += "The next opposite team ";
                  if ( nextMatchwithNYY.defeat == true )
                     aStr += "is ";
                  else
                     aStr += "will be ";

                  aStr += nextMatchwithNYY.nameFirstTeaminLarge
                     + " if NYY beats ";

                  if ( currentMatchwithNYY.nameFirstTeam.indexOf("NYY") == -1 ) {
                     aStr += currentMatchwithNYY.nameFirstTeaminLarge + ". ";
                     break;
                  } 
                  aStr += currentMatchwithNYY.nameSecondTeaminLarge + ". ";
                  break;
               case 2:
                  aStr += "The next opposite team ";
                  if ( nextMatchwithNYY.defeat == true )
                     aStr += "is ";
                  else
                     aStr += "will be ";

                  aStr += nextMatchwithNYY.nameSecondTeam + " if NYY beats ";

                  if ( currentMatchwithNYY.nameFirstTeam.indexOf("NYY") == -1 ) {
                     aStr += currentMatchwithNYY.nameFirstTeaminLarge + ". ";
                     break;
                  } 
                  aStr += currentMatchwithNYY.nameSecondTeaminLarge + ". ";
                  break;
            }
         } else {
            aStr += "If NYY beats ";
            if ( currentMatchwithNYY.nameFirstTeam.indexOf("NYY") == -1 ) {
               aStr += currentMatchwithNYY.nameFirstTeaminLarge + ", ";
            } else {
               aStr += currentMatchwithNYY.nameSecondTeaminLarge + ", ";
            }

            aStr += "NYY gets championship ring!!";

         }
      }

      setPlayoffswithYankees(aStr);

   }

   public String getPlayoffswithGeneral (MatchofPlayoffs aMatch) {
      String standings = "";

      // Playoffs result
      switch (aMatch.toLead) {
         case 0:
            standings = standings.concat(aMatch.nameFirstTeaminLarge
               + " is tied with " + aMatch.nameSecondTeaminLarge + " "
               + aMatch.winFirstTeam + "-" + aMatch.winSecondTeam + ". ");
            break;
         case 1:
            if ( aMatch.defeat == true ) {
               standings = standings.concat(aMatch.nameFirstTeaminLarge
                  + " defeats " + aMatch.nameSecondTeaminLarge + " "
                  + aMatch.winFirstTeam + "-" + aMatch.winSecondTeam + ". ");
               break;
            } 
            standings = standings.concat(aMatch.nameFirstTeaminLarge
                  + " leads " + aMatch.nameSecondTeaminLarge + " "
                  + aMatch.winFirstTeam + "-" + aMatch.winSecondTeam + ". ");
               break;
         case 2:
            if ( aMatch.defeat == true ) {
               standings += aMatch.nameSecondTeaminLarge + " defeats "
                  + aMatch.nameFirstTeaminLarge + " "
                  + Integer.toString(aMatch.winSecondTeam) + "-"
                  + Integer.toString(aMatch.winFirstTeam) + ". ";
               break;
            } 
            standings += aMatch.nameSecondTeaminLarge + " leads "
                  + aMatch.nameFirstTeaminLarge + " "
                  + Integer.toString(aMatch.winSecondTeam) + "-"
                  + Integer.toString(aMatch.winFirstTeam) + ". ";
            break;
      }
      return standings;
   }

   /**
    * Assigns the team variables to a team.
    * 
    * @param parseString a String containing all the team variables that need to
    *           be parsed.
    * @return a team object, containing all the variables pertinent to that team
    */
   private Team toTeam (String parseString) {

      int index = 0;
      String numbers = "0123456789-";
      int lastIndex = 0;
      Team aTeam = new Team();
      while (numbers.indexOf(parseString.charAt(index)) == -1)
         index++;

      /**
       * Keiichi wrote 09/22/2011 Processing of split "x-", "y-" etc.
       */
      if ( index < 3 ) {
         parseString = parseString.substring(index + 1);
         index = 0;
         while (numbers.indexOf(parseString.charAt(index)) == -1)
            index++;
      }

      aTeam.name = parseString.substring(0, index).trim();

      for (int count = 0; count < 12; count++) {
         lastIndex = index;
         while (index < parseString.length()
            && parseString.charAt(index) != ' ') {
            index++;
         }
         String statStr = parseString.substring(lastIndex, index);
         switch (count) {
            case 0:
               aTeam.win = Integer.parseInt(statStr.trim());
               break;
            case 1:
               aTeam.loss = Integer.parseInt(statStr.trim());
               break;
            case 2:
               aTeam.winPercent = Double.parseDouble(statStr);
               break;

            case 3: {
               if ( statStr.trim().charAt(0) == '-' )
                  aTeam.gamesBehind = 0;
               else
                  aTeam.gamesBehind = Double.parseDouble(statStr.trim());
               break;
            }
            case 11: {
               if ( statStr.charAt(1) == ('-') )
                  aTeam.last10Win = Integer.parseInt(statStr.substring(0, 1));
               else
                  aTeam.last10Win = 10;
               break;
            }
         }
         index++;
      }
      return aTeam;
   }

   private void setStandings () {

      for (int teamIndex = 0; teamIndex < teams.length; teamIndex++) {
         int teamsBetter = 0;
         int teamsWorse = 0;
         for (int count = 0; count <= 4; count++) {
            if ( teams[teamIndex].winPercent > teams[count].winPercent )
               teamsWorse++;
            else if ( teams[teamIndex].winPercent < teams[count].winPercent )
               teamsBetter++;

            if ( teamsWorse + teamsBetter + 1 != teams.length )
               teams[teamIndex].tiedFlag = true;

            teams[teamIndex].standing = teamsBetter + 1;
         }
      }

   }

   /**
    * This method takes in a string and removes all writing found between <>
    * signs. This removes all the html code found in the string and leaves all
    * the valuable information.
    * 
    * @param in, an unparsed string
    * @return a parsed string
    */
   static String parseString (String in) {
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
    * Getter for the Boston Red Sox .
    * 
    * @return the boston red sox
    */
   public Team getBoston () {
      return teams[0];
   }

   /**
    * Getter for the Baltimore Orioles .
    * 
    * @return the Baltimore Orioles
    */
   public Team getBaltimore () {
      return teams[3];
   }

   /**
    * Getter for the New York Yankees .
    * 
    * @return the New York Yankees
    */
   public Team getNYYankees () {
      return teams[1];
   }

   /**
    * Getter for the Toronto Blue Jays .
    * 
    * @return the Toronto Blue Jays
    */
   public Team getToronto () {
      return teams[4];
   }

   /**
    * Getter for the Tampa Bay Rays .
    * 
    * @return the Tampa Bay Rays
    */
   public Team getTampaBay () {
      return teams[2];
   }

   public Team getFirst () {
      for (Team aTeam : teams) {
         if ( aTeam.gamesBehind == 0 )
            return aTeam;
      }
      return null;
   }

   public Team getFirstTeamOtherThanBoston () {
      Team second = new Team();
      second.gamesBehind = 1000;
      for (Team aTeam : teams) {
         if ( aTeam.gamesBehind < second.gamesBehind
            && !aTeam.name.equals("Boston") )
            second = aTeam;
      }
      return second;
   }

   public Team getFirstTeamOtherThanNYY () {
      Team second = new Team();
      second.gamesBehind = 1000;
      for (Team aTeam : teams) {
         if ( aTeam.gamesBehind < second.gamesBehind
            && !aTeam.name.equals("NY Yankees") )
            second = aTeam;
      }
      return second;
   }

   public String getBostonStandingsString () {
      String recap = "";
      String bosPos = findBostonStandings();
      recap = "The Red Sox are currently " + bosPos
         + " in the American East Division.";
      if ( this.getBoston().gamesBehind != 0 ) {
         recap += " They are " + this.getBoston().gamesBehind + " "
            + gameOrGames(this.getBoston().gamesBehind) + " behind the "
            + this.getFirst().name + " with a score of " + getBoston().win
            + " wins and " + getBoston().loss + " losses, winning "
            + this.getBoston().last10Win + " "
            + gameOrGames(this.getBoston().gamesBehind)
            + " out of their last 10 games.";
      } else {
         if ( getFirstTeamOtherThanBoston().gamesBehind != 0 )
            recap += " They are " + getFirstTeamOtherThanBoston().gamesBehind
               + " games ahead of " + getFirstTeamOtherThanBoston().name
               + " with a score of " + getBoston().win + " wins and "
               + getBoston().loss + " losses, winning "
               + this.getBoston().last10Win + " "
               + gameOrGames(this.getBoston().gamesBehind)
               + " out of their last 10 games.";
         else
            recap += " They are tied with the "
               + getFirstTeamOtherThanBoston().name + " with a score of "
               + getBoston().win + " wins and " + getBoston().loss
               + " losses, winning " + this.getBoston().last10Win + " "
               + gameOrGames(this.getBoston().gamesBehind)
               + " out of their last 10 games.";
      }
      return recap;
   }

   public String getNYYStandingsString () {
      String recap = "";
      String nyyPos = findNYYStandings();
      if ( seasontype == 0 )
         recap = "The NY Yankees are currently " + nyyPos
            + " in the American East Division.";
      else
         recap = "The NY Yankees are " + nyyPos
            + " in the American East Division.";
      if ( this.getNYYankees().gamesBehind != 0 ) {
         recap += " They are " + this.getNYYankees().gamesBehind + " "
            + gameOrGames(this.getNYYankees().gamesBehind) + " behind the "
            + this.getFirst().name + " with a score of " + getNYYankees().win
            + " wins and " + getNYYankees().loss + " losses, winning "
            + this.getNYYankees().last10Win + " "
            + gameOrGames(this.getNYYankees().gamesBehind)
            + " out of their last 10 games in this regular season.";
      } else {
         if ( getFirstTeamOtherThanNYY().gamesBehind != 0 )
            recap += " They are " + getFirstTeamOtherThanNYY().gamesBehind
               + " games ahead of " + getFirstTeamOtherThanNYY().name
               + " with a score of " + getNYYankees().win + " wins and "
               + getNYYankees().loss + " losses, winning "
               + this.getNYYankees().last10Win + " "
               + gameOrGames(this.getNYYankees().gamesBehind)
               + " out of their last 10 games in this regular season.";
         else
            recap += " They are tied with " + getFirstTeamOtherThanNYY().name
               + " with a score of " + getNYYankees().win + " wins and "
               + getNYYankees().loss + " losses, winning "
               + this.getNYYankees().last10Win + " "
               + gameOrGames(this.getNYYankees().gamesBehind)
               + " out of their last 10 games in this regular season.";
      }
      return recap;
   }

   private String gameOrGames (double gamesBehind) {
      if ( gamesBehind <= 1 )
         return "game";
      return "games";
   }

   /**
    * Returns a string which states the standings of boston inside its division.
    * 
    * @return
    */
   private String findBostonStandings () {
      double bosGamesBehind = getBoston().gamesBehind;
      int bosStandings = 1;
      for (Team aTeam : teams) {
         if ( aTeam.gamesBehind < bosGamesBehind )
            bosStandings++;
      }
      switch (bosStandings) {
         case 1:
            return "first";
         case 2:
            return "second";
         case 3:
            return "third";
         case 4:
            return "fourth";
         case 5:
            return "fifth";
         case 6:
            return "sixth";
      }
      return null;
   }

   /**
    * Returns a string which states the standings of NYY inside its division.
    * 
    * @return
    */
   private String findNYYStandings () {
      double NYYGamesBehind = getNYYankees().gamesBehind;
      int NYYStandings = 1;
      for (Team aTeam : teams) {
         if ( aTeam.gamesBehind < NYYGamesBehind )
            NYYStandings++;
      }
      switch (NYYStandings) {
         case 1:
            return "first";
         case 2:
            return "second";
         case 3:
            return "third";
         case 4:
            return "fourth";
         case 5:
            return "fifth";
         case 6:
            return "sixth";
      }
      return null;
   }

   private void setPlayoffswithYankees (String aStr) {
      playoffswithYankees = aStr;
   }

   private void setShowSesonType (String aStr) {
      showSeasonType = aStr;
   }

   public String getShowSeasonType_xml () {
      return this.showSeasonType;
   }

   public String getPlayoffswithYankees_xml () {
      return this.playoffswithYankees;
   }

   public String getDSwithGeneral_xml () {
      return this.DSwithGeneral;
   }

   public String getLCwithGeneral_xml () {
      return this.LCwithGeneral;
   }

   public String getWSwithGeneral_xml () {
      return this.WSwithGeneral;
   }

   public boolean getyankeesSeason () {
      return this.yankeesSeason;
   }
}
