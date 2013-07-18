package edu.wpi.always.baseball.news;

public class NewsHeadline {
   public String name;

   public String date;

   public int gameCount = 0;

   public int playerNameCount = 0;

   public int injuryCount = 0;

   public int careerWords = 0;

   public int BostonCount = 0;

   public int businessWords = 0;

   public double totalScore = 0;

   public NewsHeadline (String name, String date) {
      this.name = name;
      this.date = date;
   }

   @Override
   public String toString () {
      return "\nDate Published: " + date + "\nTitle: " + name;
   }
}
