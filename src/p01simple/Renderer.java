package p01simple;
//package lvl2advanced.p01gui.p01simple;

import lwjglutils.OGLBuffers;
import lwjglutils.OGLRenderTarget;
import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import transforms.*;

import java.io.IOException;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL31.glPrimitiveRestartIndex;

/**
 * @author PGRF FIM UHK
 * @version 2.0
 * @since 2019-09-02
 */
public class Renderer extends AbstractRenderer {

    private int shaderProgram, shaderProgramPostProcessing;

    private int locView, locProjection, locType, locModel, locTime, locDisplayModel, locPostDisplayModel;
    private int locLightPosition;
    private int locLightColor;
    private int locSpotDirection;
    private int locAmbientStrength;
    private int locDiffuseStrength;
    private int locSpecularStrength;
    private int drawMode = 1, display = 1, postDisplay = 0;
    private OGLBuffers buffers, buffersPostProcessing;
    private Camera camera, cameraForLight;
    private Mat4 projection;
    private Mat4 matTranslSphere, matTranslDonut, matTranslBottle, matRotZ, matRotX, matRotY, matTranslTunel, matTranslHyperboloid, matRotSphere, matTranslWall, matTranslWall2;
    private OGLTexture2D.Viewer textureViewer;
    private OGLTexture2D texture2D;
    boolean mouseButton1 = false, perspProjection = true, grow = false, mouseButton2 = false;
    double ox, oy;
    private boolean sceneMode = true;
    private float time = 0, velocity = 0;
    private OGLRenderTarget renderTarget;


    @Override
    public void init() {
        super.init();
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        shaderProgram = ShaderUtils.loadProgram("/start");
        shaderProgramPostProcessing = ShaderUtils.loadProgram("/post");

        locModel = glGetUniformLocation(shaderProgram, "model");
        locView = glGetUniformLocation(shaderProgram, "view");
        locProjection = glGetUniformLocation(shaderProgram, "projection");
        locType = glGetUniformLocation(shaderProgram, "type");
        locLightPosition = glGetUniformLocation(shaderProgram, "lightPos");
        locLightColor = glGetUniformLocation(shaderProgram, "lightColor");
        locAmbientStrength = glGetUniformLocation(shaderProgram, "ambientStrength");
        locDiffuseStrength = glGetUniformLocation(shaderProgram, "diffuseStrength");
        locSpecularStrength = glGetUniformLocation(shaderProgram, "specularStrength");
        //int locSpotCutOff = glGetUniformLocation(shaderProgram, "spotCutOff");
        locSpotDirection = glGetUniformLocation(shaderProgram, "spotDir");
        locTime = glGetUniformLocation(shaderProgram, "time");
        locDisplayModel = glGetUniformLocation(shaderProgram, "display");
        locPostDisplayModel = glGetUniformLocation(shaderProgramPostProcessing, "postDisplay");

        buffers = GridFactory.generateGrid(100, 100);
        buffersPostProcessing = GridFactory.generateGrid(2, 2);

        renderTarget = new OGLRenderTarget(1024, 1024);

        camera = new Camera()
                .withPosition(new Vec3D(6, 6, 5))
                .withAzimuth(5 / 4f * Math.PI)
                .withZenith(-1 / 5f * Math.PI);

        cameraForLight = new Camera()
                .withPosition(new Vec3D(7, 3, 5))
                .withAzimuth(5 / 4f * Math.PI)
                .withZenith(-1 / 5f * Math.PI);

        textureViewer = new OGLTexture2D.Viewer();
        try {
            String textureMaterial = "./textures/mosaic.jpg";
            texture2D = new OGLTexture2D(textureMaterial);
        } catch (IOException e) {
            e.printStackTrace();
        }

        matTranslSphere = new Mat4Transl(0.0, 0.0, 2.0);
        matTranslDonut = new Mat4Transl(0.0, 0.0, -3.2);
        matTranslBottle = new Mat4Transl(0.0, 1.8, 0.0);
        matTranslTunel = new Mat4Transl(0.0, 0.0, 0.0);
        matTranslHyperboloid = new Mat4Transl(0.0, 0.0, 0.0);
        matTranslWall = new Mat4Scale(5).mul(new Mat4Transl(0.0, 0.0, -5.0)).mul(new Mat4RotY(1.5));
        matTranslWall2 = new Mat4Scale(5).mul(new Mat4Transl(0.0, 0.0, -5.0));
    }

    public void render() {
        glUseProgram(shaderProgram);
        renderTarget.bind();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        texture2D.bind(shaderProgram, "textureId", 0);

        glUniformMatrix4fv(locView, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjection, false, projection.floatArray());

        float ambientStrength = 0.1f;
        glUniform1f(locAmbientStrength, ambientStrength);
        float diffuseStrength = 1.0f;
        glUniform1f(locDiffuseStrength, diffuseStrength);
        float specularStrength = 0.5f;
        glUniform1f(locSpecularStrength, specularStrength);
        glUniform3f(locSpotDirection,
                (float) cameraForLight.getViewVector().getX(),
                (float) cameraForLight.getViewVector().getY(),
                (float) cameraForLight.getViewVector().getZ());
        glUniform3f(locLightPosition,
                (float) cameraForLight.getPosition().getX(),
                (float) cameraForLight.getPosition().getY(),
                (float) cameraForLight.getPosition().getZ());
        glUniform3f(locLightColor, 1.0f, 1.0f, 1.0f);
        glUniform1f(locTime, time);
        glUniform1i(locDisplayModel, display);

        if (sceneMode) {
            // Draw Sphere
            glUniformMatrix4fv(locModel, false, matTranslSphere.mul(matRotSphere).floatArray());
            glUniform1i(locType, 1);
            buffers.draw(GL_TRIANGLES, shaderProgram);

            // Draw Pyramid
            glUniformMatrix4fv(locModel, false, matRotZ.floatArray());
            glUniform1i(locType, 2);
            buffers.draw(GL_TRIANGLES, shaderProgram);

            //Draw Donut
            glUniformMatrix4fv(locModel, false, matTranslDonut.mul(matRotY).floatArray());
            glUniform1i(locType, 3);
            buffers.draw(GL_TRIANGLES, shaderProgram);
        } else {
            //Draw Bottle
            glUniformMatrix4fv(locModel, false, matTranslBottle.mul(matRotZ).floatArray());
            glUniform1i(locType, 4);
            buffers.draw(GL_TRIANGLES, shaderProgram);

            //Draw Tunel
            glUniformMatrix4fv(locModel, false, matTranslTunel.mul(matRotX).floatArray());
            glUniform1i(locType, 5);
            buffers.draw(GL_TRIANGLES, shaderProgram);

            //Draw Tunel
            glUniformMatrix4fv(locModel, false, matTranslHyperboloid.mul(matRotX).floatArray());
            glUniform1i(locType, 6);
            buffers.draw(GL_TRIANGLES, shaderProgram);
        }

        glUniformMatrix4fv(locModel, false, matTranslWall.floatArray());
        glUniform1i(locType, 8);
        buffers.draw(GL_TRIANGLES, shaderProgram);

        glUniformMatrix4fv(locModel, false, matTranslWall2.floatArray());
        glUniform1i(locType, 8);
        buffers.draw(GL_TRIANGLES, shaderProgram);

        glUniformMatrix4fv(locModel, false, new Mat4Transl(cameraForLight.getPosition()).floatArray());
        glUniform1i(locType, 7);
        buffers.draw(GL_TRIANGLE_STRIP, shaderProgram);
    }

    private void postProcessingRender() {
        glUseProgram(shaderProgramPostProcessing);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, width, height);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        glUniform1i(locPostDisplayModel, postDisplay);

        renderTarget.getColorTexture().bind(shaderProgramPostProcessing, "texturePost", 0);
        buffersPostProcessing.draw(GL_TRIANGLE_STRIP, shaderProgramPostProcessing);
    }


    @Override
    public void display() {
        String topText = "[SPACE] to change scene, [WASD] + left mouse to move, [C] ort/persp camera, [M] polygon mode, [Arrows] + right mouse move light source";
        String topText2 = "[H] to change display mode, [P] to change post mode";
        String infoText = "Display: ";
        String sceneText = "Scene: ";
        String postText = "Post mode: ";
        String spotText = "Spot vector: ";

        if (display == 1) {
            infoText += "Basic color + light";
        } else if (display == 2) {
            infoText += "Texture + light";
        } else if (display == 3) {
            infoText += "Only texture without light";
        } else if (display == 4) {
            infoText += "Normal";
        } else if (display == 5) {
            infoText += "Depth";
        } else if (display == 6) {
            infoText += "Position";
        } else if (display == 7) {
            infoText += "Light distance (not working)";
        }

        if (postDisplay == 0) {
            postText += "No effect";
        } else if (postDisplay == 1) {
            postText += "Grayscale";
        } else if (postDisplay == 2) {
            postText += "Red only";
        } else if (postDisplay == 3) {
            postText += "Green only";
        } else if (postDisplay == 4) {
            postText += "Blue only";
        } else if (postDisplay == 5) {
            postText += "Negative";

        }
        if (sceneMode) {
            sceneText += "Scene 1";
        } else {
            sceneText += "Scene 2";
        }

        spotText += cameraForLight.getViewVector();

        movementGenerator();
        setProjection();
        setMode();
        glEnable(GL_DEPTH_TEST);
        render();
        postProcessingRender();
        glDisable(GL_DEPTH_TEST);

        textureViewer.view(texture2D, -1, -1, 0.5);
        textureViewer.view(renderTarget.getColorTexture(), -1, -0.5, 0.5);
        textRenderer.addStr2D(5, 15, topText);
        textRenderer.addStr2D(5, 30, topText2);
        textRenderer.addStr2D(5, 230, infoText);
        textRenderer.addStr2D(5, 250, sceneText);
        textRenderer.addStr2D(5, 270, postText);
        textRenderer.addStr2D(5, 290, spotText);
        textRenderer.addStr2D(width - 180, height - 3, "Jiří Klouda (c) PGRF UHK");
    }

    public void movementGenerator() {
        if (grow) {
            if (time <= Math.PI * 2) time += 0.01;
            if (time > Math.PI * 2) grow = false;
        } else {
            if (time > 0) time -= 0.01;
            if (time <= 0) grow = true;
        }
        if (velocity <= Math.PI * 2) velocity += 0.01;
        else velocity = 0;
        matRotZ = new Mat4RotZ(velocity * -1);
        matRotSphere = new Mat4RotZ(velocity);
        matRotX = new Mat4RotX(velocity);
        matRotY = new Mat4RotY(velocity);
    }

    private final GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                switch (key) {
                    case GLFW_KEY_W:
                        camera = camera.forward(1);
                        break;
                    case GLFW_KEY_D:
                        camera = camera.right(1);
                        break;
                    case GLFW_KEY_S:
                        camera = camera.backward(1);
                        break;
                    case GLFW_KEY_A:
                        camera = camera.left(1);
                        break;
                    case GLFW_KEY_UP:
                        cameraForLight = cameraForLight.forward(1);
                        break;
                    case GLFW_KEY_RIGHT:
                        cameraForLight = cameraForLight.right(1);
                        break;
                    case GLFW_KEY_DOWN:
                        cameraForLight = cameraForLight.backward(1);
                        break;
                    case GLFW_KEY_LEFT:
                        cameraForLight = cameraForLight.left(1);
                        break;
                    case GLFW_KEY_LEFT_CONTROL:
                        camera = camera.down(1);
                        break;
                    case GLFW_KEY_LEFT_SHIFT:
                        camera = camera.up(1);
                        break;
                    case GLFW_KEY_SPACE:
                        //camera = camera.withFirstPerson(!camera.getFirstPerson());
                        sceneMode = !sceneMode;
                        break;
                    case GLFW_KEY_C:
                        perspProjection = !perspProjection;
                        break;
                    case GLFW_KEY_H:
                        if (display < 7) {
                            display += 1;
                        } else display = 1;
                        break;
                    case GLFW_KEY_M:
                        if (drawMode < 3) {
                            drawMode += 1;
                        } else {
                            drawMode = 1;
                        }
                        break;
                    case GLFW_KEY_P:
                        if (postDisplay < 5) {
                            postDisplay += 1;
                        } else postDisplay = 0;
                        break;
                }
            }
        }
    };

    private final GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;
            mouseButton2 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_2) == GLFW_PRESS;

            if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                mouseButton1 = true;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                ox = xBuffer.get(0);
                oy = yBuffer.get(0);
            }

            if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE) {
                mouseButton1 = false;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);
                camera = camera.addAzimuth(Math.PI * (ox - x) / width)
                        .addZenith(Math.PI * (oy - y) / width);
                cameraForLight = cameraForLight.addAzimuth(Math.PI * (ox - x) / width)
                        .addZenith(Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };

    private final GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mouseButton1) {
                camera = camera.addAzimuth(Math.PI * (ox - x) / width)
                        .addZenith(Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
            if (mouseButton2) {
                cameraForLight = cameraForLight.addAzimuth(Math.PI * (ox - x) / width)
                        .addZenith(Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };

    @Override
    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    @Override
    public GLFWMouseButtonCallback getMouseCallback() {
        return mbCallback;
    }

    @Override
    public GLFWCursorPosCallback getCursorCallback() {
        return cpCallbacknew;
    }

    public void setProjection() {
        if (perspProjection) {
            projection = new Mat4PerspRH(Math.PI / 3, LwjglWindow.HEIGHT / (float) LwjglWindow.WIDTH, 1, 20);
        } else {
            projection = new Mat4OrthoRH(50, 50, 0.1, 100);
        }
    }

    public void setMode() {
        switch (drawMode) {
            case 1 -> glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
            case 2 -> glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            case 3 -> glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
        }
    }

}