#version 330 core

layout (location = 0) out vec4 color;

in DATA {
	vec3 position;
	vec3 normal;
	vec2 uv;
} fs_in;

uniform sampler2D textures[16];

//uniform vec3 cameraPosition = vec3(0.0, 0.0, 0.0);
//uniform vec3 lightPos = vec3(5.0, 5.0, 5.0);
//uniform vec3 lightColor = vec3(1.0, 1.0, 1.0);

void main()
{
	// Ambient
	//float ambientStrength = 0.1f;
	//vec3 ambient = ambientStrength * lightColor;

	// Diffuse
	//vec3 vertexNormal = normalize(fs_in.normal);
	//vec3 lightDir = normalize(lightPos - fs_in.position);
	//float diff = max(dot(vertexNormal, lightDir), 0.0);
    //vec3 diffuse = diff * lightColor;

    // Specular
    //float specularStrength = 0.7f;
    //vec3 viewDir = normalize(cameraPosition - fs_in.position);
    //vec3 reflectDir = reflect(-lightDir, vertexNormal);
    //float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    //vec3 specular = specularStrength * spec * lightColor;

	//if (-1.0 > 0.0) {
		//int tid = int(fs_in.tid - 0.5);
		//vec4 texColor = texture(textures[0], fs_in.uv);
	//}

	//color = vec4(ambient + diffuse + specular, 1.0) * texColor;
	color = texture(textures[0], fs_in.uv);
}