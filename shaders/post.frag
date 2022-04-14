#version 150
out vec4 outColor;// output from the fragment shader

in vec2 texCoord;

uniform sampler2D renderTargetTexture;
uniform int postDisplay;

void main() {
    vec4 color = texture(renderTargetTexture, texCoord);
    if (postDisplay == 1){ //grayscale mode
        float grey = 0.21 * color.r + 0.71 * color.g + 0.07 * color.b;
        float u_colorFactor = 0;
        color = vec4(color.r * u_colorFactor + grey * (1.0 - u_colorFactor), color.g * u_colorFactor + grey * (1.0 - u_colorFactor), color.b * u_colorFactor + grey * (1.0 - u_colorFactor), 1.0);
    } else if (postDisplay == 2) { // red tone
        color = vec4(color.r, color.g * 0, color.b * 0, 1.0);
    } else if (postDisplay == 3) { // green tone
        color = vec4(0 * color.r, color.g, color.b * 0, 1.0);
    } else if (postDisplay == 4) { // blue tone
        color = vec4(color.r * 0, color.g * 0, color.b, 1.0);
    } else if (postDisplay == 5) { // negative mode
        color = vec4(1 - color.rgb, 1.0);
    }
    outColor = color;
}
