package edu.wpi.always.rm;

public class Starter {

   static FakeCollaborationManager cm;
   static RelationshipManager rm;

   public static void main (String[] args) {
      rm = new RelationshipManager();
      cm = new FakeCollaborationManager(rm);
      System.out.println("Starting Collaboration Manager");
      cm.start();
      System.out.println("Starting Relationship Module");
      rm.start();
      new Shell().loop();
   }
}
