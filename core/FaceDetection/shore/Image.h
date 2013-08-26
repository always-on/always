// (c) by Fraunhofer IIS, Department Electronic Imaging
//
// PROJECT     : Image helper class for the command line tool 
//
// AUTHOR      : Andreas Ernst
//
// DESCRIPTION : See below.
//
// CHANGED BY  : $LastChangedBy: ruf $
//
// DATE        : $LastChangedDate: 2009-07-15 13:12:35 +0200 (Wed, 15 Jul 2009) $
//
// REVISION    : $LastChangedRevision: 10741 $
//
// start below with your implementation

#ifndef IMAGE_H
#define IMAGE_H


#include <iostream>
#include <fstream>
#include <vector>

#include "opencv2/cv.h"


// The "Shore.h" include is out of place in an image class. But this image class
// is only a small helper class that wraps all the image facilities needed to 
// run the demo program. And this include is needed to draw the regions and
// markers of the result of Shore::Engine::Process().
#include "shore/Shore.h"


//==============================================================================
//  Although some of the methods don't fit in this class in good object 
//  oriented style, we put them in for convenience.
class Image
{
private:
   //===========================================================================
   unsigned long m_width;
   
   //===========================================================================
   unsigned long m_height;

   //===========================================================================
   std::vector< unsigned char > m_data;

   //===========================================================================
   unsigned long ReadNumber(unsigned char const*&buffer)
   {
     do 
     {
         if ( *(buffer++) == '#' )
         {
            while ( *(buffer++) != 10 );
         }
         
         switch (*buffer)
         {
            case   9 : 
            case  10 : 
            case  13 : 
            case ' ' : 
            case '#' :
            case '0' : 
            case '1' : 
            case '2' : 
            case '3' : 
            case '4' : 
            case '5' : 
            case '6' : 
            case '7' : 
            case '8' : 
            case '9' : break;
            default :  
            {
               std::cerr << "Error in Image::LoadPgm: invalid data" << std::endl;
               exit( 1 );
            }
         }
     } 
     while ( (*buffer < '0') || (*buffer > '9') );

     unsigned long value = 0;
     
     do
     {
         value = 10 * value;
         value += *(buffer++) - '0';
     } 
     while ( (*buffer>='0') && (*buffer<='9') );
     
     return value;
   }

   //===========================================================================
   void DrawPoint( float fx, float fy )
   {
      if ( fx >= 0.0f && fy >= 0.0f )
      {
         unsigned long x = static_cast< unsigned long >( fx );
         unsigned long y = static_cast< unsigned long >( fy );
      
         if ( x < m_width && y < m_height )
         {
            m_data[x + y * m_width] = 255;
         }
      }
   }

   //===========================================================================
   void DrawCross( float fx, float fy )
   {
      DrawPoint( fx - 2, fy     );
      DrawPoint( fx - 1, fy     );
      DrawPoint( fx + 1, fy     );
      DrawPoint( fx + 2, fy     );
      DrawPoint( fx    , fy     );
      DrawPoint( fx    , fy - 2 );
      DrawPoint( fx    , fy - 1 );
      DrawPoint( fx    , fy + 1 );
      DrawPoint( fx    , fy + 2 );
   }
   
   //===========================================================================
   void DrawRegion( float left,
                    float top,
                    float right,
                    float bottom )
   {
      for ( float x = left; x <= right; x += 1.0f )
      {
         DrawPoint( x, top    );
         DrawPoint( x, bottom );
      }

      for ( float y = top; y <= bottom; y += 1.0f )
      {
         DrawPoint( left,  y );
         DrawPoint( right, y );
      }
   }

   //===========================================================================
   void DrawObject( Shore::Object const* object )
   {
      Shore::Region const* region = object->GetRegion();

      if ( region )
      {
         DrawRegion( region->GetLeft(),
                     region->GetTop(),
                     region->GetRight(),
                     region->GetBottom() );
      }
      
      for ( unsigned long m = 0; m < object->GetMarkerCount(); ++m )
      {
         Shore::Marker const* marker = object->GetMarker( m );
         
         DrawCross( marker->GetX(), marker->GetY() );
      }

      for ( unsigned long p = 0; p < object->GetPartCount(); ++p )
      {
         Shore::Object const* part = object->GetPart( p );
         
         DrawObject( part );
      }
   }

public:
   //===========================================================================
   Image( std::string const& filename )
    : m_width( 0 ),
      m_height( 0 )
   {
      LoadPgm( filename );
   }

   Image(int width, int height, IplImage *image): m_width (0), m_height(0) {
	   loadIplImage(width,height,image);
   }

   //===========================================================================
   ~Image()
   {
   }

   //===========================================================================
   void LoadPgm( std::string const& filename )
   {
      std::ifstream in( filename.c_str(), std::ios::binary );

      if ( !in )
      {
         std::cerr << "Error in Image::LoadPgm: Could not open file " 
                   << filename << std::endl;
         std::exit(1);
      }
      
      std::string data = std::string( std::istreambuf_iterator< char >( in ),
                                      std::istreambuf_iterator< char >() );

      if ( ( data[0] != 'p' && data[0] != 'P' ) || data[1] != '5' )
      {
         std::cerr << "Error in Image::LoadPgm: Unsupported image format " 
                   << filename << std::endl;
         std::exit(1);
      }
     
      unsigned char const* p = reinterpret_cast< unsigned char* >(&data[2]);

      m_width  = ReadNumber( p );
      m_height = ReadNumber( p );

      if ( ReadNumber( p ) > 255 )
      {
         std::cerr << "Error in Image::LoadPgm: Unsupported image deep " 
                   << filename << std::endl;
         std::exit(1);
      }
      
      p++;

      m_data.assign( p, p + m_width * m_height );
   }

   void loadIplImage(int width, int height, IplImage *img){
	   m_width = width;
	   m_height = height;
	   m_data.assign(img->imageData,img->imageData + m_width * m_height);
   }
   
   //===========================================================================
   void SavePgm( std::string const& filename )
   {
      std::ofstream out( filename.c_str(), std::ios::binary );
      
      if ( out.bad() )
      {
         std::cerr << "Error in Image::SavePgm: Could not open file" 
                   << filename << std::endl;
         std::exit(1);
      }

      out << "P5"     << std::endl;
      out << m_width  << std::endl;
      out << m_height << std::endl;
      out << "255"    << std::endl;
      out.write( reinterpret_cast<char*>( &m_data[0] ), m_width * m_height );
   }
   
   //===========================================================================
   void DrawContent( Shore::Content const* content )
   {
      for ( unsigned long o = 0; o < content->GetObjectCount(); ++o )
      {
         Shore::Object const* object = content->GetObject( o );
         
         DrawObject( object );
      }
   }

   //===========================================================================
   unsigned long Width() const
   {
      return m_width;
   }
   
   //===========================================================================
   unsigned long Height() const
   {
      return m_height;
   }
   
   //===========================================================================
   unsigned char const* LeftTop() const
   {
      return &m_data[0];
   }
};


#endif // IMAGE_H


