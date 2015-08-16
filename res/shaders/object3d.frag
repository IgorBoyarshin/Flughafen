#version 330 core

layout (location = 0) out vec4 color;

in DATA {
	vec3 position;
	vec3 normal;
    vec4 color;
	vec2 uv;
	float tid;
} fs_in;

uniform sampler2D textures[16];

void main()
{
	vec4 texColor = fs_in.color;
	if (fs_in.tid > 0.0) {
		int tid = int(fs_in.tid - 0.5);
		texColor = texture(textures[tid], fs_in.uv);
	}

	color = texColor;
}