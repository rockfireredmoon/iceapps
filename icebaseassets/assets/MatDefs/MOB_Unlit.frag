#import "MatDefs/Lib/Common.glsllib"

#if defined(HAS_GLOWMAP) || defined(HAS_COLORMAP) || (defined(HAS_LIGHTMAP) && !defined(SEPARATE_TEXCOORD))
    #define NEED_TEXCOORD1
#endif

#ifdef ILLUMMAP
uniform sampler2D m_IllumMap;
#endif

uniform vec4 m_Color;
uniform sampler2D m_ColorMap;
uniform sampler2D m_LightMap;

varying vec2 texCoord1;
varying vec2 texCoord2;

varying vec4 vertColor;

#ifdef TINTMAP
#import "MatDefs/Lib/Palette.glsllib"
#endif
#import "MatDefs/Lib/Clothing.glsllib"

void main(){
    vec4 color = vec4(1.0);

    #ifdef HAS_COLORMAP
        color *= texture2D(m_ColorMap, texCoord1);     
    #endif

	checkDiffuse(color);

    #ifdef HAS_VERTEXCOLOR
        color *= vertColor;
    #endif

    #ifdef HAS_COLOR
        color *= m_Color;
    #endif
    
    #ifdef ILLUMMAP
        #ifdef SEPARATE_TEXCOORD
            color.rgb *= texture2D(m_IllumMap, texCoord2).rgb;
        #else
            color.rgb *= texture2D(m_IllumMap, texCoord1).rgb;
        #endif
    #endif

    #ifdef HAS_LIGHTMAP
        #ifdef SEPARATE_TEXCOORD
            color.rgb *= texture2D(m_LightMap, texCoord2).rgb;
        #else
            color.rgb *= texture2D(m_LightMap, texCoord1).rgb;
        #endif
    #endif
    
    // Color the skin
    #ifdef TINTMAP
        #ifdef SEPARATE_TEXCOORD
    		color = adjustPalette(texCoord2, color);
    	#else
    		color = adjustPalette(texCoord1, color);
    	#endif
    #endif

    // Add clothing
    #ifdef SEPARATE_TEXCOORD
    	color = textureClothing(texCoord2, color);
	#else
    	color = textureClothing(texCoord1, color);
	#endif
   	
    gl_FragColor = color;
}