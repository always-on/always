package edu.wpi.always.user.places;


public interface PlaceManager {

   Place getPlace (String zip);

   ZipCodes getZipCodes ();

   Place[] getPlaces ();
}
