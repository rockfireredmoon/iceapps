uniform sampler2D m_Mask; 
uniform sampler2D m_Texture;
varying vec2 texCoord;

void main(){
    float m = texture2D( m_Mask, texCoord).r;
    vec4 tex = texture2D( m_Texture, texCoord);
    gl_FragColor.rgb = tex.xyz;
    if(m < 0.5) {
        discard;
    }
}
