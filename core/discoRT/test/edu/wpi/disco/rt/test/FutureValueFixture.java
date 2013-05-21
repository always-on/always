package edu.wpi.disco.rt.test;

import static org.junit.Assert.*;
import edu.wpi.disco.rt.util.FutureValue;
import org.junit.*;
import java.util.concurrent.*;

public class FutureValueFixture {

   private FutureValue<Integer> subject;

   @Before
   public void setUp () {
      subject = new FutureValue<Integer>();
   }

   @Test
   public void testSetGet_SingleThread () {
      assertFalse(subject.isSet());
      try {
         subject.set(10);
         assertTrue(subject.isSet());
         assertEquals((Integer) 10, subject.get());
         assertEquals((Integer) 10, subject.get(10));
      } catch (InterruptedException e) {
         fail();
      } catch (ExecutionException e) {
         fail();
      } catch (TimeoutException e) {
         fail();
      }
   }

   @Test
   public void testSetGet_TwoThreads () {
      Thread consumerThread = createAConsumerThread(subject, 20);
      consumerThread.start();
      try {
         consumerThread.join(20);
         if ( !consumerThread.isAlive() )
            fail("consumer thread should not have stopped without setting the ref");
      } catch (InterruptedException e) {
         fail();
      }
      assertTrue(consumerThread.isAlive());
      subject.set(20);
      try {
         consumerThread.join();
      } catch (InterruptedException e) {
         e.printStackTrace();
         fail();
      }
   }

   @Test
   public void testGetWithTimeOut_SingleThread () {
      try {
         long s = System.nanoTime();
         Integer actual = subject.get(30);
         double elapsed = (System.nanoTime() - s) * Math.pow(10, -6);
         assertEquals(30.0, elapsed, 15/* delta */);
         assertFalse(subject.isSet());
         assertNull(actual);
         subject.set(1000);
         actual = subject.get(100000);
         assertTrue(subject.isSet());
         assertEquals((Integer) 1000, actual);
      } catch (InterruptedException e) {
         e.printStackTrace();
         fail();
      } catch (ExecutionException e) {
         e.printStackTrace();
         fail();
      } catch (TimeoutException e) {
         e.printStackTrace();
         fail();
      }
   }

   @Test
   public void testGet_TenThreads () {
      final Integer val = 121;
      Thread[] consumers = createTenConsumerThreads(val);
      try {
         Thread.sleep(50);
         for (int i = 0; i < 10; i++) {
            assertTrue(consumers[i].isAlive());
         }
         subject.set(val);
         joinAll(consumers);
      } catch (InterruptedException e) {
         e.printStackTrace();
         fail();
      }
   }

   private void joinAll (Thread[] consumers) throws InterruptedException {
      for (int i = 0; i < consumers.length; i++) {
         consumers[i].join();
      }
   }

   private Thread[] createTenConsumerThreads (final Integer val) {
      Thread[] consumers = new Thread[10];
      for (int i = 0; i < 10; i++) {
         consumers[i] = createAConsumerThread(subject, val);
         consumers[i].start();
      }
      return consumers;
   }

   @Test
   public void settingTwiceShouldNotCauseAnyProblems () {
      final Integer val = 121;
      Thread[] consumers = createTenConsumerThreads(val);
      subject.set(val);
      try {
         joinAll(consumers);
      } catch (InterruptedException e) {
         e.printStackTrace();
         fail();
      }
      subject.set(1000);
      try {
         assertEquals(1000, (int) subject.get());
      } catch (InterruptedException e) {
         e.printStackTrace();
         fail();
      } catch (ExecutionException e) {
         e.printStackTrace();
         fail();
      }
   }

   private Thread createAConsumerThread (final FutureValue<Integer> r,
         final Integer expectedValue) {
      return new Thread(new Runnable() {

         @Override
         public void run () {
            try {
               assertEquals(expectedValue, r.get());
            } catch (InterruptedException e) {
               fail();
            } catch (ExecutionException e) {
               fail();
            }
         }
      });
   }
}
