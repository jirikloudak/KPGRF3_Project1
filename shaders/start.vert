#version 150
in vec2 inPosition; // input from the vertex buffer

uniform mat4 projection;
uniform mat4 view;

float getZ(vec2 vec) {
	return sin(vec.y * 3.14 * 2);
}

void main() {
	vec2 position = inPosition * 2.0 - 1.0;
	vec4 pos4 = vec4(position, getZ(position), 1.0);
	gl_Position = projection * view * pos4;
} 
