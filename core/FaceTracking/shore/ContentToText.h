// (c) by Fraunhofer IIS, Department Electronic Imaging
//
// PROJECT     : Shore
//
// AUTHOR      : Andreas Ernst
//
// DESCRIPTION : See below. 
//
// CHANGED BY  : $LastChangedBy: ruf $
//
// DATE        : $LastChangedDate: 2007-05-10 23:32:18 +0200 (Thu, 10 May 2007) $
//
// REVISION    : $LastChangedRevision: 10741 $
//
// start below with your implementation

#ifndef CONTENTTOTEXT_H
#define CONTENTTOTEXT_H


#include "Shore.h"

#include <string>


//==============================================================================
/**
 * A simple function that converts the provided content into a human readable
 * text string which can be printed on the console or written into a log file.
 * As the conversion is not optimized for speed, this function should only be
 * used for testing and debugging purposes.
 */
std::string ContentToText( Shore::Content const* content );


#endif // CONTENTTOTEXT_H



