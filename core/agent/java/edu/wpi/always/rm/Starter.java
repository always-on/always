package edu.wpi.always.rm;

public class Starter {

   static CollaborationManager cm;
   static RelationshipManager rm;

   public static void main (String[] args) {
      System.out.println("Starting Collaboration Manager");
      cm = new CollaborationManager();
      cm.start();
      System.out.println("Starting Relationship Module");
      rm = new RelationshipManager(cm);
      rm.start();
      Shell.make(args, cm).loop();
   }
}
