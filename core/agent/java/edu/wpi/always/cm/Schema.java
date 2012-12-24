package edu.wpi.always.cm;

//A Schema's run() method will be called periodically by the executor. Do not block the thread.
public interface Schema extends Runnable {

   long DEFAULT_INTERVAL = 500;
}