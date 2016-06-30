#import "MatDefs/Lib/Common.glsllib"
#import "MatDefs/Lib/Lighting.glsllib"
#ifdef TINTMAP
#import "MatDefs/Lib/Palette.glsllib"
#endif
#import "MatDefs/Lib/Clothing.glsllib"

void main(){
    vec2 newTexCoord;
    vec4 diffuseColor;
    lightPre(newTexCoord, diffuseColor);

    // Color the skin
    #ifdef TINTMAP
    	diffuseColor = adjustPalette(newTexCoord, diffuseColor);
    #endif

    // Add clothing
    #ifdef HAS_CLOTHING_0
    	diffuseColor = textureClothing(newTexCoord, diffuseColor);
    #endif

    // Process lighting
    gl_FragColor = lightPost(newTexCoord, diffuseColor);
}
