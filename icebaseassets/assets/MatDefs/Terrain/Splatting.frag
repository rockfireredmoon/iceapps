uniform sampler2D m_Alpha;
uniform sampler2D m_Tex1;

#ifdef TEX_2
uniform sampler2D m_Tex2;
#endif
#ifdef TEX_3
uniform sampler2D m_Tex3;
#endif
#ifdef TEX_4
uniform sampler2D m_Tex4;
#endif
uniform float m_AlphaScale;
uniform float m_Tex1Scale;
uniform float m_Tex2Scale;
uniform float m_Tex3Scale;
uniform float m_Tex4Scale;

varying vec2 texCoord;

#ifdef TRI_PLANAR_MAPPING
  varying vec4 vVertex;
  varying vec3 vNormal;
#endif

void main(void)
{

    // get the alpha value at this 2D texture coord
    vec4 alpha   = texture2D( m_Alpha, texCoord.xy * m_AlphaScale);
    vec4 outColor;

    // if there is more than one texture, use splatting
    #ifdef TRI_PLANAR_MAPPING
        // tri-planar texture bending factor for this fragment's normal
        vec3 blending = abs( vNormal );
        blending = (blending -0.2) * 0.7;
        blending = normalize(max(blending, 0.00001));      // Force weights to sum to 1.0 (very important!)
        float b = (blending.x + blending.y + blending.z);
        blending /= vec3(b, b, b);

        // texture coords
        vec4 coords = vVertex;

        vec4 col1 = texture2D( m_Tex1, coords.yz * m_Tex1Scale );
        vec4 col2 = texture2D( m_Tex1, coords.xz * m_Tex1Scale );
        vec4 col3 = texture2D( m_Tex1, coords.xy * m_Tex1Scale );
        // blend the results of the 3 planar projections.
        vec4 tex1 = col1 * blending.x + col2 * blending.y + col3 * blending.z;

        #ifdef TEX_2
            col1 = texture2D( m_Tex2, coords.yz * m_Tex2Scale );
            col2 = texture2D( m_Tex2, coords.xz * m_Tex2Scale );
            col3 = texture2D( m_Tex2, coords.xy * m_Tex2Scale );
            // blend the results of the 3 planar projections.
            vec4 tex2 = col1 * blending.x + col2 * blending.y + col3 * blending.z;
        #endif

        #ifdef TEX_3
            col1 = texture2D( m_Tex3, coords.yz * m_Tex3Scale );
            col2 = texture2D( m_Tex3, coords.xz * m_Tex3Scale );
            col3 = texture2D( m_Tex3, coords.xy * m_Tex3Scale );
            // blend the results of the 3 planar projections.
            vec4 tex3 = col1 * blending.x + col2 * blending.y + col3 * blending.z;
        #endif

        #ifdef TEX_4
            col1 = texture2D( m_Tex4, coords.yz * m_Tex4Scale );
            col2 = texture2D( m_Tex4, coords.xz * m_Tex4Scale );
            col3 = texture2D( m_Tex4, coords.xy * m_Tex4Scale );
            // blend the results of the 3 planar projections.
            vec4 tex4 = col1 * blending.x + col2 * blending.y + col3 * blending.z;
        #endif

    #else
            vec4 tex1    = texture2D( m_Tex1, texCoord.xy * m_Tex1Scale ); // Tile
            #ifdef TEX_2
            vec4 tex2    = texture2D( m_Tex2, texCoord.xy * m_Tex2Scale ); // Tile
            #endif
            #ifdef TEX_3
            vec4 tex3    = texture2D( m_Tex3, texCoord.xy * m_Tex3Scale ); // Tile
            #endif
            #ifdef TEX_4
            vec4 tex4    = texture2D( m_Tex4, texCoord.xy * m_Tex4Scale ); // Tile
            #endif
    #endif

    outColor = mix( outColor, tex1, 1.0 );
    #ifdef TEX_3
    outColor = mix( outColor, tex3, alpha.g );
    #endif
    #ifdef TEX_2
    outColor = mix( outColor, tex2, alpha.r );
    #endif
    #ifdef TEX_4
    outColor = mix( outColor, tex4, alpha.b );
    #endif

    gl_FragColor = outColor;
}

