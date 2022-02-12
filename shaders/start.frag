#version 150
out vec4 outColor; // output from the fragment shader

uniform int type;

void main() {
    if (type == 1) {
        outColor = vec4(0.0, 1.0, 1.0, 1.0);
    } else if (type == 2) {
        outColor = vec4(1.0, 1.0, 0.0, 1.0);
    }
} 
