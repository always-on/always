// (c) by Fraunhofer IIS, Department Electronic Imaging
//
// PROJECT     : Shore
//
// AUTHOR      : Andreas Ernst, Tobias Ruf
//
// DESCRIPTION : See below.
//
// CHANGED BY  : $LastChangedBy: ruf $
//
// DATE        : $LastChangedDate: 2010-09-20 12:34:38 +0200 (Mon, 20 Sep 2010) $
//
// REVISION    : $LastChangedRevision: 10706 $
//
// start below with your implementation

#ifndef GALLERY_H
#define GALLERY_H


//=============================================================================
// INCLUDES
//=============================================================================
#include "Shore.h"


//=============================================================================

/**
 * NOTE: if you are using a WIN32 static version of this library you must add
 *       SHORE_STATIC to your preprocessor settings .
 */
#if defined( WIN32 ) || defined( WIN64 )
 #ifdef SHORE_STATIC
  #define SHORE_WIN_API
 #else
  #ifdef SHORE_WIN_EXPORTS
   #define SHORE_WIN_API __declspec(dllexport)
  #else
   #define SHORE_WIN_API __declspec(dllimport)
  #endif
 #endif
#else
 #define SHORE_WIN_API
#endif

//=============================================================================


namespace Shore
{


//==============================================================================

/**
 * @brief An Image within a sample.
 *
 *        The interface provides all needed methods to access the internal
 *        data of a specific image. The internal data must be valid as long as
 *        the image exists.
 */
class SHORE_WIN_API Image
{
public:
   /**
    * Returns the left top point of the image.
    */
   virtual unsigned char const* GetLeftTop() const = 0;

   /**
    * Returns the width of the image.
    */
   virtual unsigned long GetWidth() const = 0;

   /**
    * Returns the height of the image.
    */
   virtual unsigned long GetHeight() const = 0;

   /**
    * Returns the number of planes.
    */
   virtual unsigned long GetPlanes() const = 0;

protected:
   /**
    * Protected destructor. Cannot be called across library boundaries.
    */
   virtual ~Image() {}
};


//==============================================================================

/**
 * @brief A Sample within the gallery.
 *
 *        One sample contains an image, an object and optional a so called
 *        record. A record represents the preprocessed image to speedup the
 *        verification. The image, the object and the record data must be valid
 *        as long as the sample exists.
 */
class SHORE_WIN_API Sample
{
public:
   /**
    * Returns the image of the samples
    */
   virtual Image const* GetImage() const = 0;

   /**
    * Returns the corresponding object to the image.
    *
    * @see Shore::Object
    */
   virtual Object const* GetObject() const = 0;

   /**
    * Returns the record of the sample.
    */
   virtual bool GetRecord( char const** data,  unsigned long* size ) const = 0;

   /**
    * Sets the record of the sample.
    */
   virtual void SetRecord( char const* data, unsigned long size ) = 0;

protected:
   /**
    * Protected destructor. Cannot be called across library boundaries.
    */
   virtual ~Sample() {}
};



//==============================================================================

/**
 * @brief A Gallery of samples (e.g. persons) for verification purpose.
 *
 *        The gallery must be valid as long as the engine exits, in which it
 *        is used.
 */
class SHORE_WIN_API Gallery
{
public:
   /**
    * Returns the number of samples contained in the gallery.
    */
   virtual unsigned long GetSampleCount() const = 0;

   /**
    * Returns the i-th sample in the gallery, with i < GetSampleCount(). The
    * returned sample must be valid up to the next call of GetSample().
    */
   virtual Sample* GetSample( unsigned long i ) = 0;

protected:
   /**
    * Protected destructor. Cannot be called across library boundaries.
    */
   virtual ~Gallery() {}
};


} // namespace Shore


#endif // GALLERY_H
