package edu.wpi.always.cm;

import java.util.List;

public interface DialogContentProvider {

   public String whatToSay ();

   public void doneSaying (String text);

   public List<String> userChoices ();

   public void userSaid (String text);

   public double timeRemaining ();
}
