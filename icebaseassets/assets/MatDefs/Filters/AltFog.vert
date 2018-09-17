attribute vec3 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;
 
uniform mat4 g_WorldViewMatrix, g_ViewMatrix, g_ProjectionMatrix;
 
out vec3 world_pos;
out vec3 world_normal;
out vec2 texCoord;
out vec4 viewSpace;
 
void main() {
	 
	//used for lighting models
	world_pos = (g_WorldViewMatrix * vec4(inPosition,1)).xyz;
	world_normal = normalize(mat3(g_WorldViewMatrix) * inNormal);
	texcoord = inTexCoord;
	 
	//send it to fragment shader
	viewSpace = g_ViewMatrix * g_WorldViewMatrix * vec4(inPosition,1);
	gl_Position = g_ProjectionMatrix * viewSpace;
 
}