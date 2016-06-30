uniform sampler2D m_ColorMap1;
uniform sampler2D m_ColorMap2;
uniform sampler2D m_ColorMap3;
uniform sampler2D m_ColorMap4;

varying vec2 texCoord1;
varying vec2 texCoord2;
varying vec2 texCoord3;
varying vec2 texCoord4;

uniform float m_Scale1;
uniform float m_Scale2;
uniform float m_Scale3;
uniform float m_Scale4;

uniform float m_Alpha1;
uniform float m_Alpha2;
uniform float m_Alpha3;
uniform float m_Alpha4;

void main(){

    vec4 tex1    = texture2D( m_ColorMap1, texCoord1.xy * m_Scale1 );
    vec4 tex2    = texture2D( m_ColorMap2, texCoord2.xy * m_Scale2 );
    vec4 tex3    = texture2D( m_ColorMap3, texCoord3.xy * m_Scale3 );
    vec4 tex4    = texture2D( m_ColorMap4, texCoord4.xy * m_Scale4 );

    //gl_FragColor = texture2D(m_ColorMap1, texCoord1);

    vec4 outColor;
    
    outColor = mix( outColor, tex1, m_Alpha1 );
    outColor = mix( outColor, tex2, m_Alpha2 );
    outColor = mix( outColor, tex3, m_Alpha3 );
    outColor = mix( outColor, tex4, m_Alpha4 );

    gl_FragColor = outColor;
}