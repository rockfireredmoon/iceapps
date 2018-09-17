varying vec2 texCoord;

#ifdef HAS_GLOWMAP
  uniform sampler2D m_GlowMap;
  uniform float m_GlowIntensity;
#endif

#ifdef HAS_GLOWCOLOR
  uniform vec4 m_GlowColor;
#endif


void main(){
   
    #if defined HAS_GLOWMAP

vec4 GlowTex = texture2D(m_GlowMap, texCoord);
     GlowTex = GlowTex * m_GlowIntensity;
           gl_FragColor = GlowTex;
    #else
        #ifdef HAS_GLOWCOLOR
            gl_FragColor =  m_GlowColor;
        #else
            gl_FragColor = vec4(0.0);
        #endif
    #endif
}