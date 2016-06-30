uniform sampler2D m_AlphaMap;
uniform sampler2D m_ColorMap;

varying vec2 texCoord;

uniform float m_Scale;
uniform float m_GlobalAlpha;
uniform float m_AlphaDiscardThreshold;

void main(){
	vec2 texc =  vec2(texCoord.x, texCoord.y) ;
    vec4 tex    = texture2D( m_ColorMap, texc * m_Scale );

    float alpha = tex.a;
    
    #ifdef ALPHAMAP
	    alpha = alpha * texture2D(m_AlphaMap, texc).a;
	#endif
	
	alpha = alpha * m_GlobalAlpha; 
    
    if(alpha < m_AlphaDiscardThreshold){
        discard;
    }
    tex.a = alpha;
    gl_FragColor = tex;
}