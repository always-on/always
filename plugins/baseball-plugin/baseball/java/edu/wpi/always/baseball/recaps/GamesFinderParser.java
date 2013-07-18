package edu.wpi.always.baseball.recaps;

/**
 * This class parses espn's Red Sox schedule page and comes up with the five most recent games played by the Red Sox.
 * 
 * @author Frederik Clinckemaillie
 */

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.io.*;

public class GamesFinderParser {
   private Game[] lastGamesPlayed;

   /**
    * Which is season now? 0 - spring 1 - Regular Season 2 - Postseason
    * 
    * @author Keiichi
    */
   public int seasonType;

   public GamesFinderParser () throws IOException {
      lastGamesPlayed = new Game[100];

      /**
       * Online
       */
      // Document doc =
      // Jsoup.connect("http://espn.go.com/mlb/team/schedule/_/name/bos/boston-red-sox")
      // .data("query", "Java")
      Document doc = Jsoup
            .connect(
                  "http://espn.go.com/mlb/team/schedule/_/name/nyy/year/2011/new-york-yankees")
            .data("query", "Java").userAgent("Mozilla").cookie("auth", "token")
            .timeout(10000).get();

      /**
       * Offline
       */
      // File input = new File("scheduleNYY.html");
      // Document doc = Jsoup.parse(input, "UTF-8");

      // String title = doc.title();

      Elements games = doc.select("tr[class]");
      Elements playedGames = new Elements();
      for (Element game : games) {
         if ( game.toString().indexOf("POSTPONED") > -1
            || game.toString().indexOf("class=\"score\"") > -1 )
            playedGames.add(game);

      }

      /**
       * Check seasons
       * 
       * @author Keiichi Oct.3rd
       */

      if ( playedGames.size() < 20 )
         seasonType = 2;
      else if ( playedGames.size() < 35 )
         seasonType = 0;
      else
         seasonType = 1;

      int index = 0;
      while (index < 100) {
         // This part initializes games if there actually is a game.
         if ( (index) < playedGames.size() ) {
            index++;
            Element aGameElement = playedGames.get(playedGames.size() - index);
            String aGameStr = aGameElement.toString();

            lastGamesPlayed[index - 1] = new Game();
            if ( aGameStr.indexOf("POSTPONED") != -1 ) {
               lastGamesPlayed[index - 1].gameDate = "POSTPONED";
               lastGamesPlayed[index - 1].opposingTeam = getOppTeam(aGameStr);
            } else {
               lastGamesPlayed[index - 1].gameDate = parseString(
                     aGameStr.substring(0,
                           aGameStr.indexOf("class=\"game-schedule\""))).trim();
               lastGamesPlayed[index - 1].link = getLink(aGameStr);
               lastGamesPlayed[index - 1].opposingTeam = getOppTeam(aGameStr);
            }
         }
         // This part initializes the game if there is no game, and marks it
         // by setting the gameDate with "NOGAME"
         else {
            index++;
            lastGamesPlayed[index - 1] = new Game();
            lastGamesPlayed[index - 1].thereIsAGame = false;
         }

      }

   }

   public GamesFinderParser (int useFiles) throws IOException {
      lastGamesPlayed = new Game[100];

      Document doc;

      if ( useFiles == 0 ) {// Online
         doc = Jsoup
               .connect(
                     "http://espn.go.com/mlb/team/schedule/_/name/nyy/year/2011/new-york-yankees")
               .data("query", "Java").userAgent("Mozilla")
               .cookie("auth", "token").timeout(10000).get();
      } else {// Offline
         File input = new File("OfflineFiles/scheduleNYY.html");
         doc = Jsoup.parse(input, "UTF-8");
      }

      // String title = doc.title();

      Elements games = doc.select("tr[class]");
      Elements playedGames = new Elements();
      for (Element game : games) {
         if ( game.toString().indexOf("POSTPONED") > -1
            || game.toString().indexOf("class=\"score\"") > -1 )
            playedGames.add(game);

      }

      /**
       * Check seasons
       * 
       * @author Keiichi Oct.3rd
       */

      if ( playedGames.size() < 20 )
         seasonType = 2;
      else if ( playedGames.size() < 35 )
         seasonType = 0;
      else
         seasonType = 1;

      int index = 0;
      while (index < 100) {
         // This part initializes games if there actually is a game.
         if ( (index) < playedGames.size() ) {
            index++;
            Element aGameElement = playedGames.get(playedGames.size() - index);
            String aGameStr = aGameElement.toString();

            lastGamesPlayed[index - 1] = new Game();
            if ( aGameStr.indexOf("POSTPONED") != -1 ) {
               lastGamesPlayed[index - 1].gameDate = "POSTPONED";
               lastGamesPlayed[index - 1].opposingTeam = getOppTeam(aGameStr);
            } else {
               lastGamesPlayed[index - 1].gameDate = parseString(
                     aGameStr.substring(0,
                           aGameStr.indexOf("class=\"game-schedule\""))).trim();
               lastGamesPlayed[index - 1].link = getLink(aGameStr);
               lastGamesPlayed[index - 1].opposingTeam = getOppTeam(aGameStr);
            }
         }
         // This part initializes the game if there is no game, and marks it
         // by setting the gameDate with "NOGAME"
         else {
            index++;
            lastGamesPlayed[index - 1] = new Game();
            lastGamesPlayed[index - 1].thereIsAGame = false;
         }

      }

   }

   private String getOppTeam (String aGameStr) {
      int startIndex = 0;
      int endIndex = 0;
      startIndex = aGameStr.indexOf("<li class=\"team-name\"");
      endIndex = startIndex + aGameStr.substring(startIndex).indexOf("</li>");

      return parseString(aGameStr.substring(startIndex, endIndex + 1));
   }

   private String getLink (String aGameStr) {

      int startIndex = 0;
      int endIndex = 0;
      startIndex = aGameStr.indexOf("<li class=\"score\"");
      endIndex = startIndex + aGameStr.substring(startIndex).indexOf("</li>");
      String subStr = aGameStr.substring(startIndex, endIndex);
      while (subStr.indexOf('>') != -1) {
         if ( subStr.substring(0, subStr.indexOf('>')).indexOf("gameId") > -1 ) {
            startIndex = subStr.indexOf("?");
            endIndex = startIndex
               + subStr.substring(startIndex + 1).indexOf("\"") + 1;

            return "http://espn.go.com/mlb/boxscore"
               + subStr.substring(startIndex, endIndex);
         } else
            subStr = subStr.substring(subStr.indexOf('>') + 1);
      }
      return " ";
   }

   public Game[] getRecentGames () {
      return lastGamesPlayed;
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
    * Get the season type(0-spring, 1 - Regular Season, 2 - Postseason)
    * 
    * @return seasonType
    */
   public int getSeasonType () {
      return seasonType;
   }

}
