// (c) by Fraunhofer IIS, Department Electronic Imaging
//
// PROJECT     : Shore 
//
// AUTHOR      : Andreas Ernst
//
// DESCRIPTION : See header file.
//
// CHANGED BY  : $LastChangedBy: ruf $
//
// DATE        : $LastChangedDate: 2007-05-11 01:18:51 +0200 (Fri, 11 May 2007) $
//
// REVISION    : $LastChangedRevision: 10741 $
//
// start below with your implementation

#include "shore/ContentToText.h"

#include <sstream>
#include <iomanip>


using namespace Shore;


namespace
{

//==============================================================================
std::string ObjectToText( Object const* object, std::string const& indentation )
{
   std::stringstream text;
   text << std::fixed << std::setprecision(1);

   char const* type = object->GetType() ? object->GetType() : "";
   if ( *type )
   {
      text << indentation
           << "|---Type: \"" << type
           << "\"\n" ;
   }

   if ( object->GetRegion() )
   {
      text << indentation << "|\n" 
           << indentation << "|---Region" 
           << ": Left="   << object->GetRegion()->GetLeft()
           << ", Top="    << object->GetRegion()->GetTop()
           << ", Right="  << object->GetRegion()->GetRight()
           << ", Bottom=" << object->GetRegion()->GetBottom() << "\n";
   }

   if ( object->GetMarkerCount() )
   {
      text << indentation << "|\n" 
           << indentation << "|---Marker\n";
   }

   for( unsigned long m = 0; m < object->GetMarkerCount(); ++m )
   {
      if( !( object->GetAttributeCount() ||
             object->GetRatingCount()    ||
             object->GetPartCount() ) )
      {
         text << indentation << "    " << "|---"; 
      }
      else
      {
         text << indentation << "|   " << "|---"; 
      }

      text << object->GetMarkerKey(m) << ": "
           << "X="   << object->GetMarker(m)->GetX()
           << ", Y=" << object->GetMarker(m)->GetY() << "\n";
   }

   if ( object->GetAttributeCount() )
   {
      text << indentation << "|\n" 
           << indentation << "|---Attribute\n";
   }

   for( unsigned long a = 0; a < object->GetAttributeCount(); ++a )
   {
      if( !( object->GetRatingCount() || object->GetPartCount() ) )
      {
         text << indentation << "    " << "|---"; 
      }
      else
      {
         text << indentation << "|   " << "|---"; 
      }

      text << object->GetAttributeKey(a) << " = " 
           << object->GetAttribute(a) << "\n" ;
   }

   if ( object->GetRatingCount() )
   {
      text << indentation << "|\n" 
           << indentation << "|---Rating\n";
   }

   for( unsigned long r = 0; r < object->GetRatingCount(); ++r )
   {
      if( !( object->GetPartCount() ) )
      {
         text << indentation << "    " << "|---";
      }
      else
      {
         text << indentation << "|   " << "|---"; 
      }

      text << object->GetRatingKey(r) << " = "
           << *(object->GetRating(r)) << "\n";
   }

   if ( object->GetPartCount() )
   {
      text << indentation << "|\n" 
           << indentation << "|---Part\n";

      for( unsigned long p = 0; p < object->GetPartCount(); ++p )
      {
         text << indentation << "    |---" << object->GetPartKey(p) << "\n";

         if ( (p+1) == object->GetPartCount() )
         {
            text << ObjectToText(object->GetPart(p), indentation + "        ");
         }
         else
         {
            text << ObjectToText(object->GetPart(p), indentation + "    |   ");
            text << indentation << "    |" << "\n";
         }
      }
   }

   return text.str();
}

} //namespace


//==============================================================================
std::string ContentToText( Content const* content )
{
   char const* separator = "========================================"
                           "========================================\n";

   std::stringstream text;

   if ( content->GetObjectCount() > 0 )
   {
      for( unsigned long j = 0; j < content->GetObjectCount(); ++j )
      {
         text << separator
              << "=\n"
              << "= Object[" << j << "]\n"
              << "= |\n"
              << ObjectToText( content->GetObject(j), "= " )
              << "=\n";
      }
   }
   else
   {
      text << separator
           << "=\n"
           << "= No objects\n"
           << "=\n";
   }

   text << separator
        << "=\n";

   if ( content->GetInfoCount() > 0 )
   {
      for ( unsigned long i = 0; i < content->GetInfoCount(); ++i )
      {
         text << "= " << content->GetInfoKey(i) << ": "
                      << content->GetInfo(i) << "\n";
      }
   }
   else
   {
      text << "= No info\n";
   }

   text << "=\n" 
        << separator;

   return text.str();
}


