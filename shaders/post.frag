#version 150
out vec4 outColor; // output from the fragment shader

in vec2 texCoord;

uniform sampler2D renderTargetTexture;

void main() {
    float width = 800.0;
    float height = 600.0;
    vec4 textureColor2 = texture(renderTargetTexture, vec2(texCoord.x + 1 / width, texCoord.y + 1 / height));

    vec4 textureColor = texture(renderTargetTexture, texCoord);
    float greyValue = (textureColor.r + textureColor.g + textureColor.b) / 3;
    outColor = vec4(vec3(greyValue), 1.0);
} 
