#version 150
in vec2 inPosition;// input from the vertex buffer

out vec2 texCoord;

void main() {
    vec2 position = inPosition * 2 - 1;// grid je <0;1> - chci <-1;1>
    texCoord = inPosition;
    gl_Position = vec4(position, 0.0, 1.0);
}
