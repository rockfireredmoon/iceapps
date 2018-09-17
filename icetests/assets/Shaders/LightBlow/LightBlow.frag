#import "Common/ShaderLib/Optics.glsllib"
#define ATTENUATION
//#define HQ_ATTENUATION

varying vec2 texCoord;

varying vec3 AmbientSum;
varying vec4 DiffuseSum;
varying vec3 SpecularSum;


#ifdef HAS_LIGHTMAP
    uniform float m_LightMapIntensity;
    uniform sampler2D m_LightMap;
    #ifdef SEPERATE_TEXCOORD
        varying vec2 texCoord2;
    #endif
#endif


#ifndef VERTEX_LIGHTING
  varying vec3 vPosition;
  varying vec3 vViewDir;
  varying vec4 vLightDir;
  varying vec3 mat;
#endif

#ifdef DIFFUSEMAP
  uniform sampler2D m_DiffuseMap;
#endif

#ifdef SPECULARMAP
  uniform sampler2D m_SpecularMap;
#endif

#ifdef PARALLAXMAP
  uniform sampler2D m_ParallaxMap;
#endif
  
#ifdef NORMALMAP
  uniform sampler2D m_NormalMap;
#else
  varying vec3 vNormal;
#endif

#ifdef ALPHAMAP
  uniform sampler2D m_AlphaMap;
#endif

#ifdef COLORRAMP
  uniform sampler2D m_ColorRamp;
#endif

uniform float m_AlphaDiscardThreshold;


#ifndef VERTEX_LIGHTING

#ifdef SPECULAR_LIGHTING
uniform float m_Shininess;
uniform float m_SpecIntensity;
#endif

#ifdef HQ_ATTENUATION
uniform vec4 g_LightPosition;
varying vec3 lightVec;
#endif

#if defined RIM_LIGHTING || RIM_LIGHTING_2
uniform vec4 m_RimLighting;
uniform vec4 m_RimLighting2;
// uniform vec4 g_AmbientLightColor;
#endif

#ifdef IBL 
varying vec4 iblVec;
uniform ENVMAP m_IblMap;
uniform float m_iblIntensity;
#endif



#ifdef REFLECTION 

    uniform float m_RefPower;
    uniform float m_RefIntensity;
    varying vec4 refVec;
    uniform ENVMAP m_RefMap;
#endif

#ifdef MINNAERT
uniform vec4 m_Minnaert;
#endif


float tangDot(in vec3 v1, in vec3 v2){
    float d = dot(v1,v2);
    #ifdef V_TANGENT
        d = 1.0 - d*d;
        return step(0.0, d) * sqrt(d);
    #else
        return d;
    #endif
}


float lightComputeDiffuse(in vec3 norm, in vec3 lightdir, in vec3 viewdir){
 
     #if defined(HEMI_LIGHTING_1)
     return (0.5 + 0.5 * dot(norm, lightdir)) * (0.5 + 0.5 * dot(norm, lightdir));
    #elif !defined(HEMI_LIGHTING_1) && defined(HEMI_LIGHTING_2)
    return 0.4 + 0.5 * dot(norm, lightdir);
    #else
        return max(0.0, dot(norm, lightdir));

    #endif
}


#if defined(SPECULAR_LIGHTING) && !defined(VERTEX_LIGHTING)
float lightComputeSpecular(in vec3 norm, in vec3 viewdir, in vec3 lightdir, in float shiny){
    #ifdef LOW_QUALITY
       // Blinn-Phong
       // Note: preferably, H should be computed in the vertex shader
       
       vec3 H = (viewdir + lightdir) * vec3(0.5);
       return pow(max(tangDot(H, norm), 0.0), shiny);

    #elif defined(WARDISO)
        // Isotropic Ward
        vec3 halfVec = normalize(viewdir + lightdir);
        float NdotH  = max(0.001, tangDot(norm, halfVec));
        float NdotV  = max(0.001, tangDot(norm, viewdir));
        float NdotL  = max(0.001, tangDot(norm, lightdir));
        float a      = tan(acos(NdotH));
        float p      = max(shiny/128.0, 0.001);
        return NdotL * (1.0 / (4.0*3.14159265*p*p)) * (exp(-(a*a)/(p*p)) / (sqrt(NdotV * NdotL)));
    #else
       // Standard Phong
       vec3 R = reflect(-lightdir, norm);
       return pow(max(tangDot(R, viewdir), 0.0), shiny);
    #endif
}
#endif


vec2 computeLighting(in vec3 wvPos, in vec3 wvNorm, in vec3 wvViewDir, in vec3 wvLightDir){
   
float diffuseFactor = lightComputeDiffuse(wvNorm, wvLightDir, wvViewDir);

   #ifdef HQ_ATTENUATION
    float att = clamp(1.0 - g_LightPosition.w * length(lightVec), 0.0, 1.0);
    #elif NO_ATTENUATION
    float att = 1.0;
   #else
    float att = vLightDir.w;
   #endif
    
    #if defined(SPECULAR_LIGHTING)
    float specularFactor = lightComputeSpecular(wvNorm, wvViewDir, wvLightDir, m_Shininess);
    specularFactor =  (specularFactor * step(1.0, m_Shininess)) * m_SpecIntensity;
 
    return vec2(diffuseFactor, specularFactor) * vec2(att);
    #endif

     #if !defined(SPECULAR_LIGHTING)
   return vec2(diffuseFactor, 0.0) * vec2(att);
    #endif

}
#endif




void main(){
    
  vec2 newTexCoord;

 
    #if defined(PARALLAXMAP) || defined(PARALLAX_A_NOR) && !defined(VERTEX_LIGHTING)
       float h;
       #if defined (PARALLAXMAP)
          h = texture2D(m_ParallaxMap, texCoord).r;
       #elif defined (PARALLAX_A_NOR) && defined (NORMALMAP)
          h = texture2D(m_NormalMap, texCoord).a;
       #endif
       float heightScale = 0.05;
       float heightBias = heightScale * -0.5;
       vec3 normView = normalize(vViewDir);
       h = (h * heightScale + heightBias) * normView.z;
       newTexCoord = texCoord + (h * normView.xy);
    #else
       newTexCoord = texCoord;
    #endif


    
   #ifdef DIFFUSEMAP
      vec4 diffuseColor = texture2D(m_DiffuseMap, newTexCoord);
    #else
      vec4 diffuseColor = vec4(0.6, 0.6, 0.6, 1.0);
    #endif






    #if defined(NORMALMAP) && !defined(VERTEX_LIGHTING)
      vec4 normalHeight = texture2D(m_NormalMap, newTexCoord);
      vec3 normal = (normalHeight.xyz * vec3(2.0) - vec3(1.0));
      normal = normalize(normal);
     #ifdef LATC
        normal.z = sqrt(1.0 - (normal.x * normal.x) - (normal.y * normal.y));
      #endif
    #if defined (NOR_INV_X) && (NORMALMAP) 
    normal.x = -normal.x;
    #endif

    #if defined (NOR_INV_Y) && (NORMALMAP)
    normal.y = -normal.y;
    #endif

    #if defined (NOR_INV_Z) && (NORMALMAP)
    normal.z = -normal.z;
    #endif
 
          #elif !defined(VERTEX_LIGHTING)
      vec3 normal = vNormal;
    #endif



    #ifdef SPECULARMAP 
      vec4 specularColor = texture2D(m_SpecularMap, newTexCoord);
    #else
      vec4 specularColor = vec4(1.0);
    #endif

    #if defined(SPECULAR_LIGHTING) && defined(SPEC_A_NOR) && defined(NORMALMAP) && !defined(SPECULARMAP)
    float specA = texture2D(m_NormalMap, newTexCoord).a;
    specularColor =  vec4(specA);
    #elif defined(SPECULAR_LIGHTING) && defined(SPEC_A_DIF) && !defined(SPEC_A_NOR) && defined(DIFFUSEMAP) && !defined(SPECULARMAP)
    float specA = texture2D(m_DiffuseMap, newTexCoord).a;
    specularColor =  vec4(specA);    
    #endif


     float alpha = DiffuseSum.a;

    #if defined (ALPHA_A_DIF) && defined (DIFFUSEMAP)
       alpha = DiffuseSum.a * diffuseColor.a;
    #elif defined (ALPHA_A_NOR) && defined (NORMALMAP)
       alpha = DiffuseSum.a * normalHeight.a;   
    #endif
    if(alpha < m_AlphaDiscardThreshold){
        discard;
    }



    #ifdef VERTEX_LIGHTING
      // vec2 light = vec2(AmbientSum.a, SpecularSum.a);
       #ifdef COLORRAMP
           light.x = texture2D(m_ColorRamp, vec2(light.x, 0.0)).r;
           light.y = texture2D(m_ColorRamp, vec2(light.y, 0.0)).r;
       #endif

     #if defined(SPECULAR_LIGHTING) && defined(VERTEX_LIGHTING)
       gl_FragColor =  AmbientSum * diffuseColor + 
                       DiffuseSum * diffuseColor  * light.x +
                       SpecularSum * specularColor * light.y;       
        #endif


    #if !defined(SPECULAR_LIGHTING) && defined(VERTEX_LIGHTING)
                        gl_FragColor = AmbientSum * diffuseColor + 
                                       DiffuseSum * diffuseColor  * light.x;
        #endif
    #else
       vec4 lightDir = vLightDir;
       lightDir.xyz = normalize(lightDir.xyz);

       vec2 light = computeLighting(vPosition, normal, vViewDir.xyz, lightDir.xyz);

       #ifdef COLORRAMP
           diffuseColor.rgb  *= texture2D(m_ColorRamp, vec2(light.x, 0.0)).rgb;
           specularColor.rgb *= texture2D(m_ColorRamp, vec2(light.y, 0.0)).rgb;
       #endif



       // Workaround, since it is not possible to modify varying variables
       vec4 SpecularSum2 = vec4(SpecularSum, 1.0);





        #ifdef IBL
       // IBL - Image Based Lighting. The lighting based on either cube map or sphere map.
           #if  defined (IBL) && defined (NORMALMAP)
            vec4 iblLight = Optics_GetEnvColor(m_IblMap, (iblVec.xyz - mat.xyz * normal.xyz)*-2.0);
           #elif  defined (IBL) && !defined (NORMALMAP)
            vec4 iblLight = Optics_GetEnvColor(m_IblMap,  iblVec.xyz);
           #endif
        
        //Albedo 
        AmbientSum.rgb +=  iblLight.rgb * m_iblIntensity;
        

        #endif


#if defined(EMISSIVEMAP) && defined(DIFFUSEMAP)
        //Illumination based on diffuse map alpha chanel.
	float emissiveTex = texture2D(m_DiffuseMap, texCoord).a;
	
	//diffuseColor = max(diffuseColor, emissiveSum); 
	
light.x = light.x + 1.1 * emissiveTex;
//light.x = max(light.x,  emissiveTex);

        #endif


#if defined (REFLECTION) 
    // Reflection based on either cube map or sphere map.

    #if  defined (REFLECTION) && defined (NORMALMAP)
    vec4 refGet = Optics_GetEnvColor(m_RefMap, (refVec.xyz - mat.xyz * normal.xyz)*-2.0);
  #elif defined (REFLECTION) && !defined (NORMALMAP)
    vec4 refGet = Optics_GetEnvColor(m_RefMap, refVec.xyz);
    #endif

    vec4 refColor = refGet * m_RefPower;
    float refTex = 1.0;

    #if defined(REF_A_NOR) && defined(NORMALMAP)
    refTex = texture2D(m_NormalMap, texCoord).a * m_RefIntensity;
    diffuseColor += refColor * refTex;
    #elif defined(REF_A_DIF) && !defined(REF_A_NOR) && defined(DIFFUSEMAP)
    refTex = texture2D(m_DiffuseMap, texCoord).a * m_RefIntensity;
    diffuseColor.rgb += refColor.rgb * refTex;
    #else
    diffuseColor.rgb += refColor.rgb;
    #endif

light.x = max(light.x, refGet* refTex * 0.5);
 #endif

#ifdef MINNAERT
// if (length(g_AmbientLightColor.xyz) != 0.0) { // 1st pass only
        vec4 minnaert = pow( 1.0 - dot( normal, vViewDir.xyz ), 2.0 ) * m_Minnaert * m_Minnaert.w;
        minnaert.a = 0.0;
       diffuseColor.rgb += minnaert.rgb*diffuseColor.rgb;
    //   light.x += minnaert*0.1;
// }
#endif

#ifdef RIM_LIGHTING
// if (length(g_AmbientLightColor.xyz) != 0.0) { // 1st pass only
        vec4 rim = pow( 1.0 - dot( normal, vViewDir.xyz ), 1.5 ) * m_RimLighting * m_RimLighting.w;
        rim.a = 0.0;
       AmbientSum.rgb += rim.rgb*diffuseColor.rgb;
    //   light.x += rim*0.1;
// }
#endif

    #ifdef HAS_LIGHTMAP
     vec3 lightMapColor;
   #ifdef SEPERATE_TEXCOORD
            lightMapColor = texture2D(m_LightMap, texCoord2).rgb * m_LightMapIntensity;
        #else
            lightMapColor = texture2D(m_LightMap, texCoord1).rgb * m_LightMapIntensity;
        #endif
specularColor.rgb *= lightMapColor;
diffuseColor.rgb  *= lightMapColor;
    #endif


        #if defined(SPECULAR_LIGHTING) && !defined(VERTEX_LIGHTING)
       gl_FragColor.rgb =  AmbientSum * diffuseColor.rgb +
                       DiffuseSum.rgb * diffuseColor.rgb  * light.x +
                       SpecularSum2 * specularColor.rgb * light.y;       
        #endif

#if !defined(SPECULAR_LIGHTING) && !defined(VERTEX_LIGHTING)
                        gl_FragColor.rgb = AmbientSum * diffuseColor.rgb +
                                       DiffuseSum.rgb * diffuseColor.rgb  * light.x;
    #endif



#ifdef RIM_LIGHTING_2
// if (length(g_AmbientLightColor.xyz) != 0.0) { // 1st pass only
        vec4 rim2 = pow( 1.0 - dot( normal, vViewDir.xyz ), 1.5 ) * m_RimLighting2 * m_RimLighting2.w;
        rim2.a = 0.0;
       gl_FragColor.rgb += rim2.rgb*diffuseColor.rgb;
    //   light.x += rim2*0.1;
// }
#endif

 #endif
    gl_FragColor.a = alpha;

}
