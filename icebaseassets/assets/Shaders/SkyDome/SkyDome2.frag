uniform vec4 m_Color;

uniform sampler2D m_SkyMap;
uniform float m_SkyMapScale;

uniform sampler2D m_FogAlphaMap;
uniform float m_GlobalAlpha;
uniform float m_AlphaDiscardThreshold;
uniform vec4 m_FogColor;

varying float cycleTime;

varying vec2 texCoord1;
uniform float m_CelestialBodyAlpha;
uniform float m_CelestialBodyScale;
uniform float m_SkyMapAlpha;

uniform sampler2D m_CloudMap1;
uniform float m_CloudMap1Scale;
uniform float m_CloudsAlpha;

#ifdef HAS_CLOUDS1
varying vec2 cloudCoord1;
#endif

#ifdef HAS_ALPHAMAP
uniform sampler2D m_AlphaMap;
uniform float m_AlphaMapScale;
#endif

#ifdef HAS_CLOUDS2
varying vec2 cloudCoord2;
#endif

#ifdef HAS_CELESTIAL_BODY
uniform sampler2D m_CelestialBodyMap;
varying vec2 celestialBodyCoord;
#endif

#ifdef HAS_COLORRAMP1
uniform vec4 m_CloudColorRamp1;
#endif

#ifdef HAS_COLORRAMP2
uniform vec4 m_CloudColorRamp2;
#endif

#ifdef HAS_LIGHTMAP
uniform sampler2D m_LightMap;
#ifdef SEPERATE_TEXCOORD
varying vec2 texCoord2;
#endif
#endif

#ifdef HAS_VERTEXCOLOR
varying vec4 vertColor;
#endif

void main(){
    vec4 color = m_Color;
    
    #ifdef HAS_SKYMAP
	    #if HAS_SKYMAPUSEASMAP
		    vec4 sky = texture2D(m_SkyMap, texCoord1 * m_SkyMapScale);
		    color *= mix(color, sky, m_SkyMapAlpha);
		#else		
		    color = texture2D(m_SkyMap, texCoord1 * m_SkyMapScale * m_SkyMapAlpha);
	    #endif
    #endif

    #ifdef HAS_VERTEXCOLOR
    color *= vertColor;
    #endif

    #ifdef HAS_CELESTIAL_BODY
    vec4 celestialBody = texture2D(m_CelestialBodyMap, celestialBodyCoord * m_CelestialBodyScale);
    color = mix(color, celestialBody, celestialBody.a * m_CelestialBodyAlpha);
    #endif

    vec4 fogColor = m_FogColor;

    vec4 c_Color;
    #if defined(HAS_COLORRAMP1) || defined(HAS_COLORRAMP2)
        vec4 n_Color;
    #endif
    #if defined(HAS_CLOUDS1) || defined(HAS_CLOUDS2)
        vec4 cloud;
    #endif

    #ifdef HAS_CLOUDS1
    	
	    cloud = texture2D(m_CloudMap1, cloudCoord1 * m_CloudMap1Scale);
    	#if HAS_CLOUDMAP1USEASMAP
	        c_Color = vec4(1.0,1.0,1.0,cloud.r*m_CloudsAlpha);
	        #ifdef HAS_COLORRAMP1
	            n_Color = vec4(vec3(m_CloudColorRamp1.rgb*cloud.r),cloud.r);
	            c_Color = mix(c_Color, n_Color, 0.25f);
	        #endif
	        color = mix(color,c_Color,cloud.r*m_CloudsAlpha);
	    #else
    		color = mix(color, cloud, cloud.a * m_CloudsAlpha);
	    #endif
        
    #endif
    #ifdef HAS_CLOUDS2
        cloud = texture2D(m_CloudMap1, cloudCoord2);
        c_Color = vec4(1.0,1.0,1.0,cloud.r*m_CloudsAlpha);
        #ifdef HAS_COLORRAMP2
            n_Color = vec4(vec3(m_CloudColorRamp2.rgb*cloud.r),cloud.r);
            c_Color = mix(c_Color, n_Color, 0.25f);
        #endif
        color = mix(color,c_Color,cloud.r*m_CloudsAlpha);
    #endif

    #ifdef HAS_FOGALPHAMAP
    float fogAlpha = 1.0-texture2D(m_FogAlphaMap, texCoord1).r;
    color = mix(color,fogColor,fogAlpha);
    #endif
    
	#ifdef HAS_ALPHAMAP
    //color.a = color.a * texture2D(m_AlphaMap, texCoord1 * m_AlphaMapScale).a;
	#endif

	color.a = color.a * m_GlobalAlpha; 
    if(color.a < m_AlphaDiscardThreshold){
        discard;
    }

    gl_FragColor = color;
}