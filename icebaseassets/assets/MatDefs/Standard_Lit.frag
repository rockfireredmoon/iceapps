#import "MatDefs/Lib/Common.glsllib"
#import "MatDefs/Lib/Lighting.glsllib"
#ifdef TINTMAP
#import "MatDefs/Lib/Palette.glsllib"
#endif

#import "MatDefs/Lib/Scrolling.glsllib"

varying float texCoordZ;

#ifdef FADE_ENABLED
uniform sampler2D m_AlphaNoiseMap;
#endif

void main(){

    vec2 newTexCoord;
    vec4 diffuseColor;
    
    calcNewTexCoord(newTexCoord);
    diffuseColor = mixAllDiffuse(newTexCoord);
    checkDiffuse(diffuseColor);

    #ifdef FADE_ENABLED
    if(texCoordZ < texture2D(m_AlphaNoiseMap, newTexCoord.xy).r){
        discard;
    }
    #endif

    #ifdef TINTMAP
    diffuseColor = adjustPalette(newTexCoord, diffuseColor);
    #endif

    // Process lighting
    gl_FragColor = lightPost(newTexCoord, diffuseColor);
}
