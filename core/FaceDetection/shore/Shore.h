// (c) by Fraunhofer IIS, Department Electronic Imaging
//
// PROJECT     : Shore
//
// AUTHOR      : Andreas Ernst
//
// DESCRIPTION : See below.
//
// CHANGED BY  : $LastChangedBy: kue $
//
// DATE        : $LastChangedDate: 2010-12-14 12:28:28 +0100 (Tue, 14 Dec 2010) $
//
// REVISION    : $LastChangedRevision: 10856 $
//
// start below with your implementation

#ifndef SHORE_H
#define SHORE_H

//==============================================================================

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

//==============================================================================

/**
 * @brief The one and only namespace.
 *
 *        The whole library interface is encapsulated in this namespace.
 */
namespace Shore
{

/**
 * @mainpage SHORE - "Sophisticated High-speed Object Recognition Engine"
 *
 *           (c) copyright by Fraunhofer IIS
 *
 * @section  S0 Introduction to the Library
 *
 *           This library was designed for the purpose of rapid face and object
 *           detection and analysis tasks. The current version is available as
 *           a C++ library for different platforms. The technology is based on
 *           the predecessor the so called rtdlib. The new interface was
 *           designed to offer a flexible and configurable tool for real
 *           time object detection and analysis tasks in still images
 *           as well as in video sequences. There are a few general things
 *           worth to know, which are explained within the following sections.
 * 
 * @section  S1 Getting Started
 *           
 *           Before reading the documentation and the api it might be 
 *           reasonable to try out the simple command line demo tool shipped 
 *           with this library. It shows the functionality on some sample
 *           images. Furthermore it is delivered along with it's source code 
 *           and demonstrates how to integrate and use the library in own 
 *           projects. Just have a look at it and work through the provided 
 *           source code and a lot will become much clearer.
 *            
 * @section  S2 The Engine Class 
 *
 *           The workhorse of this library is the Engine class which 
 *           encapsulates all the facilities. It has a very slim interface
 *           consisting of one single public method only, named Process(). 
 *           Nevertheless it covers a highly flexible and powerful class.
 * 
 *           Because the Engine class has no constructor the free function 
 *           CreateEngine() is offered that takes care of creating new 
 *           instances of an Engine. This function utilizes a scripting 
 *           language (see http://www.lua.org) to gain flexibility for the 
 *           engine configuration. Therefore an engine setup script is needed
 *           as a parameter to CreateEngine().
 *
 *           As the proper setup of an engine requires deep insight into the
 *           library and the scripting possibilities, a separate function
 *           that encapsulates the setup process of an engine for face
 *           detection and analysis is offered in CreateFaceEngine().
 *           This function has understandable and well documented parameters
 *           and can easily be applied. Just call it with the desired arguments
 *           and you will get a properly built engine.
 *           
 *           The CreateFaceEngine() function is shipped in the form of a 
 *           header and a cpp file. Simply add both files to your project and
 *           include the header file where you want to use the function. 
 *           If you look into the cpp file you can read the setup script
 *           (because it's visible as a simple c-string) and if needed also 
 *           make some minor adaptations.
 *
 *           After the creation of an engine you can apply it to your images
 *           or video sequences. The only method available and needed for this
 *           purpose is Engine::Process(). The call to this method with the 
 *           desired image as a parameter returns a pointer to a Content 
 *           instance that contains all the information that was gathered by 
 *           the engine. 
 * 
 *           If you are finished with processing images and you don't need the
 *           engine any more don't forget to delete it by calling
 *           DeleteEngine() on it. Otherwise memoryleaks will occour.
 *           Because setting up an engine is a quite time consuming process 
 *           you should create the engines you need at the beginning of your
 *           application and delete them not until you don't need them any
 *           more.
 *
 *           If you have any questions concerning the engine setup or if you
 *           need to modify the setup provided in the CreateFaceEngine() 
 *           function don't hesitate to ask us for customized setups.
 *
 * @section  S3 Definition of the Coordinate System
 *
 *           The cartesian coordinate system used in this library is located in
 *           the upper left corner of the image (strictly speaking in the upper
 *           left corner of the upper left pixel). The positive x-axis points to
 *           the right and the positive y-axis points downwards. The coordinates
 *           within the image region are defined by 0 to image width and 0 to
 *           image height respectively. All markers and regions of objects
 *           returned by the library are provided with respect to this
 *           coordinate system.
 *
 * @section  S4 Using Models in the Library
 *
 *           The detection and analysis tasks within the library are
 *           based on models. These models must be previously built in a
 *           separate training step. The algorithms used for the training of
 *           new models are not included in the library. Different kinds of
 *           models are optionally shipped with the library. They are coded in
 *           separate cpp files and can be included in the destination project
 *           if required. If you have special needs and an appropriate model
 *           is not already shipped with the library, don't hesitate to contact
 *           us. Customized models can be trained upon request.
 * 
 *           To make a model available within the library, two steps are
 *           necessary. Firstly the model cpp file must be added to the
 *           destination project, where this library is used. Secondly a
 *           registration step is necessary. Within the model cpp files an
 *           auto-registration code is implemented. To activate this code the
 *           tag SHORE_REGISTER_MODELS must be defined in the project
 *           preprocessor settings. Alternatively the registration can be done
 *           manually somewhere in the destination project, before the model is
 *           actually used in an Engine setup process. Check the RegisterModel()
 *           documentation or look into one of the model cpp files on how to
 *           do that. As soon as a model was registered it can be used for the
 *           setup of an Engine. 
 *
 *           In some cases the automatic model registration can fail, even 
 *           if SHORE_REGISTER_MODELS was defined. So although you did it 
 *           right you may receive a message (see the function 
 *           SetMessageCall()) during the CreateFaceEngine() call that the 
 *           model was not registered and is unknown. In this case you have
 *           to call Shore::RegisterModel() manually before creating an engine.
 *           Just look into the appropriate model cpp file on how to do that.
 *
 * @section  S5 The Purpose of Lua
 *
 *           As already mentioned earlier the library uses Lua
 *           (see http://www.lua.org) for some of it's facilities. For a short
 *           introduction to Lua see http://www.lua.org/about.html
 *           Lua is a fast, light-weight and embeddable scripting language.
 *           Within this library it accomplishes different tasks. One of them
 *           is to provide the scripting facilities available in the
 *           free CreateEngine() function. There it offers the possibility to 
 *           setup engines very flexible and fast especially during the 
 *           development and testing phase.
 *
 * @section  S6 Update to Version 1.4.0 from Previous Versions
 *
 *           A lot of internal things changed and have been improved compared
 *           to the previous library versions. Nevertheless an update to the
 *           current version of the library should be quite easy. All the old
 *           model files must be removed from the project and replaced with 
 *           the new ones named in the CreateFaceEngine documentation. Also 
 *           the CreateFaceEngine header and cpp file and the Shore-dll and
 *           lib must be replaced with the updated version. After that don't
 *           forget to adapt your call to CreateFaceEngine to the new extended
 *           function parameters (even if your project compiles without this,
 *           check your call to CreateFaceEngine, because a lot of the 
 *           parameters changed). Last but not least check whether the new
 *           engine still returns the object properties that you need in the
 *           same attributes, ratings, parts and so on (what exactly is 
 *           returned by an engine depends on the CreateFaceEngine parameters
 *           and is well documented in the CreateFaceEngine.h file).
 *
 * @section  S7 Version History
 *
 * @subsection V140 Version 1.4.0
 *           Improvements have been added to this release:
 *           The detection speed has been slightly improved, especially for 
 *           scenarios with multiple faces. The age estimation has been improved
 *           and now is depending less from the distance / face size. A module 
 *           for phantom detection has been implemented. Detections that are
 *           unlikely to be a face and more likely to belong to the background
 *           in static camera setups can no be recognised as "phantoms"
 *
 * @subsection V130 Version 1.3.0
 *
 *           New features have been added to this release (age estimation,
 *           out-of-plane detection, temporary recovery of the object id,
 *           detection of smaller faces by image upscaling). Replacement of
 *           the boost serialization by an own implementation. Some thresholds
 *           for gender and expression classification have been adapted. Some
 *           object types and attributes have been changed. 
 *
 * @subsection V120 Version 1.2.0
 * 
 *           A lot of internal improvements. New model class hierarchy. Newly
 *           trained detection and analysis models. Additional features (nose
 *           and mouth detection, eyes open/closed analysis, in-plane rotated
 *           face detection, object tracking facility, ...). Gender analysis
 *           is filtered over time in the video mode. Improved analysis steps
 *           by using a faster and more precise image transformation.
 *
 * @subsection V112 Version 1.1.2
 *
 *           Fixed multithreading problem when calling the CreateEngine function
 *           simultaneously in different threads.
 *
 * @subsection V111 Version 1.1.1
 *
 *           Fixed multithreading problem when using different engine instances
 *           in different threads by putting a mutex around a function call to
 *           a not thread safe 3rd-party library function.
 *           Fixed broken gender model file.
 *
 * @subsection V110 Version 1.1.0
 *
 *           First official release of the new library interface. Modified
 *           some internal details. Added some facilities to the setup 
 *           scripting language. Added absolut object size constraints to the
 *           CreateFaceEngine() function and the library. Removed exact time
 *           measurement on windows platform. Removed the TimeStamp and
 *           ProcessTime in the engine returned by CreateFaceEngine. Converted
 *           the models to a new and platform independent format. Modified the
 *           CreateFaceEngine script.
 *
 * @subsection V100 Version 1.0.0
 *
 *           Initial release of the new library interface.
 *
 * @section  S8 Copyright Information
 *
 *           @verbinclude License.txt
 *
 * @section  S81 Copyright of 3rd Party Libraries
 *
 *           The library depends and uses three different 3rd party libraries
 *           or packages which have their own copyright notice.
 *
 * @subsection SS811 Boost
 *           @verbinclude LICENSE_1_0.txt
 *
 * @subsection SS812 Lua
 *           @verbinclude COPYRIGHT
 *
 * @subsection SS813 VXL
 *           @verbinclude vxl_copyright.h
 */

//==============================================================================

/**
 * Returns the version of the library.
 */
SHORE_WIN_API char const* Version();


/**
 * Normally messages are sent to the standard error channel. If you want them
 * to appear somewhere else you have to set a message function which will be
 * called to provide remarks, warnings and errors to the user, when something
 * failed or went wrong.
 *
 * @param[in] messageCall
 *            The function which will be called with the message as a parameter
 *            when something failed. By providing 0 the standard error channel
 *            is set or set again as the error channel.
 */
SHORE_WIN_API bool SetMessageCall( void (*messageCall)( const char* ) );

//==============================================================================

/**
 * @brief A point in the image.
 *
 *        Simple class to define a point within an image by the x- and
 *        y-coordinate.
 */
class SHORE_WIN_API Marker
{
public:
   /**
    * Returns the x-coordinate of the marker.
    */
   virtual float GetX() const = 0;

   /**
    * Returns the y-coordinate of the marker.
    */
   virtual float GetY() const = 0;

protected:
   /**
    * Protected destructor. Cannot be called outside the library.
    */
   virtual ~Marker() {}
};

//==============================================================================

/**
 * @brief A region in the image.
 *
 *        Simple class to define a region within an image by four borders.
 */
class SHORE_WIN_API Region
{
public:
   /**
    * Returns the left border of the region.
    */
   virtual float GetLeft()   const = 0;

   /**
    * Returns the top border of the region.
    */
   virtual float GetTop()    const = 0;

   /**
    * Returns the right border of the region.
    */
   virtual float GetRight()  const = 0;

   /**
    * Returns the bottom border of the region.
    */
   virtual float GetBottom() const = 0;

protected:
   /**
    * Protected destructor. Cannot be called outside the library.
    */
   virtual ~Region() {}
};

//==============================================================================

/**
 * @brief An object within the image.
 *
 *        This class describes an object within an image. The object can be
 *        defined by a type (e.g. "Face"), a region within the image, arbitrary
 *        markers with keys and positions within the image
 *        (e.g. "LeftEye" -> x=30.5, y=40.3), arbitrary attributes as key value
 *        pairs (e.g. "Gender"="Male"), arbitrary ratings (e.g. "Score" = 68)
 *        and arbitrary parts which are itself objects.
 */
class SHORE_WIN_API Object
{
public:
   /**
    * Returns the type of the object, 0 if no type is available.
    */
   virtual char const* GetType() const = 0;

   /**
    * Returns the region of the object, 0 if no region is available.
    */
   virtual Region const* GetRegion() const = 0;

   /**
    * Returns the number of object markers.
    */
   virtual unsigned long GetMarkerCount() const = 0;

   /**
    * Returns the key of marker i if i < GetMarkerCount(), 0 otherwise.
    */
   virtual char const* GetMarkerKey( unsigned long i ) const = 0;

   /**
    * Returns the marker i if i < GetMarkerCount(), 0 otherwise.
    */
   virtual Marker const* GetMarker( unsigned long i ) const = 0;

   /**
    * Returns the marker with the given key if available, 0 otherwise.
    */
   virtual Marker const* GetMarkerOf( char const* key ) const = 0;

   /**
    * Returns the number of object attributes.
    */
   virtual unsigned long GetAttributeCount() const = 0;

   /**
    * Returns the key of attribute i if i < GetAttributeCount(), 0 otherwise.
    */
   virtual char const* GetAttributeKey( unsigned long i ) const = 0;

   /**
    * Returns the attribute i if i < GetAttributeCount(), 0 otherwise.
    */
   virtual char const* GetAttribute( unsigned long i ) const = 0;

   /**
    * Returns the attribute with the given key if available, 0 otherwise.
    */
   virtual char const* GetAttributeOf( char const* key ) const = 0;

   /**
    * Returns the number of ratings.
    */
   virtual unsigned long GetRatingCount() const = 0;

   /**
    * Returns the key of rating i if i < GetRatingCount(), 0 otherwise.
    */
   virtual char const* GetRatingKey( unsigned long i ) const = 0;

   /**
    * Returns the rating i if i < GetRatingCount(), 0 otherwise.
    */
   virtual float const* GetRating( unsigned long i ) const = 0;

   /**
    * Returns the rating with the given key if available, 0 otherwise.
    */
   virtual float const* GetRatingOf( char const* key ) const = 0;

   /**
    * Returns the number of parts in this object.
    */
   virtual unsigned long GetPartCount() const = 0;

   /**
    * Returns the key of part i if i < GetPartCount(), 0 otherwise.
    */
   virtual char const* GetPartKey( unsigned long i ) const = 0;

   /**
    * Returns the part i if i < GetPartCount(), 0 otherwise.
    */
   virtual Object const* GetPart( unsigned long i ) const = 0;

   /**
    * Returns the part with the given key if available, 0 otherwise.
    */
   virtual Object const* GetPartOf( char const * key ) const = 0;

protected:
   /**
    * Protected destructor. Cannot be called outside the library.
    */
   virtual ~Object() {}
};

//==============================================================================

/**
 * @brief The content of an image.
 *
 *        The Engine:Process() returns this interface type. It can have an
 *        arbitrary number of objects and arbitrary image info as key value
 *        pairs.
 */
class SHORE_WIN_API Content
{
public:
   /**
    * Returns the number of objects.
    */
   virtual unsigned long GetObjectCount() const = 0;

   /**
    * Returns the object i if i < GetObjectCount(), 0 otherwise.
    */
   virtual Object const* GetObject( unsigned long i ) const = 0;

   /**
    * Returns the number of image info.
    */
   virtual unsigned long GetInfoCount() const = 0;

   /**
    * Returns the key of info i if i < GetInfoCount(), 0 otherwise.
    */
   virtual char const* GetInfoKey( unsigned long i ) const = 0;

   /**
    * Returns the info i if i < GetInfoCount(), 0 otherwise.
    */
   virtual char const* GetInfo( unsigned long i ) const = 0;

   /**
    * Returns the info with the given key if available, 0 otherwise.
    */
   virtual char const* GetInfoOf( char const* key ) const = 0;

protected:
   /**
    * Protected destructor. Cannot be called outside the library.
    */
   virtual ~Content() {}
};

//==============================================================================

/**
 * @brief The workhorse of the library.
 *
 *        The engine covers a highly flexible and powerful class. An engine
 *        can be created and customized by the free CreateEngine() function. 
 *        For a more detailed description see the appropriate section in the 
 *        introduction of the library \ref S2.
 */
class SHORE_WIN_API Engine
{
public:
   /**
    * This is the only public method of the class. It applies the engine to 
    * the given image. Calling it returns the result on success and 0
    * if something failed. Take care that the returned pointer is only valid
    * until the next call to Process and as long as this engine is alive
    * respectively! At the moment only grayscale images are supported.
    * Nevertheless if you want to process a color image normally you don't 
    * have to convert it to grayscale manually. Instead of that you can 
    * provide one single plane of the color image as a graylevel image. 
    * Which channel fits best depends on the job. For the face detection task
    * the red channel emerged to provide the best results. Even if the color 
    * image is pixel interleaved it is possible to process it directly by
    * choosing the pixelFeed and lineFeed parameter properly. Take care that
    * the provided image data must stay valid until the Process call returns.
    * And don't call process of one engine simultaneously in different threads.
    * Use different engine instances to process different streams or images 
    * simultaneously in different threads.
    *
    * @param[in] leftTop
    *            Pointer to the first plane of the left top pixel in the image.
    *
    * @param[in] width
    *            Width of the image.
    *
    * @param[in] height
    *            Height of the image.
    *
    * @param[in] planes
    *            Number of planes of the image. This must correspond to the
    *            provided colorSpace below. As only "GRAYSCALE" is supported
    *            at the moment it must always be 1.
    *
    * @param[in] pixelFeed
    *            What must be added to leftTop to access the pixel on the right
    *            next to leftTop on the same plane.
    *
    * @param[in] lineFeed
    *            What must be added to leftTop to access the same pixel in the
    *            next line and on the same plane.
    *
    * @param[in] planeFeed
    *            What must be added to leftTop to get the same pixel in the 
    *            next plane. If planes = 1, this value will be disregarded.
    *            As only "GRAYSCALE" is supported at the moment and planes must
    *            always be 1 this parameter is currently not used.
    *
    * @param[in] colorSpace
    *            Describes the color space of the given image. At the moment
    *            only "GRAYSCALE" with one single plane is supported.
    *
    * @return    Returns a pointer to the result on success, and 0 otherwise.
    *            Pay attention that the pointer is only valid until the next
    *            call to Process and as long as this engine is alive
    *            respectively.
    */
   virtual Content const* Process( unsigned char const* leftTop,
                                   unsigned long width,
                                   unsigned long height,
                                   unsigned long planes,
                                   long pixelFeed,
                                   long lineFeed,
                                   long planeFeed,
                                   char const* colorSpace ) = 0;

protected:
   /**
    * Protected destructor. Cannot be called outside the library.
    */
   virtual ~Engine() {}
};

//==============================================================================

/**
 * Creates an engine using a Lua script. It returns the pointer to the engine 
 * on success, otherwise 0. Take care to call DeleteEngine() on the returned
 * engine if it is not used any more! Otherwise memoryleaks may occour. For a
 * more detailed description see the appropriate section in the introduction of
 * the library \ref S2.
 *
 * @param[in] setupScript
 *            Lua script with the definition of the engine setup. It may
 *            return the engine at the end of the script. In this case 
 *            setupCall must be 0.
 *
 * @param[in] setupCall
 *            If setupScript returns an engine at the end of the script, this 
 *            must be 0. Otherwise you have to provide the function call that
 *            is used at the end of the script to create the engine.
 */
SHORE_WIN_API Engine* CreateEngine( char const* setupScript, 
                                    char const* setupCall );


/**
 * Deletes the given engine. Returns 0 on success and the provided engine
 * pointer if something failed. It must be called when the engine is not used
 * any more. Otherwise memoryleaks may occour.
 */
SHORE_WIN_API Engine* DeleteEngine( Engine* engine );


/**
 * Registers a model in the library. Returns true on success otherwise false.
 * Take care not to mistake the modelName and modelData argument, because they
 * have the same type and therefore are not checked by the compiler. It is
 * possible to reregister a model or register a new model under an already
 * used name.
 *
 * IMPORTANT: The modelData pointer must be valid as long as you want to 
 * create new engines.
 *
 * @param[in] modelName
 *            The name of the model. This will be used to access an instance
 *            of this model in the setup script of CreateEngine().
 *
 * @param[in] modelData
 *            Pointer to the model data. This pointer must stay valid as long
 *            as you create new engines.
 */
SHORE_WIN_API bool RegisterModel( char const* modelName,
                                  char const* modelData );


} // namespace Shore


#endif // SHORE_H



