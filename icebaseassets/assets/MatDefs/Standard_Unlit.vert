#import "Common/ShaderLib/Skinning.glsllib"

uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldMatrix;
uniform vec3 g_CameraPosition;
uniform float g_Time;

attribute vec3 inPosition;

#if defined(HAS_COLORMAP) || (defined(HAS_LIGHTMAP) && !defined(SEPARATE_TEXCOORD))
    #define NEED_TEXCOORD1
#endif

attribute vec2 inTexCoord;
attribute vec2 inTexCoord2;
attribute vec4 inColor;

varying vec2 texCoord1;

#ifdef SEPARATE_TEXCOORD
varying vec2 texCoord2;
#endif

#if defined(FADE_ENABLED) || defined(SWAYING) 
varying float texCoordZ;
#endif

varying vec4 vertColor;

#import "MatDefs/Lib/Scrolling.vert.glsllib"
#import "MatDefs/Lib/FadeAndSway.vert.glsllib"

void main(){
    
    #ifdef NEED_TEXCOORD1
        texCoord1 = inTexCoord;
    	#ifndef SEPARATE_TEXCOORD 
    		scroll(texCoord1);
    	#endif
    #endif

    #ifdef SEPARATE_TEXCOORD
        texCoord2 = inTexCoord2;
    	scroll(texCoord2);
    #endif   

    #ifdef HAS_VERTEXCOLOR
        vertColor = inColor;
    #endif

    vec4 modelSpacePos = vec4(inPosition, 1.0);
    #ifdef NUM_BONES
        Skinning_Compute(modelSpacePos);
    #endif
    
    #if defined(FADE_ENABLED) || defined(SWAYING) 
    	modelSpacePos = fadeAndSway(g_WorldMatrix*modelSpacePos, modelSpacePos, texCoordZ, texCoord1);
    #endif
    
    gl_Position = g_WorldViewProjectionMatrix * modelSpacePos;
}