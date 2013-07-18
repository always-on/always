package edu.wpi.always.baseball.standings;

/**
 * This class contains all the variable marking the standing of a team as used
 * in the StandingsParser class.
 * 
 * @author Frederik Clinckemaillie
 */
public class Team {
   public String name;

   public int standing;

   public boolean tiedFlag;

   public double gamesBehind;

   public int win;

   public double winPercent;

   public int loss;

   public int last10Win;

   public Team () {
      tiedFlag = false;
      name = "";
      standing = 5;
      gamesBehind = 100;
      win = 0;
      winPercent = 0;
      loss = 0;
      last10Win = 0;

   }
}
