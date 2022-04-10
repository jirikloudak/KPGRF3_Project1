#version 150
in vec2 inPosition; // input from the vertex buffer

out vec3 finalPosition;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;
uniform int type;

const float PI = 3.1415;

float getZ(vec2 vec) {
	return sin(vec.y * PI/2 * 5);
}

vec3 getSphere(vec2 vec) {
	float az = vec.x * PI; // <-1;1> -> <-PI;PI>
	float ze = vec.y * PI / 2.0; // <-1;1> -> <-PI/2;PI/2>
	float r = 1.0;

	float x = r * cos(az) * cos(ze);
	float y = 2 * r * sin(az) * cos(ze);
	float z = 0.5 * r * sin(ze);
	return vec3(x, y, z);
}

vec3 getPyramid(vec2 vec) {
    return vec3(2 * vec.x, 2 * vec.y,
    			1 - abs(vec.x + vec.y) - abs(vec.y - vec.x));
}

// Tunel - https://christopherchudzicki.github.io/MathBox-Demos/parametric_surfaces_3D.html
vec3 getTunel(vec2 vec) {
    float u = vec.x * PI;
	float v = vec.y * PI / 2.0;

	float x = 3 * cos(u);
	float y = 3 * sin(u);
	float z = v;
	return vec3(x, y, z);
}

// Donut - https://stemkoski.github.io/Three.js/Graphulus-Surface.html
vec3 getDonut(vec2 vec) {
    float u = vec.x * PI;
	float v = vec.y * PI - PI / 2;
	vec3 position;

	position.x = 3 * cos(u) + cos(v) * cos(u);
	position.y = 3 * sin(u) + cos(v) * sin(u);
	position.z = sin(v);
	return position / 3;
}


// https://www.wolframalpha.com/input?i=klein+bottle&assumption=%7B%22C%22%2C+%22klein+bottle%22%7D+-%3E+%7B%22Surface%22%7D
vec3 getKleinBottle(vec2 vec) {
    float u = vec.x * PI;
    float v = vec.y * PI - PI / 2;
    vec3 position;

    float x;
    if (u >= 0 && u <= PI) {
        position.x = 6 * (sin(u) + 1) * cos(u) + 3 * (1 - cos(u)/2) * cos(u) * cos(v);
    } else if (u >= PI && u <= 2*PI) {
        position.x = 6 * (sin(u) + 1) * cos(u) - 3 * (1 - cos(u)/2) * cos(u) * cos(v);
    }

    float y;
    if (u >= 0 && u <= PI) {
            position.y = 3 * sin(u) * (1 - cos(u)/2) * cos(v) + 19 * sin(u);
        } else if (u >= PI && u <= 2*PI) {
            position.y = 19 * sin(u);
        }

    float z;
    position.z = 3 * (1 - cos(u)/2) * sin(v);

    return position / 5;
}

void main() {
	vec2 position = inPosition * 2 - 1;

    switch (type) {
        case 1:
            finalPosition = getSphere(position);
            break;
        case 2:
            finalPosition = getPyramid(position);
            break;
        case 3:
            finalPosition = getDonut(position);
            break;
        case 4:
            finalPosition = getKleinBottle(position);
            break;
        case 5:
            finalPosition = getTunel(position);
            break;
    }

	vec4 pos4 = view * model * vec4(finalPosition, 1.0);
	gl_Position = projection * pos4;

} 
