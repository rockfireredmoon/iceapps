uniform mat4 g_WorldViewProjectionMatrix;

uniform float g_Time;
uniform float m_GlobalAnimSpeed;
uniform float m_ScrollAnim1X;
uniform float m_ScrollAnim1Y;
uniform float m_Scroll1X;
uniform float m_Scroll1Y;
uniform float m_ScrollAnim2X;
uniform float m_ScrollAnim2Y;
uniform float m_Scroll2X;
uniform float m_Scroll2Y;
uniform float m_ScrollAnim3X;
uniform float m_ScrollAnim3Y;
uniform float m_Scroll3X;
uniform float m_Scroll3Y;
uniform float m_ScrollAnim4X;
uniform float m_ScrollAnim4Y;
uniform float m_Scroll4X;
uniform float m_Scroll4Y;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
 
varying vec2 texCoord1;
varying vec2 texCoord2;
varying vec2 texCoord3;
varying vec2 texCoord4;
 
void main(){
    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
    texCoord1 = vec2(inTexCoord[0] + mod((g_Time * m_ScrollAnim1X * m_GlobalAnimSpeed) + m_Scroll1X , 1),
            inTexCoord[1] + mod((g_Time * m_ScrollAnim1Y * m_GlobalAnimSpeed ) + m_Scroll1Y , 1));
    texCoord2 = vec2(inTexCoord[0] + mod((g_Time * m_ScrollAnim2X * m_GlobalAnimSpeed ) + m_Scroll2X , 1),
            inTexCoord[1] + mod((g_Time * m_ScrollAnim2Y * m_GlobalAnimSpeed ) + m_Scroll2Y , 1));
    texCoord3 = vec2(inTexCoord[0] + mod((g_Time * m_ScrollAnim3X * m_GlobalAnimSpeed ) + m_Scroll3X , 1),
            inTexCoord[1] + mod((g_Time * m_ScrollAnim3Y * m_GlobalAnimSpeed ) + m_Scroll3Y , 1));
    texCoord4 = vec2(inTexCoord[0] + mod((g_Time * m_ScrollAnim4X * m_GlobalAnimSpeed ) + m_Scroll4X , 1),
            inTexCoord[1] + mod((g_Time * m_ScrollAnim4Y * m_GlobalAnimSpeed ) + m_Scroll4Y , 1));
}