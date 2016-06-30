uniform mat4 g_WorldViewProjectionMatrix;

uniform float g_Time;
uniform float m_GlobalAnimSpeed;
uniform float m_ScrollAnimX;
uniform float m_ScrollAnimY;
uniform float m_ScrollX;
uniform float m_ScrollY;

attribute vec3 inPosition;
attribute vec2 inTexCoord;
 
varying vec2 texCoord;
const float pi = 3.14159;
 
void main(){

   //     texCoord = inTexCoord;
     //   float alphaTime = (g_Time*m_GlobalAnimSpeed);
	   // texCoord.x += (alphaTime*sin((1*(pi/180.0))));
	    //texCoord.y += (alphaTime*cos((1*(pi/180.0))));


    gl_Position = g_WorldViewProjectionMatrix * vec4(inPosition, 1.0);
    texCoord = vec2(inTexCoord[0] + mod((g_Time * m_ScrollAnimX * m_GlobalAnimSpeed ) + m_ScrollX , 1),
            inTexCoord[1] + mod((g_Time * m_ScrollAnimY * m_GlobalAnimSpeed ) + m_ScrollY , 1));
}