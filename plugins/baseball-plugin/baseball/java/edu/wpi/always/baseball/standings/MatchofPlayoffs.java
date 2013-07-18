package edu.wpi.always.baseball.standings;

public class MatchofPlayoffs {

   public String nameFirstTeam;

   public String nameFirstTeaminLarge;

   public String nameSecondTeam;

   public String nameSecondTeaminLarge;

   /**
    * which team wins 0: tied 1: First Team 2: Second Team
    */
   public int toLead;

   /**
    * The match is done true: done false: continue
    */
   public boolean defeat;

   public int winFirstTeam;

   public int winSecondTeam;

   public String answerResults;

   public MatchofPlayoffs () {
      nameFirstTeam = null;
      nameSecondTeam = null;
      toLead = 0;
      defeat = false;
      winFirstTeam = 0;
      winSecondTeam = 0;
      answerResults = null;
   }

}
