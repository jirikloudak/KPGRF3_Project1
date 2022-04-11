#version 150
in vec2 texCoord;
in vec3 finalPosition, normal, lightDirView, lightDirWorld, viewDir;
in float lightDist;

out vec4 outColor; // output from the fragment shader

uniform int type;
uniform int display;
uniform sampler2D textureID;
uniform float ambientStrength, diffuseStrength, specularStrength, spotCutOff;
uniform vec3 lightColor, spotDir;

void main() {
    const float attConst = 1.0f;
    const float attLin = 0.045f;
    const float attQuad = 0.0075f;
    vec3 color, finalColor;
    vec3 nNormal = normalize(normal);
    color = vec3(texture(textureID, texCoord));

    float att = 1.0 / (attConst + attLin * lightDist + attQuad * lightDist * lightDist);
    // ambient
    vec3 ambient = ambientStrength * lightColor;

    // diffuse
    vec3 nLightDir = normalize(lightDirView);
    float diff = max(dot(nNormal, nLightDir), 0.0);
    vec3 diffuse = diffuseStrength * diff * lightColor;

    // specular
    vec3 nViewDir = normalize(viewDir);
    vec3 nHalfwayDir = normalize(nLightDir + nViewDir);
    float spec = pow(max(dot(nNormal, nHalfwayDir), 0.0), 64);
    vec3 specular = specularStrength * spec * lightColor;

    float spotEffect = max(dot(normalize(spotDir), normalize(- lightDirWorld)), 0);
    if (spotEffect > spotCutOff) {
        float blend = clamp((spotEffect - spotCutOff) / (1 - spotCutOff), 0.0, 1.0);
        finalColor = mix(ambient, ambient + att * (diffuse + specular), blend);
    } else finalColor = ambient;
    outColor = vec4(finalColor * color, 1.0);
    outColor = vec4(finalColor * color, 1.0);
    //outColor = vec4(color, 1.0);
    switch (display){
        case 1:
            //color = vec4(0.5, 0.0, 1.0, 1.0);
            break;
    }


} 
