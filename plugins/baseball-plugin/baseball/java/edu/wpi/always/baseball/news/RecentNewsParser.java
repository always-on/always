package edu.wpi.always.baseball.news;

import edu.wpi.always.baseball.recaps.GameRecapParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import java.io.*;
import java.util.ArrayList;

public class RecentNewsParser {
   Document doc;

   ArrayList<NewsHeadline> newsHeadlines;

   public RecentNewsParser () throws IOException {

      getHeadlines();
      // If offline, do not use updateNames().
      // updateNames();
      analyzeHeadlines();
      orderHeadlines();
   }

   public RecentNewsParser (int useFiles) throws IOException {

      getHeadlines(useFiles);
      if ( useFiles == 0 ) {
         updateNames();
      }
      analyzeHeadlines();
      orderHeadlines();
   }

   /**
    * Getter that returns the headline at that index
    * 
    * @param index index of the headline, in terms of relevance.
    * @return
    */
   public NewsHeadline getHeadline (int index) {
      return newsHeadlines.get(index);
   }

   /**
    * Gets the headlines from the RSS feed of the mlb for the NY Yankees teams
    * and puts them in the NewsHeadlines Arraylist.
    * 
    * @throws IOException
    */
   private void getHeadlines () throws IOException {
      /**
       * Online
       */
      // doc =
      // Jsoup.connect("http://mlb.mlb.com/partnerxml/gen/news/rss/bos.xml")
      // .data("query", "Java")
      doc = Jsoup.connect("http://mlb.mlb.com/partnerxml/gen/news/rss/nyy.xml")
            .data("query", "Java").userAgent("Mozilla").cookie("auth", "token")
            .timeout(10000).get();

      /**
       * Offline
       */
      /*
       * File input = new File("nyy.xml"); doc = Jsoup.parse(input, "UTF-8");
       */
      newsHeadlines = new ArrayList<NewsHeadline>();
      Elements headlines = doc.select("item");

      for (Element headline : headlines) {
         String date = GameRecapParser.parseString(headline.select("pubdate")
               .get(0).toString());
         String newsHeadLineStr = GameRecapParser.parseString(headline
               .select("description").get(0).toString());

         // The code &amp;#39 means an apostrophe. This while loop removes it.
         while (newsHeadLineStr.indexOf("&amp;#39;") > -1) {
            int index = newsHeadLineStr.indexOf("&amp;#39;");
            newsHeadLineStr = newsHeadLineStr.substring(0, index) + "\'"
               + newsHeadLineStr.substring(index + 9);
         }
         newsHeadlines.add(new NewsHeadline(newsHeadLineStr, date));
      }
   }

   private void getHeadlines (int useFiles) throws IOException {
      if ( useFiles == 0 ) {// Online
         doc = Jsoup
               .connect("http://mlb.mlb.com/partnerxml/gen/news/rss/nyy.xml")
               .data("query", "Java").userAgent("Mozilla")
               .cookie("auth", "token").timeout(10000).get();
      } else {// Offline
         File input = new File("OfflineFiles/nyy.xml");
         doc = Jsoup.parse(input, "UTF-8");
      }

      newsHeadlines = new ArrayList<NewsHeadline>();
      Elements headlines = doc.select("item");

      for (Element headline : headlines) {
         String date = GameRecapParser.parseString(headline.select("pubdate")
               .get(0).toString());
         String newsHeadLineStr = GameRecapParser.parseString(headline
               .select("description").get(0).toString());

         // The code &amp;#39 means an apostrophe. This while loop removes it.
         while (newsHeadLineStr.indexOf("&amp;#39;") > -1) {
            int index = newsHeadLineStr.indexOf("&amp;#39;");
            newsHeadLineStr = newsHeadLineStr.substring(0, index) + "\'"
               + newsHeadLineStr.substring(index + 9);
         }
         newsHeadlines.add(new NewsHeadline(newsHeadLineStr, date));
      }
   }

   /**
    * Analyzes each headlines to get an idea of the topic it talks about and how
    * good of a headline it is depending on how many key words are found in it.
    * 
    * @throws IOException
    */
   private void analyzeHeadlines () throws IOException {
      File[] wordFiles = new File[9];
      wordFiles[0] = new File("Words/Injuries/HelpfulInjuryWords");
      wordFiles[1] = new File("Words/Injuries/InjuryWords");
      wordFiles[2] = new File("Words/Players/HelpfulPlayerCareerWords");
      wordFiles[3] = new File("Words/Players/PlayerNames");
      wordFiles[4] = new File("Words/Players/PlayerNicknames");
      wordFiles[5] = new File("Words/TeamsAndGames/HelpfulGameWords");
      wordFiles[6] = new File("Words/TeamsAndGames/TeamNames");
      wordFiles[7] = new File("Words/TeamsAndGames/TeamRelatedNames");
      wordFiles[8] = new File("Words/Business/businessWords");

      for (NewsHeadline aHeadline : newsHeadlines) {
         String HLName = aHeadline.name + " ";
         /*
          * int gameCount = 0; int playerNameCount = 0; int injuryCount = 0; int
          * careerWords = 0; int BostonCount = 0; int businessWords = 0;
          */
         for (int index = 0; index < wordFiles.length; index++) {
            try (BufferedReader input = new BufferedReader(new FileReader(
                  wordFiles[index]))) {
               String letters = "qwertyuiopasdfghjklzzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
               String aStr = input.readLine();

               while (aStr != null) {
                  String unCapStr = aStr.trim();

                  unCapStr = (char) (unCapStr.charAt(0) - 32)
                     + unCapStr.substring(1);
                  if ( (HLName.indexOf(aStr) != -1 && (HLName.indexOf(aStr) == 0 || letters
                        .indexOf(HLName.charAt(HLName.indexOf(aStr) - 1)) == -1))
                     || (HLName.indexOf(unCapStr) != -1 && (HLName
                           .indexOf(unCapStr) == 0 || letters.indexOf(HLName
                           .charAt(HLName.indexOf(unCapStr) - 1)) == -1)) ) {

                     if ( index == 0 || index == 1 )
                        aHeadline.injuryCount++;
                     if ( index == 3 || index == 4 )
                        aHeadline.playerNameCount++;
                     if ( index == 2 )
                        aHeadline.careerWords++;
                     if ( index == 5 || index == 6 )
                        aHeadline.gameCount++;
                     if ( index == 7 )
                        aHeadline.BostonCount++;
                     if ( index == 8 )
                        aHeadline.businessWords++;

                  }
                  aStr = input.readLine();
               }
            }
         }
         aHeadline.totalScore = aHeadline.injuryCount * 3 + aHeadline.gameCount
            + aHeadline.businessWords + aHeadline.careerWords
            + (aHeadline.playerNameCount * .5) + aHeadline.BostonCount * .5;
         if ( aHeadline.BostonCount == 0 && aHeadline.playerNameCount == 0 )
            aHeadline.totalScore = 0;
      }
   }

   /**
    * Updates the list of names on the NY Yankees Roster
    * 
    * @throws IOException
    */
   private void updateNames () throws IOException {
      // Document namesDoc =
      // Jsoup.connect("http://espn.go.com/mlb/team/roster/_/name/bos/boston-red-sox")
      // .data("query", "Java")
      Document namesDoc = Jsoup
            .connect(
                  "http://espn.go.com/mlb/team/roster/_/name/nyy/new-york-yankees")
            .data("query", "Java").userAgent("Mozilla").cookie("auth", "token")
            .timeout(10000).get();
      Element table = namesDoc.select("table").first();
      Elements players = table.select("a[href]");
      File playerNamesFile = new File("Words/Players/PlayerNames");

      for (Element player : players) {
         if ( player.toString().indexOf("/mlb/player/") > 0 ) {
            // Get updated file content in a string readerContents
            try (BufferedReader reader = new BufferedReader(new FileReader(
                  playerNamesFile))) {
               String playerReaderText = "";
               String playerLine = reader.readLine();
               while (playerLine != null) {
                  playerReaderText += playerLine;
                  playerLine = reader.readLine();
               }

               // Get last name and check if the file contains it.
               String playerName = GameRecapParser.parseString(player
                     .toString());
               playerName = playerName.substring(playerName.indexOf(' ') + 1);

               if ( playerReaderText.indexOf(playerName) == -1 ) {

                  BufferedWriter playerWriter = new BufferedWriter(
                        new FileWriter(playerNamesFile, true));
                  playerWriter.write(playerName);
                  playerWriter.newLine();
                  playerWriter.close();
               }

            }
         }
      }

   }

   /**
    * Orders the newsheadlines in order of which has the highest total score.
    */
   private void orderHeadlines () {
      ArrayList<NewsHeadline> temp = new ArrayList<NewsHeadline>();
      for (int y = 0; y < newsHeadlines.size();) {
         double highestScore = 0;
         int indexOfHighest = 0;
         for (int x = 0; x < newsHeadlines.size(); x++) {

            if ( newsHeadlines.get(x).totalScore > highestScore ) {
               highestScore = newsHeadlines.get(x).totalScore;
               indexOfHighest = x;
            }
         }
         temp.add(newsHeadlines.get(indexOfHighest));
         newsHeadlines.remove(indexOfHighest);
      }
      newsHeadlines = temp;
   }

   @Override
   public String toString () {
      String toString = "";
      for (NewsHeadline ahl : newsHeadlines)
         toString += (ahl.toString() + " " + ahl.totalScore + "\n");

      return toString;
   }
}
