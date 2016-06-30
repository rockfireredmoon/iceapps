uniform sampler2D m_AlphaMap;
uniform sampler2D m_ColorMap;

varying vec2 texCoord;

uniform float m_Scale;
uniform float m_Alpha;
uniform float m_AlphaDiscardThreshold;

void main(){
    vec4 tex    = texture2D( m_ColorMap, texCoord.xy * m_Scale );

    float alpha = tex.a;
    
    #ifdef ALPHAMAP
	    alpha = alpha * texture2D(m_AlphaMap, texCoord.xy).a;
	#endif
	
	alpha = alpha * m_Alpha; 
    
    if(alpha < m_AlphaDiscardThreshold){
        discard;
    }
    tex.a = alpha;
    gl_FragColor = tex;
}