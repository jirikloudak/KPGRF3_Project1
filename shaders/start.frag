#version 150
in vec2 texCoord;
in vec3 finalPosition, normal, lightDirView, lightDirWorld, viewDir;
in float lightDist;

out vec4 outColor;// output from the fragment shader

uniform int type;
uniform int display;
uniform sampler2D textureID;
uniform float ambientStrength, diffuseStrength, specularStrength, spotCutOff;
uniform vec3 lightColor, spotDir;

void main() {
    vec3 color, finalColor;
    vec3 nNormal = normalize(normal);
    if (display == 1){
        //Basic color + light
        color = vec3(0.5, 0, 0.5);
    } else if (display == 2) {
        //Texture + light
        color = vec3(texture(textureID, texCoord));
    } else if (display == 3) {
        //Only texture without light
        color = vec3(texture(textureID, texCoord));
    } else if (display == 4) {
        //Normal
        color = nNormal;
    } else if (display == 5) {
        //Depth
        color = vec3(gl_FragCoord.w * 3);
    } else if (display == 6) {
        //Position
        color = finalPosition;
    } else if (display == 7) {
        //Light distance (not working)
        color = vec3(lightDist);
    }
    const float attConst = 0.5f;
    const float attLin = 0.05f;
    const float attQuad = 0.002f;
    if (type == 7){
        outColor = vec4(1.0, 0.5, 0.0, 0.1);
    } else {
        if (display == 1 || display == 2) {
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
        } else {
            outColor = vec4(color, 1.0);
        }
    }

} 
