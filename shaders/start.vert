#version 150
in vec2 inPosition;// input from the vertex buffer

out vec3 finalPosition, normal, viewDir, lightDirView, lightDirWorld;
out vec2 texCoord;
out float lightDist;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;
uniform int type;
uniform vec3 lightPos;
uniform float time;

const float PI = 3.1415;

float getZ(vec2 vec) {
    return sin(vec.y * PI/2 * 5);
}

vec3 getSphere(vec2 vec, float r) {
    float az = vec.x * PI;// <-1;1> -> <-PI;PI>
    float ze = vec.y * PI / 2.0;// <-1;1> -> <-PI/2;PI/2>

    float x = 1.5 * r * cos(az) * cos(ze);
    float y = r * sin(az) * cos(ze);
    float z = r * sin(ze);
    return vec3(x, y, z);
}

vec3 normSphere(vec2 vec, float r) {
    float az = vec.x * PI;// <-1;1> -> <-PI;PI>
    float ze = vec.y * PI / 2.0;// <-1;1> -> <-PI/2;PI/2>
    vec3 tvaz = vec3(- r * sin(az) * cos(ze), r * cos(az) * cos(ze), 0.0);
    vec3 tvze = vec3(- r * cos(az) * sin(ze), - r * sin(az) * sin(ze), r * cos(ze));
    return cross(tvaz, tvze);
}

vec3 getPyramid(vec2 vec) {
    return vec3(2 * vec.x, 2 * vec.y,
    1 - abs(vec.x + vec.y) - abs(vec.y - vec.x));
}

vec3 normPyramid(vec2 position, vec3 finalPosition){
    vec3 difx = getPyramid(position + vec2(0.1, 0.)) - finalPosition;
    vec3 dify = getPyramid(position + vec2(0., 0.1)) - finalPosition;
    return cross(difx, dify);
}

// Tunel - https://christopherchudzicki.github.io/MathBox-Demos/parametric_surfaces_3D.html
vec3 getTunel(vec2 vec) {
    float u = vec.x * PI;
    float v = vec.y * PI / 2.0;
    vec3 position;

    position.x = 3 * cos(u);
    position.y = 3 * sin(u);
    position.z = v;
    return position / 3;
}

vec3 normTunel(vec2 position, vec3 finalPosition) {
    vec3 difx = getTunel(position + vec2(0.1, 0.)) - finalPosition;
    vec3 dify = getTunel(position + vec2(0., 0.1)) - finalPosition;
    return cross(difx, dify);
}


// Donut - https://stemkoski.github.io/Three.js/Graphulus-Surface.html
vec3 getDonut(vec2 vec) {
    float u = vec.x * PI;
    float v = vec.y * PI - PI / 2;
    vec3 position;

    position.x = time * cos(u) + cos(v) * cos(u);
    position.y = time * sin(u) + cos(v) * sin(u);
    position.z = sin(v);
    return position / 3;
}

vec3 normDonut(vec2 position, vec3 finalPosition) {
    vec3 difx = getDonut(position + vec2(0.1, 0.)) - finalPosition;
    vec3 dify = getDonut(position + vec2(0., 0.1)) - finalPosition;
    return cross(difx, dify);
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

vec3 normBottle(vec2 position, vec3 finalPosition) {
    vec3 difx = getKleinBottle(position + vec2(0.1, 0.)) - finalPosition;
    vec3 dify = getKleinBottle(position + vec2(0., 0.1)) - finalPosition;
    return cross(difx, dify);
}

vec3 getHyperboloid(vec2 vec) {
    float u = vec.x * PI;
    float v = vec.y * PI - PI / 2;
    vec3 position;

    position.x = 1 * sqrt(u*u + 1) * cos(v);
    position.y = 1 * sqrt(u*u + 1) * sin(v);
    position.z = 1 * u;
    return position / 3;
}

vec3 normHyperboloid(vec2 position, vec3 finalPosition) {
    vec3 difx = getHyperboloid(position + vec2(0.1, 0.)) - finalPosition;
    vec3 dify = getHyperboloid(position + vec2(0., 0.1)) - finalPosition;
    return cross(difx, dify);
}

vec3 getSphereForLight(vec2 vec) {
    float az = vec.x * PI;// <-1;1> -> <-PI;PI>
    float ze = vec.y * PI / 2.0;// <-1;1> -> <-PI/2;PI/2>

    float x = 0.03 * cos(az) * cos(ze);
    float y = 0.03 * sin(az) * cos(ze);
    float z = 0.03 * sin(ze);
    return vec3(x, y, z);
}


void main() {
    vec2 position = inPosition * 2 - 1;
    vec3 finalNormal;
    texCoord = inPosition;
    switch (type) {
        case 1:
        finalPosition = getSphere(position, 1.0);
        finalNormal = normSphere(position, 1.0);
        break;
        case 2:
        finalPosition = getPyramid(position);
        finalNormal = normPyramid(position, finalPosition);
        break;
        case 3:
        finalPosition = getDonut(position);
        finalNormal = normDonut(position, finalPosition);
        break;
        case 4:
        finalPosition = getKleinBottle(position);
        finalNormal = normBottle(position, finalPosition);
        break;
        case 5:
        finalPosition = getTunel(position);
        finalNormal = normTunel(position, finalPosition);
        break;
        case 6:
        finalPosition = getHyperboloid(position);
        finalNormal = normHyperboloid(position, finalPosition);
        break;
        case 7:
        finalPosition = getSphereForLight(position);
        break;
    }

    vec4 pos4 = view * model * vec4(finalPosition, 1.0);
    gl_Position = projection * pos4;
    normal = inverse(transpose(mat3(view * model))) * normalize(finalNormal);
    viewDir = - pos4.xyz;
    lightDirView = vec3(view * vec4(lightPos, 1.0)) - pos4.xyz;
    lightDirWorld = lightPos - vec3(model * vec4(finalPosition, 1.0));
    lightDist = length(lightDirWorld);
} 
