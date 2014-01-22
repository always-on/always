// (c) by Fraunhofer IIS, Department Electronic Imaging
//
// PROJECT     : Shore
//
// AUTHOR      : Andreas Ernst, Tobias Ruf
//
// DESCRIPTION : See below.
//
// CHANGED BY  : $LastChangedBy:  $
//
// DATE        : $LastChangedDate: $
//
// REVISION    : $LastChangedRevision: $
//
// start below with your implementation

#ifndef CREATEFACEENGINE_H
#define CREATEFACEENGINE_H

#include "Shore.h"
#include "Gallery.h"

//==============================================================================

namespace Shore
{


/**
 * What exactly can be returned by the engine highly depends on the provided
 * parameters. If only the face detection mode is used, the engine will return
 * objects of type 'Face', with a region defining the face border. The face
 * object has three attributes with key 'Roll', 'Pitch' and 'Yaw' that
 * indicate the orientation of the face. Depending on the modelType parameter
 * the face object also has markers that describe roughly the position of the
 * eyes, the nose or the mouth corners, for details see the modelType parameter
 * documentation. The face object also has a rating with key 'Score' that
 * describes how likely it is to be a face. The higher the score the more it
 * seems to be a face (and not a false detection). The score of the faces is at
 * least the provided minFaceScore parameter, because faces with a lower score
 * are not returned. In the video mode each face object gets an additional
 * unique identity number stored in the attribute with key 'Id'. Furthermore
 * the objects have an additional 'Uptime' rating, that shows how long an 
 * object has already been detected. Depending on the used classification 
 * modules the face objects get additional ratings and attributes as described
 * in the parameter documentation. Just try out the command line 'Demo' 
 * application shipped with the library with different parameters. It prints 
 * out exactly what the engine returns.\n
 * \n
 * Depending on the parameter values, different kinds of models are needed for
 * the engine setup. The required models are named in the appropriate
 * parameter descriptions. You must add the needed models to your project and
 * register them in the Shore library. See Shore::RegisterModel() and the
 * introduction to the library on how to do that. Otherwise this function
 * returns a null pointer and a message with the unknown model name is 
 * generated.\n
 * \n
 * @param timeBase
 *        Video sampling interval in [s]. 0 means video filtering off (single
 *        image mode). E.g. a video of 25 frames/second would require a value of
 *        0.04 (40ms). If updateTimeBase is true then the value given here (if
 *        different from 0) is only used as a first estimate and is updated
 *        automatically during runtime. The valid range of this parameter
 *        is [0, 10].\n
 *        \n
 * @param updateTimeBase
 *        If updateTimeBase == true the automatic estimation and adaption of the
 *        current time base is turned on. Otherwise turned off. This parameter
 *        takes only effect if timeBase > 0 (video mode). See also the timeBase
 *        parameter.\n
 *        \n
 * @param threadCount
 *        The number of threads used for processing. If you run your application
 *        on a machine with more than one single cpu, it typically will be
 *        faster to use all cpus. Notice that the result of processing an image
 *        can be slightly different from the single threaded version. The number
 *        of threads is limited to 10. But it only makes sense to provide at
 *        most the number of available cpus available.\n
 *        \n
 * @param modelType
 *        Defines which model is used to detect faces. Valid values are 
 *        'Face.Front', 'Face.Rotated' and 'Face.Profile'. The returned face
 *        objects also have the three attributes with key 'Roll', 'Pitch' and
 *        'Yaw'. The 'Roll' attribute describes roughly the in-plane rotation of
 *        the face. The 'Pitch' attribute indicates the tilt of the face. The
 *        pitch of a face can be compared with nodding. The 'Yaw' attribute
 *        describes roughly the out-of-plane rotation, e.g. for profile faces
 *        the yaw is '-90' and '90' respectively.\n
 *        \n  
 *        For the value 'Face.Front' a model is used to detect upright frontal 
 *        faces. This requires the model 
 *        'FaceFront_24x24_2008_08_29_161712_7.cpp' to be added to the project
 *        and registered. All returned face objects will have the value '0' for
 *        the 'Roll', 'Pitch' and 'Yaw' attributes. The face objects also have
 *        two markers with key 'LeftEye' and 'RightEye' that describe
 *        roughly the position of both eyes. \n
 *        \n
 *        Providing 'Face.Rotated' as value, a detection model is used that is  
 *        able to detect -60 to +60 degree in-plane rotated faces. This requires
 *        the model 'FaceRotated_24x24_2008_10_15_180432_24.cpp' to be added to
 *        the project and registered. For rotated face objects the attribute
 *        'Roll' contains the in-plane rotation ('-60','-45', '-30', '-15',
 *        '0', '15', '30', '45', '60'). The 'Pitch' and 'Yaw' attributes will
 *        have the value '0'. The rotated face objects also have two markers
 *        with key 'LeftEye' and 'RightEye' that describe roughly the position
 *        of both eyes.\n
 *        \n
 *        For the value 'Face.Profile' a model is used that is able to detect 
 *        upright frontal and out-of-plane rotated faces. This requires the 
 *        model 'Face_24x24_2009_09_02_185611_48.cpp' to be added to the
 *        project and registered. The 'Yaw' attribute contains the out-of-plane
 *        rotation ('-90', '-45', '0', '45', '90'). In addition the model also
 *        detects profile faces (yaw equal to '-90' or '90') that are pitched.
 *        Possible values for the attribute 'Pitch' are '-20', '0' and '20'.
 *        For the 'Yaw' values '-45', '0' and '45' the face objects have the
 *        three markers with key 'LeftEye', 'RightEye' and 'NoseTip' that
 *        describe roughly the position of both eyes and the nose tip. If the
 *        value of the 'Yaw' attribute equals '90', the face objects have three
 *        markers with key 'LeftEye', 'NoseTip' and 'LeftMouthCorner'. For
 *        the value '-90' of the 'Yaw' attribute the face objects have three
 *        markers with key 'RightEye', 'NoseTip' and 'RightMouthCorner'. \n
 *        \n
 *        Keep in mind that the rotated and profile models do not have the same
 *        performance than the upright frontal model (lower detection rate and 
 *        more false detections). And please consider that the analysis and fine
 *        search modules only act on face if the 'Roll' attribute equals '-15',
 *        '0' or '15', the 'Pitch' attribute equals '0' and the 'Yaw' attribute
 *        equals '-45', '0' or '45'.\n
 *        \n
 * @param imageScale
 *        This parameter resizes the original input image using this scaling
 *        factor for the internal processing. The valid range is in ]0,3]. The
 *        value 1 leaves the image in the original size. This parameter has two
 *        purposes. On the one hand you can use it to downscale the input image.
 *        This can make sense if you are only interested in faces bigger then
 *        24x24 pixel and want to speed up the detection. On the other hand this
 *        parameter can be used to upscale the input image to find faces even
 *        smaller then 24x24 pixel. Pay attention that downscaling the input
 *        image can have drawbacks on the detection performance. If you want to
 *        find only bigger faces with the full detection performance it is
 *        recommended to limit the minimal face size via the minFaceSize
 *        parameter. If speed is important downscaling the input image could be
 *        a good choice. To detect faces smaller then 24x24 pixel the only
 *        option is to upscale the input image. Please pay attention that the
 *        minFaceSize parameter must be set to an appropriate value in this 
 *        case. For example if you want to find faces with at least 12x12
 *        pixel in size, you must set the image scale to 2 and the minimal face
 *        size at most to 12*12, 0 or a proper relative value depending on the
 *        input image size. The input image must be sharp for good results on
 *        small faces. See also the minFaceSize parameter documentation.\n
 *        \n
 * @param minFaceSize
 *        This parameter limits the minimal face size that will be returned. It
 *        must be greater or equal to 0. If it is in the range [0, 1] it is
 *        taken with respect to the original image size. If it is greater than
 *        1.0 it is the absolute minimal number of pixels a face will have in
 *        the original image. Thus if you want the minimal face size to take at
 *        most a quarter of the image area you have to provide 0.25 for this
 *        parameter. If you want to detect faces with a minimal size of about
 *        100*100 pixels in the image just provide 100*100 for this parameter.
 *        Regard that this parameter is only the lower bound for the face size.
 *        The faces actually returned by the engine will normally be slightly
 *        bigger. So choose this parameter generous. Although you can provide 0
 *        as the face size, the minimal detected face size is limited by the
 *        used model size which is at the moment 24x24 pixel. But you can set
 *        the imageScale parameter to 3 to find even faces of 8x8 pixel in size.
 *        Consider that limiting the minimal face size often speeds up the
 *        detection process without drawbacks on the detection performance. See
 *        also the imageScale parameter documentation.\n
 *        \n
 * @param minFaceScore
 *        This is the minimal face detection threshold. Each face has a rating
 *        with key 'Score' that indicates how likely it is to be a face. Using
 *        this parameter you can set a minimal score for the faces that will be
 *        returned by the engine. The lower this value is the more faces but
 *        also false detections will be found. A good choice for the model type
 *        'Face.Front' and 'Face.Rotated' is 9 and for 'Face.Profile' 16. A
 *        reasonable range for all models is in [0, 60].\n
 *        \n
 * @param idMemoryLength
 *        This parameter takes only effect if timeBase > 0 (video mode). As
 *        already mentioned above, in the video mode an identity number is
 *        assigned to each face in the object attribute with key 'Id' which is
 *        constant as long as the trajectory can be determined without
 *        discontinuity. This parameter defines how long (in seconds) a face 
 *        is remembered internally, so that a connection of a currently
 *        detected face to the same face detected some time ago can be made.
 *        How this connection is made and which information is returned by the
 *        engine is defined by the parameter idMemoryType. The valid range of
 *        idMemoryLength is [0, 180].\n
 *        \n
 * @param idMemoryType
 *        This parameter takes only effect if timeBase > 0 (video mode) and if 
 *        idMemoryLength > 0. In this case valid arguments are "Spatial",
 *        "Recent" or "All". Their meaning is described in the following. 
 *        For "Spatial" the face id is remembered internally and assigned
 *        again if a face is lost and found again at approximately the same
 *        position in the image. However if a face is lost and found again in
 *        a different position a new id is assigned. This is the fastest method
 *        of the three. \n
 *        For "Recent" and "All" 'FaceFrontId_36x44_2009_08_07_122105.cpp' must
 *        be added to the project and registered. In both cases 'fingerprints'
 *        of the faces are temporarily remembered and used to create links
 *        between newly detected faces and faces already detected some time 
 *        ago. Therefore it can be quite a time and memory consuming task, if
 *        there appear a lot of new faces in the image. The more and longer the
 *        fingerprints of the faces are stored, the more time consuming this
 *        task can be. Thus keep idMemoryLength as low as reasonable. The 
 *        number of faces currently remembered is available in the content
 *        info with the key 'GallerySize'. The number of views of currently
 *        memorized faces in the gallery is available in the object attribute
 *        with the key 'ViewCount'. If idMemoryType is "Recent" and
 *        links to previously detected faces has been found the object gets
 *        attributes with key 'RecentId_0' to 'RecentId_N' that contain the id
 *        or ids of the faces when they where detected the last time. In case
 *        of "All" the object gets an attribute with key 'PreviousIds' that
 *        contains a space separated list of not only the last ids (as in case
 *        of 'Recent' but ALL the ids this face had when it was previously 
 *        detected. Take care that this is dangerous in a static environment
 *        where some insistent false detections appear again and again! In this
 *        case the list may become very very long. (To avoid this see parameter 
 *        @ref phantomTrap) Prefer the 'Recent' setup if possible, because it 
 *        remembers and returns only the ids when the face was detected the last
 *        time. In both cases it may take some time or frames until links can be
 *        established.\n
 *        \n
 * @param trackFaces
 *        This parameter takes only effect if timeBase > 0 (video mode). If it
 *        is true an object tracker is activated that tries to track the faces
 *        that were lost by the face detection module. In this case the object
 *        type is changed to 'Face.Tracked'. Take care that the fine search and
 *        analysis modules below do not act on tracked faces. If this parameter
 *        is true 'Tracking_36x36_2008_08_21_110315.cpp' must be added to the
 *        project and registered.\n
 *        \n
 * @param phantomTrap 
          @anchor phantomTrap
 *        This parameter takes only effect if timeBase > 0 (video mode). The 
 *        module tries to find false detections (phantoms) which are left 
 *        over by the previous modules. It is currently desgined for \p static 
 *        mounted cameras. The default mode is 'Off'. Usage of this module 
 *        requires the model 'Tracking_36x36_2008_08_21_110315.cpp'. It have to 
 *        be added to the project and registered. \n
 *        \n
 *        Following modes are possible: 'Off', 'Delete', 'Mark' \n
 *        \li 'Off'   : the module is not used
 *        \li 'Delete': false detections are identified and deleted on the fly. 
 *        \li 'Mark'  : identified false detections are marked by the module.
 *                  Each object will have an additional attribute with the key
 *                  'Phantom'. The value can be 'Yes' or 'No'. No objects are
 *                  deleted in this mode. This option makes only sense, if you
 *                  postprocess the added metadata.
 *        \n \n
 * @param searchEyes
 *        If true an eye fine search module is added to the engine. This
 *        requires the models 'LeftEyeFront_16x16_2008_10_20_190938_4.cpp' and
 *        'RightEyeFront_16x16_2008_10_20_190953_4.cpp' to be registered. The
 *        eye fine search works better if the face has a minimal size of about
 *        36x36 pixel in the image (for the used models). If the eye fine search
 *        was successful, the appropriate face object will get a part named
 *        'LeftEye' and 'RightEye' respectively. The marker positions 'LeftEye'
 *        and 'RightEye' of the parts will also be transfered to the face
 *        object. Note that if you use one of the additional analyzer modules
 *        you should also turn the eye fine search on to get better results.\n
 *        \n
 * @param searchNose
 *        If true a nose fine search module is added to the engine. This
 *        requires the model 'NoseFront_16x16_2008_10_17_134731_4.cpp' to be
 *        registered. The nose fine search works better if the face has a
 *        minimal size of about 36x36 pixel in the image (for the used model).
 *        If the nose fine search was successful, the appropriate face object
 *        will get a part named 'Nose'. The marker 'NoseTip' of the part will
 *        also be transfered to the face object.\n
 *        \n
 * @param searchMouth
 *        If true a mouth fine search module is added to the engine. This
 *        requires the model 'MouthFront_16x14_2008_10_20_190419_4.cpp' to be
 *        registered. The mouth fine search works better if the face has a
 *        minimal size of about 36x36 pixel in the image (for the used model).
 *        If the mouth fine search was successful, the appropriate face object
 *        will get a part named 'Mouth'. The markers 'LeftMouthCorner' and
 *        'RightMouthCorner' of the part will also be transfered to the face
 *        object.\n
 *        \n
 * @param analyzeEyes
 *        If true a analysis step of the eyes is added to the engine. This
 *        requires the models 'LeftEyeClosed_16x16_2008_10_23_185544.cpp' and
 *        'RightEyeClosed_16x16_2008_10_23_185544.cpp' to be registered. Each
 *        face object will get two ratings with the keys 'LeftEyeClosed' and
 *        'RightEyeClosed' which indicate how closed the eyes are. The ratings
 *        are both in the range [0, 100]. The higher the ratings, the more
 *        the eyes were classified as closed. The rating for open eyes is 0.
 *        It is recommended to turn the eye search on.\n
 *        \n
 * @param analyzeMouth
 *        If true a mouth analysis step is added to the engine. This requires
 *        the model 'MouthOpen_16x14_2008_10_23_185229.cpp' to be registered.
 *        Each face object will get a rating with key 'MouthOpen' that indicates
 *        how wide the mouth is open. The rating is in the range [0, 100]. The
 *        higher the rating, the more the mouth is open. The rating for a closed
 *        mouth is 0. It is recommended to turn the eye search on (mouth search
 *        is not necessary in this case).\n
 *        \n
 * @param analyzeGender
 *        If true a gender analysis step is added to the engine. This requires
 *        the model 'Gender_26x26_2008_09_04_174103.cpp' to be registered. Each
 *        face object will get an attribute with key 'Gender' that can be
 *        'Female' or 'Male' (and also empty '' in the video mode depending on
 *        the result). In the video mode this attribute is filtered over time.
 *        Furthermore the face gets two ratings with the keys 'Female' and
 *        'Male', which indicate whether it is more likely to be a female or
 *        male face. The ratings are both in the range [0, 100]. The higher the
 *        ratings, the more it was classified as male or female. Pay attention
 *        that the gender model was trained mainly for European adult faces at
 *        the moment. It is recommended to turn the eye search on. \n
 *        \n 
 * @param analyzeAge
 *        If true a age estimation step is added to the engine. This requires
 *        the model 'Age_28x28_2009_09_17_131241.cpp' to be registered. Each
 *        face object will get two additional ratings with the keys 'Age' and
 *        'AgeDeviation'. The value of the 'Age' rating is the estimated age in
 *        years, which ranges in most cases from 0 to 90 years. The
 *        'AgeDeviation' rating indicates the deviation (mean absolute error)
 *        of the age estimation on two public available test sets. For more
 *        information about the test sets please contact us. Considering the
 *        estimated age and the deviation a believable range for the real age
 *        can be defined by estimated age +/- deviation. Pay attention that the
 *        age estimation was trained mainly for European faces at the moment.
 *        It is recommended to turn the eye search on.\n
 *        \n
 * @param analyzeHappy
 *        If true a happy analysis step is added to the engine. This requires
 *        the model 'Happy_26x26_2008_09_08_124526.cpp' to be registered. Each
 *        face object will get a rating with key 'Happy' that indicates how much
 *        it is the case. The rating is in the range [0, 100]. The higher the
 *        rating, the more it was classified as happy. It is recommended to turn
 *        the eye search on. \n
 *        \n
 * @param analyzeSad
 *        If true a sad analysis step is added to the engine. This requires the
 *        model 'Sad_26x26_2008_10_21_161703.cpp' to be registered. Each face
 *        object will get a rating with key 'Sad' that indicates how much it is
 *        the case. The rating is in the range [0, 100]. The higher the rating,
 *        the more it was classified as sad. It is recommended to turn the eye
 *        search on.\n
 *        \n
 * @param analyzeSurprised
 *        If true a surprised analysis step is added to the engine. This 
 *        requires the model 'Surprised_26x26_2008_09_11_175815.cpp' to be 
 *        registered. Each face object will get a rating with key 'Surprised' 
 *        that indicates how much it is the case. The rating is in the range 
 *        [0, 100]. The higher the rating, the more it was classified as 
 *        surprised. It is recommended to turn the eye search on.
 *        \n
 * @param analyzeAngry
 *        If true a angry analysis step is added to the engine. This requires 
 *        the model 'Angry_26x26_2008_10_21_152601.cpp' to be registered. Each 
 *        face object will get a rating with key 'Angry' that indicates how much
 *        it is the case. The rating is in the range [0, 100]. The higher the
 *        rating, the more it was classified as angry. It is recommended to turn
 *        the eye search on.\n
 *        \n
 * @param gallery
 *        If gallery is 0 this parameter has no effect. If a valid pointer to a 
 *        gallery interface is provided, an additional face identification step
 *        is added to the engine. All the objects in the gallery must provide
 *        valid markers with key 'LeftEye' and key 'RightEye'. Additionally
 *        the objects must have an attribute with key 'Identity' that contains
 *        the identity of the face.\n
 *        \n
 *        Each detected object with type 'Face.Front' is compared with all the
 *        faces in the gallery. All the objects get at most five attributes and
 *        five ratings with keys 'Identity_0' to 'Identity_4 that contain the 
 *        identities of the best matching gallery faces and the corresponding
 *        match scores in descending order. If gallery is not 0 the model 
 *        FaceFrontIdent_36x45_2008_07_18_123318.cpp is required for face
 *        comparison and must be added to the project and registered.\n
 *        \n
 */
Shore::Engine* CreateFaceEngine( float timeBase,
                                 bool updateTimeBase       = true,
                                 unsigned long threadCount = 1UL,
                                 char const* modelType     = "Face.Front",
                                 float imageScale          = 1.0f,
                                 float minFaceSize         = 0.0f,
                                 long minFaceScore         = 9L,
                                 float idMemoryLength      = 0.0f,
                                 char const* idMemoryType  = "Spatial",
                                 bool trackFaces           = false,
                                 char const* phantomTrap   = "Off",
                                 bool searchEyes           = false,
                                 bool searchNose           = false,
                                 bool searchMouth          = false,
                                 bool analyzeEyes          = false,
                                 bool analyzeMouth         = false,
                                 bool analyzeGender        = false,
                                 bool analyzeAge           = false,
                                 bool analyzeHappy         = false,
                                 bool analyzeSad           = false,
                                 bool analyzeSurprised     = false,
                                 bool analyzeAngry         = false,
                                 Gallery* gallery          = 0 );


} // namespace Shore


#endif // CREATEFACEENGINE_H


