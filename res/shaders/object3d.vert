#version 330 core

layout (location = 0) in vec4 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec4 color;
layout (location = 3) in vec2 uv;
layout (location = 4) in float tid;
layout (location = 5) in float mid;

uniform mat4 pr_matrix = mat4(1.0);
uniform mat4 vw_matrix = mat4(1.0);

uniform mat4 modelMatrices[16];

out DATA {
	vec3 position;
	vec3 normal;
    vec4 color;
	vec2 uv;
	float tid;
} vs_out;

void main()
{
    mat4 ml_matrix = mat4(1.0);
    if (mid > 0.0) {
        ml_matrix = modelMatrices[int(mid - 0.5)];
    }

	vec4 worldPosition = ml_matrix * position;

	gl_Position = pr_matrix * vw_matrix * worldPosition;

	vs_out.position = vec3(worldPosition.x, worldPosition.y, worldPosition.z);
	vs_out.normal = normal; // can remove
	vs_out.color = color;
	vs_out.uv = uv;
	vs_out.tid = tid;
}