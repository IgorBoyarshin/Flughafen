#version 330 core

layout (location = 0) in vec4 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 uv;

uniform mat4 pr_matrix = mat4(1.0);
uniform mat4 vw_matrix = mat4(1.0);
uniform mat4 ml_matrix = mat4(1.0);

out DATA {
	vec3 position;
	vec3 normal;
	vec2 uv;
} vs_out;

void main()
{
    mat4 ml_matrix = mat4(1.0);

	vec4 worldPosition = ml_matrix * position;

	gl_Position = pr_matrix * vw_matrix * worldPosition;
	//gl_Position = worldPosition;

	vs_out.position = vec3(worldPosition.x, worldPosition.y, worldPosition.z);
	vec4 transformedNormal = ml_matrix * vec4(normal, 0.0);
	vs_out.normal = vec3(transformedNormal.x, transformedNormal.y, transformedNormal.z);
	vs_out.uv = uv * 3;
}