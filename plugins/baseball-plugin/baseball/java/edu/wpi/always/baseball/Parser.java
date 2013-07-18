package edu.wpi.always.baseball;

import edu.wpi.always.baseball.recaps.*;
import edu.wpi.always.baseball.standings.StandingsParser;
import java.io.IOException;
import java.util.*;

public class Parser {

   /*
    * fromFiles = 1: OFFLINE fromFiles = 0: ONLINE
    */
   int fromFiles;

   Game[] seasonGames;

   /**
    * add @Keiichi Oct.3rd Which is season now? 0 - spring 1 - Regular Season 2
    * - Postseason
    * 
    * @author Keiichi
    */
   int seasonType = 1;

   /**
    * The parser constructor initializes the seasonGames array with the games of
    * the seaon If a game is not found, it is marked by having the thereIsAGame
    * value set to false; If a game is postponed, then it is marked by having a
    * gameDate value of "POSTPONED"
    */
   public Parser () {
      try {
         // fixed @Keiichi Oct.3rd
         if ( recapChecker() ) {
            GamesFinderParser parser = new GamesFinderParser();
            seasonGames = parser.getRecentGames();
            seasonType = parser.getSeasonType();
         } else
            throw new Exception();
      } catch (Exception e) {
         seasonGames = new Game[4];
         for (Game aGame : seasonGames) {
            aGame = new Game();
            aGame.thereIsAGame = false;
         }
      }
   }

   public Parser (int useFiles) {
      try {
         this.fromFiles = useFiles;
         // fixed @Keiichi Oct.3rd
         if ( recapChecker() || useFiles == 1 ) {
            GamesFinderParser parser = new GamesFinderParser(useFiles);
            seasonGames = parser.getRecentGames();
            seasonType = parser.getSeasonType();
         } else
            throw new Exception();
      } catch (Exception e) {
         seasonGames = new Game[4];
         for (Game aGame : seasonGames) {
            aGame = new Game();
            aGame.thereIsAGame = false;
         }
      }
   }

   /* Standings Interface Methods */

   /**
    * The method uses the game results acquired by the recap parser and the
    * league standings acquired by the standings parser to respond to the
    * 
    * @param bosScore
    * @param otherScore
    * @return
    */
   public String getWeNeededThatWinResponseString (StandingsParser aParser,
         int bosScore, int otherScore) {
      String returnString = "";
      if ( bosScore > otherScore ) {
         if ( aParser.getNYYankees().gamesBehind == 0 )
            returnString += "We are doing great.  We are still first in the American League East.";
         else
            returnString = " We did.  We are now only "
               + aParser.getNYYankees().gamesBehind
               + " games behind the first team in our division. ";

      } else {

         if ( aParser.getNYYankees().gamesBehind == 0 )
            returnString = "I think we will recover quickly.  We are still first in the American League East.";
         else
            returnString = " Its a shame we lost.  We are now "
               + aParser.getNYYankees().gamesBehind
               + " games behind the first team in our division. ";
      }
      if ( seasonType == 2 ) {
         if ( bosScore > otherScore )
            returnString = "We are doing great. We got important won in postseason.";
         else
            returnString = "Its a shame we lost. ";
      }
      return returnString;
   }

   /**
    * This method returns strings talking about the playoffs depending on the
    * results of the standings results and the responseIndex, which indicates
    * which option the user picked
    * 
    * @param responseIndex: 0-End of Regular Season 1-Not Far in Playoffs
    *           2-World Series
    */
   public static String getPlayoffsResponse (StandingsParser aParser,
         int responseIndex) {
      String playoffResponse = "";
      switch (responseIndex) {
         case 0: // End of regular season
            if ( aParser.getNYYankees().gamesBehind < 5 ) {
               if ( aParser.getNYYankees().gamesBehind == 0 )
                  playoffResponse = "They are still first in their division and they won "
                     + aParser.getNYYankees().last10Win
                     + " of their last 10 games. You don't think they will be able to keep their lead?";
               else
                  playoffResponse = "They are only "
                     + aParser.getNYYankees().gamesBehind
                     + " games behind the " + aParser.getFirst().name
                     + ".  Don't you think they can make a comeback?";
            } else {
               playoffResponse = "It is true that things look pretty grim.  The Yankkes are now "
                  + aParser.getNYYankees().gamesBehind
                  + " behind the "
                  + aParser.getFirst().name
                  + ". Do you think they have any chances of making a comeback? ";
            }
            break;
         case 1: // Not Far in Playoffs
            if ( aParser.getNYYankees().gamesBehind == 0 )
               playoffResponse = "I think they have what it takes to make it to the playoffs too. They are first in their division for now. "
                  + " How far do you think they will get into the playoffs?";
            else {
               playoffResponse = "We are "
                  + aParser.getNYYankees().gamesBehind
                  + " games behind the "
                  + aParser.getFirst().name
                  + ". Catching up is definitely possible. How far do you think they will get into the playoffs?";
            }
            break;

         case 2:// World Series
            if ( aParser.getNYYankees().gamesBehind == 0 )
               playoffResponse = "I think they could very well get to the world series.  After all, they are first in the hardest division.  How likely do you think they to win it?";
            else
               playoffResponse = "It will be a hard road, especially that they are still "
                  + aParser.getNYYankees().gamesBehind
                  + " games behind the "
                  + aParser.getFirst().name
                  + ".  Although stranger things have happened. Do you think they have a shot at winning it?";
            break;
      }
      return playoffResponse;
   }

   /* Recaps Interface Methods */

   /**
    * Returns a game with what teams played and when, compared to the current
    * date. If no game was found, a game with no opponent or date is returned.
    * 
    * @param gameIndex: decides which game is returned. a game index of 1
    *           returns the last game played, 2 the one before that, etc.
    * @return Game, containing the opponents and date of the last game played.
    * @throws IOException
    */
   public Game getOneOfTheLastGames (int gameIndex) {
      try {
         if ( recapChecker() ) {
            Game lastGame = seasonGames[gameIndex - 1];
            Date currentDate = Calendar.getInstance().getTime();
            if ( timeSinceGame(currentDate, lastGame.gameDate.trim()) == 1 )// Game
                                                                            // Was
                                                                            // Yesterday
            {
               lastGame.gameDate = "yesterday";
               lastGame.timeSinceLastGame = 1;
            } else {
               lastGame.timeSinceLastGame = timeSinceGame(currentDate,
                     lastGame.gameDate.trim());
               lastGame.gameDate = findDayOfWeek(lastGame.gameDate.trim());
            }

            int[] score = getLastGameScore(lastGame);
            lastGame.bosScore = score[0];
            lastGame.otherScore = score[1];
            return lastGame;
         } 
         throw new Exception();
      } catch (Exception e) {
         Game noGame = new Game();// A game with no values mean no game was
                                  // found.
         noGame.thereIsAGame = false;
         return noGame;
      }
   }

   /**
    * This method interfaces with the GameRecap class to create a recap when
    * given a game and returns the recap.
    * 
    * @param aGame the game whose recap is needed.
    * @return the string containing the the recap
    */
   public String getRecap (Game aGame) {
      GameRecapParser aRecapParser;
      try {
         // aRecapParser = new GameRecapParser(aGame.link);
         aRecapParser = new GameRecapParser(aGame.link, fromFiles);
         return aRecapParser.toString();
      } catch (IOException e) {
         e.printStackTrace();
      }
      return null;
   }

   /**
    * Takes the link of the game stored in lastGame.link() and uses that to
    * obtain the game score.
    * 
    * @param lastGame
    * @return
    */
   private int[] getLastGameScore (Game lastGame) {
      try {
         // GameRecapParser aParser = new GameRecapParser(lastGame.link);
         GameRecapParser aParser = new GameRecapParser(lastGame.link, fromFiles);
         return aParser.getScore();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return null;
   }

   /**
    * Returns the number of days between two dates to determine how many days
    * have passed between two dates
    */
   @SuppressWarnings("deprecation")
   private static int timeSinceGame (Date currentDate, String aDate) {

      Date gameDate = new Date(currentDate.getYear(), getMonth(aDate),
            getDay(aDate));

      // int aDay = currentDate.getDay();
      currentDate = new Date(currentDate.getYear(), currentDate.getMonth(),
            currentDate.getDate());
      return (int) ((currentDate.getTime() - gameDate.getTime()) / 86400000);
   }

   /**
    * Prints out all the games that it can find for this year.
    * 
    * @throws IOException
    */
   public void runGameFinder () throws IOException {

      GamesFinderParser newParser = new GamesFinderParser();
      Game[] lastGamesPlayed = newParser.getRecentGames();
      for (int index = 1; index <= lastGamesPlayed.length; index++) {

         if ( lastGamesPlayed[index - 1] != null
            && lastGamesPlayed[index - 1].thereIsAGame == true ) {

            int flag = 1;
            while (flag == 1) {
               try {
                  flag = 0;
                  if ( lastGamesPlayed[index - 1].gameDate.equals("POSTPONED") )
                     System.out.println("The game has been postponed");
                  else {
                     // GameRecapParser aParser = new
                     // GameRecapParser(lastGamesPlayed[index-1].link);
                     GameRecapParser aParser = new GameRecapParser(
                           lastGamesPlayed[index - 1].link, fromFiles);
                     System.out.println(aParser.toString());
                  }
               } catch (IOException e) {
                  System.out.println("Connection Failed.  Trying again");

                  flag = 1;
               }
            }
         }
      }
   }

   /**
    * The method uses playoff games from past seasons to figure out if the recap
    * parser still works. This checks for changes to the website that might have
    * broken the parser. If the parser has been broken, the program will not use
    * the data acquired.
    * 
    * @throws IOException
    */
   public boolean recapChecker () throws IOException {
      /*
       * GameRecapParser Parser1 = new
       * GameRecapParser("http://espn.go.com/mlb/boxscore?gameId=281011130");
       * boolean game1bool = Parser1.toString().equals(
       * "\nThe Red Sox lost to the Rays 8 to 9 in a close 11-inning game.  The Red Sox offense was still able to have a few solid hits, with  2 homeruns from D Pedroia, 1 homerun from K Youkilis, and 1 homerun from J Bay.  \n These homeruns were part of the 8 runs scored by the Red Sox offense:  J Bay  scored 4 runs,  D Pedroia  scored 2 runs, and K Youkilis scored 1 run."
       * ); GameRecapParser Parser2 = new
       * GameRecapParser("http://espn.go.com/mlb/boxscore?gameId=260824103");
       * boolean game2bool = Parser2.toString().equals(
       * "\nThe Red Sox beat the Angels 2 to 1 in a close pitcher duel. J Beckett defeated the Angels  bullpen, letting only 1 run and 2 hits.   The offense managed to get just enough runs in to take the game, with  1 homerun from D Ortiz.  \n That homerun was part of the 2 runs scored by the Red Sox offense to secure the win:  D Ortiz scored 1 run, and D Mirabelli scored 1 run."
       * ); GameRecapParser Parser3 = new
       * GameRecapParser("http://espn.go.com/mlb/boxscore?gameId=281019130");
       * boolean game3bool = Parser3.toString().equals(
       * "\nBoston lost to the Rays 1 to 3.   The Red Sox offense was underwhelming this game, with only  1 homerun from D Pedroia.  \n That homerun was part of the 1 run scored by the Red Sox offense:  D Pedroia scored 1 run."
       * ); return game1bool && game2bool && game3bool; GameRecapParser Parser1
       * = new
       * GameRecapParser("http://espn.go.com/mlb/boxscore?gameId=271004105");
       * boolean game1bool = Parser1.toString().equals(
       * "\nThe New York Yankees were routed by the Indians 3 to 12. The Yankees bullpen were not able to keep up with the Indians  offense.   The Yankees offense was underwhelming this game, with only  1 homerun from J Damon, and 1 homerun from R Cano.  \n These homeruns were part of the 3 runs scored by the Yankees offense:  J Damon scored 1 run,  R Cano scored 1 run, and B Abreu scored 1 run."
       * ); GameRecapParser Parser2 = new
       * GameRecapParser("http://espn.go.com/mlb/boxscore?gameId=291019103");
       * boolean game2bool = Parser2.toString().equals(
       * "\nThe New York Yankees lost to the Angels 4 to 5 in a close 11-inning game.  The Yankees offense was underwhelming this game, with only  1 homerun from D Jeter, 1 homerun from A Rodriguez, 1 homerun from J Damon, and 1 homerun from J Posada.  \n These homeruns were part of the 4 runs scored by the Yankees offense:  D Jeter scored 1 run,  A Rodriguez scored 1 run,  J Damon scored 1 run, and J Posada scored 1 run."
       * ); GameRecapParser Parser3 = new
       * GameRecapParser("http://espn.go.com/mlb/boxscore?gameId=291104110");
       * boolean game3bool = Parser3.toString().equals(
       * "\nThe New York Yankees beat the Phillies 7 to 3 due to a solid performance from A Pettitte and the Yankees offense, which had  1 homerun from H Matsui.  \n That homerun was part of the 7 runs scored by the Yankees offense to secure the win:  H Matsui  scored 6 runs, and M Teixeira scored 1 run."
       * );
       */
      GameRecapParser Parser1 = new GameRecapParser(
            "http://espn.go.com/mlb/boxscore?gameId=271004105", fromFiles);
      boolean game1bool = Parser1
            .toString()
            .equals(
                  "\nThe New York Yankees were routed by the Indians 3 to 12. The Yankees bullpen were not able to keep up with the Indians  offense.   The Yankees offense was underwhelming this game, with only  1 homerun from J Damon, and 1 homerun from R Cano.  \n These homeruns were part of the 3 runs scored by the Yankees offense:  J Damon scored 1 run,  R Cano scored 1 run, and B Abreu scored 1 run.");
      GameRecapParser Parser2 = new GameRecapParser(
            "http://espn.go.com/mlb/boxscore?gameId=291019103", fromFiles);
      boolean game2bool = Parser2
            .toString()
            .equals(
                  "\nThe New York Yankees lost to the Angels 4 to 5 in a close 11-inning game.  The Yankees offense was underwhelming this game, with only  1 homerun from D Jeter, 1 homerun from A Rodriguez, 1 homerun from J Damon, and 1 homerun from J Posada.  \n These homeruns were part of the 4 runs scored by the Yankees offense:  D Jeter scored 1 run,  A Rodriguez scored 1 run,  J Damon scored 1 run, and J Posada scored 1 run.");
      GameRecapParser Parser3 = new GameRecapParser(
            "http://espn.go.com/mlb/boxscore?gameId=291104110", fromFiles);
      boolean game3bool = Parser3
            .toString()
            .equals(
                  "\nThe New York Yankees beat the Phillies 7 to 3 due to a solid performance from A Pettitte and the Yankees offense, which had  1 homerun from H Matsui.  \n That homerun was part of the 7 runs scored by the Yankees offense to secure the win:  H Matsui  scored 6 runs, and M Teixeira scored 1 run.");
      return game1bool && game2bool && game3bool;
   }

   /**
    * Given a 3 letter value for a day, this method finds the full name of the
    * day and returns it.
    * 
    * @param day
    * @return
    */
   private static String findDayOfWeek (String day) {
      String[] dayOfWeek = { "Monday", "Tuesday", "Wednesday", "Thursday",
         "Friday", "Saturday", "Sunday" };
      for (int i = 0; i < dayOfWeek.length; i++) {

         if ( (dayOfWeek[i].substring(0, 3)).equals(day.substring(0, 3)) ) {
            return dayOfWeek[i];
         }
      }
      return null;
   }

   /**
    * Returns an int representing the day of the month.
    * 
    * @param aDay
    * @return
    */
   private static int getDay (String aDay) {
      return Integer.parseInt(aDay.substring(aDay.length() - 2).trim());
   }

   /**
    * Gets the numeral value of the month given a months first 3 letters
    * 
    * @param aDate
    * @return
    */
   private static int getMonth (String aDate) {
      String aMonth = aDate.substring(5, 9).trim();
      String[] months = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
         "Aug", "Sep", "Oct", "Nov", "Dec" };
      for (int i = 0; i < months.length; i++) {
         if ( months[i].equals(aMonth) )
            return i;
      }
      return 0;
   }

   public int getSeasonType () {
      return seasonType;
   }
}
