#version 150
in vec2 inPosition; // input from the vertex buffer

out vec2 texCoord;

void main() {
	texCoord = inPosition;

	// grid je <0;1> - chci <-1;1>
	vec2 position = inPosition * 2 - 1;
	gl_Position = vec4(position, 0.01, 1.0);
}
