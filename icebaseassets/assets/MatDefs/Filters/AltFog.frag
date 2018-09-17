attribute vec4 out_color;
 
uniform vec4 g_LightPosition;
uniform vec3 g_CameraPosition;

uniform sampler2D m_Texture;
 
//0 linear; 1 exponential; 2 exponential square
uniform int m_FogMode;
//0 plane based; 1 range based
uniform bool m_DepthFog;
 
//can pass them as uniforms
const vec3 DiffuseLight = vec3(0.15, 0.05, 0.0);
const vec3 RimColor = vec3(0.2, 0.2, 0.2);
 
//from vertex shader
in vec3 world_pos;
in vec3 world_normal;
in vec4 viewSpace;
in vec2 texcoord;
 
uniform vec4 m_FogColor;
uniform float m_FogDensity;
 
void main() {
 
	vec3 tex1 = texture(m_Texture, texcoord).rgb;
 
	//get light an view directions
	vec3 L = normalize( g_LightPosition - world_pos);
	vec3 V = normalize( g_CameraPosition - world_pos);
	 
	//diffuse lighting
	vec3 diffuse = DiffuseLight * max(0, dot(L,world_normal));
	 
	//rim lighting
	float rim = 1 - max(dot(V, world_normal), 0.0);
	rim = smoothstep(0.6, 1.0, rim);
	vec3 finalRim = RimColor * vec3(rim, rim, rim);
	//get all lights and texture
	vec3 lightColor = finalRim + diffuse + tex1;
	 
	vec3 finalColor = vec3(0, 0, 0);
	 
	//distance
	float dist = 0;
	float fogFactor = 0;
	 
	//compute distance used in fog equations
	if(m_DepthFog)//select plane based vs range based
	{
	   //range based
	   dist = length(viewSpace);
	}
	else
	{
	  //plane based
	  dist = abs(viewSpace.z);
	  //dist = (gl_FragCoord.z / gl_FragCoord.w);
	}
	 
	if(m_FogMode == 0)//linear fog
	{
	   // 20 - fog starts; 80 - fog ends
	   fogFactor = (80 - dist)/(80 - 20);
	   fogFactor = clamp( fogFactor, 0.0, 1.0 );
	 
	   //if you inverse color in glsl mix function you have to
	   //put 1.0 - fogFactor
	   finalColor = mix(m_FogColor, lightColor, fogFactor);
	}
	else if( m_FogMode == 1)// exponential fog
	{
	    fogFactor = 1.0 /exp(dist * m_FogDensity);
	    fogFactor = clamp( fogFactor, 0.0, 1.0 );
	 
	    // mix function m_FogColor⋅(1−fogFactor) + lightColor⋅fogFactor
	    finalColor = mix(m_FogColor, lightColor, fogFactor);
	}
	else if( m_FogMode == 2)
	{
	   fogFactor = 1.0 /exp( (dist * m_FogDensity)* (dist * m_FogDensity));
	   fogFactor = clamp( fogFactor, 0.0, 1.0 );
	 
	   finalColor = mix(m_FogColor, lightColor, fogFactor);
	}
	 
	//show fogFactor depth(gray levels)
	//fogFactor = 1 - fogFactor;
	//out_color = vec4( fogFactor, fogFactor, fogFactor,1.0 );
	//out_color = vec4(finalColor, 1); 
	//out_color = tex1;
	out_color = vec4(1,0,1,1);
}