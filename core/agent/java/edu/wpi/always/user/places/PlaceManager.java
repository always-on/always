package edu.wpi.always.user.places;

import edu.wpi.always.user.ZipCodes;

public interface PlaceManager {

   Place getPlace (String zip);

   ZipCodes getZipCodes ();

   Place[] getPlaces ();
}
