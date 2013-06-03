package edu.wpi.always.client;

public interface Keyboard {

   void showKeyboard (String prompt);
   
   void hideKeyboard ();
   
   String getInputSoFar ();
}
