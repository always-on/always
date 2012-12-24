package edu.wpi.always.user.places;

import edu.wpi.always.user.ZipCodes;

public interface PlaceManager {

   public Place getPlace (String zip);

   public ZipCodes getZipCodes ();

   public Place[] getPlaces ();
}
