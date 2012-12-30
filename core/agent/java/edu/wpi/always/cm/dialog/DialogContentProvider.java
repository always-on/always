package edu.wpi.always.cm.dialog;

import java.util.List;

public interface DialogContentProvider {

   String whatToSay ();

   void doneSaying (String text);

   List<String> userChoices ();

   void userSaid (String text);

   double timeRemaining ();
}
