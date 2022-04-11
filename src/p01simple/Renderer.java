package p01simple;
//package lvl2advanced.p01gui.p01simple;

import lwjglutils.OGLBuffers;
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
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author PGRF FIM UHK
 * @version 2.0
 * @since 2019-09-02
 */
public class Renderer extends AbstractRenderer {

    private int shaderProgram;
    private float ambientStrength = 0.1f, diffuseStrength = 1.0f, specularStrength = 0.5f;

    private int locView, locProjection, locType, locModel, locTime;
    private int locLightPosition, locLightColor, locSpotDirection, locAmbientStrength, locDiffuseStrength, locSpecularStrength, locSpotCutOff;
    private  int drawMode = 1;
    private OGLBuffers buffers;
    private Camera camera, cameraForLight;
    private Mat4 projection;
    private Mat4 matTranslSphere, matTranslDonut, matTranslBottle, matRotZ, matRotX, matRotY, matTranslTunel, matTranslHyperboloid;
    private OGLTexture2D.Viewer textureViewer;
    private OGLTexture2D textureSurface;
    boolean mouseButton1 = false, perspProjection = true;
    double ox, oy;
    private boolean draw = true, sceneMode = true;
    private float time = 0;


    @Override
    public void init() {
        super.init();
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        glEnable(GL_DEPTH_TEST); // zapne z-test (z-buffer) - až po new OGLTextRenderer (uvnitř super.init())
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL); // vyplnění přivrácených i odvrácených stran
        shaderProgram = ShaderUtils.loadProgram("/start");

        locModel = glGetUniformLocation(shaderProgram, "model");
        locView = glGetUniformLocation(shaderProgram, "view");
        locProjection = glGetUniformLocation(shaderProgram, "projection");
        locType = glGetUniformLocation(shaderProgram, "type");
        locLightPosition = glGetUniformLocation(shaderProgram, "lightPos");
        locLightColor = glGetUniformLocation(shaderProgram, "lightColor");
        locAmbientStrength = glGetUniformLocation(shaderProgram, "ambientStrength");
        locDiffuseStrength = glGetUniformLocation(shaderProgram, "diffuseStrength");
        locSpecularStrength = glGetUniformLocation(shaderProgram, "specularStrength");
        locSpotCutOff = glGetUniformLocation(shaderProgram, "spotCutOff");
        locSpotDirection = glGetUniformLocation(shaderProgram, "spotDir");
        locTime = glGetUniformLocation(shaderProgram, "time");


        buffers = GridFactory.generateGrid(50, 50);

        camera = new Camera()
                .withPosition(new Vec3D(6, 6, 5))
                .withAzimuth(5 / 4f * Math.PI)
                .withZenith(-1 / 5f * Math.PI);

        cameraForLight = new Camera()
                .withPosition(new Vec3D(4, 3, 4))
                .withAzimuth(5 / 4f * Math.PI)
                .withZenith(-1 / 5f * Math.PI);

        textureViewer = new OGLTexture2D.Viewer();
        try {
            textureSurface = new OGLTexture2D("./textures/wood.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        matTranslSphere = new Mat4Transl(0.0, 0.0, 1.5);
        matTranslDonut = new Mat4Transl(0.0, 0.0, -3.2);
        matTranslBottle = new Mat4Transl(0.0, 1.8, 0.0);
        matTranslTunel = new Mat4Transl(0.0, 0.0, 0.0);
        matTranslHyperboloid = new Mat4Transl(0.0, 0.0, 0.0);

    }

    public void render(){
        glUseProgram(shaderProgram);
        setMode();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        textureSurface.bind(shaderProgram, "textureId", 0);

        glUniformMatrix4fv(locView, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjection, false, projection.floatArray());

        glUniform1f(locAmbientStrength, ambientStrength);
        glUniform1f(locDiffuseStrength, diffuseStrength);
        glUniform1f(locSpecularStrength, specularStrength);
        glUniform3f(locSpotDirection,
                (float) cameraForLight.getViewVector().getX(),
                (float) cameraForLight.getViewVector().getY(),
                (float) cameraForLight.getViewVector().getZ());
        glUniform3f(locLightPosition,
                (float) cameraForLight.getPosition().getX(),
                (float) cameraForLight.getPosition().getY(),
                (float) cameraForLight.getPosition().getZ());
        glUniform3f(locLightColor, 0.8f, 0.8f, 0.8f);
        glUniform1f(locTime, time);

        if (sceneMode){
            // Draw Sphere
            glUniformMatrix4fv(locModel, false, matTranslSphere.floatArray());
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

        glUniformMatrix4fv(locModel, false, new Mat4Transl(cameraForLight.getPosition()).floatArray());
        glUniform1i(locType, 7);
        buffers.draw(GL_TRIANGLE_STRIP, shaderProgram);

    }

    @Override
    public void display() {
        movementGenerator();

        // znovu zapnout z-test (kvůli textRenderer)
        glEnable(GL_DEPTH_TEST);

        // nutno opravit viewport (kvůli textRenderer)
        glViewport(0, 0, width, height);
        setProjection();
        render();


        textureViewer.view(textureSurface, -1, -1, 0.5);
        textRenderer.addStr2D(width - 90, height - 3, " (c) PGRF UHK");
    }

    public void movementGenerator(){
        if (draw) {
            if (time <= Math.PI * 2) time += 0.01;
            else time = 0;
        }
        matRotZ = new Mat4RotZ(time*-1);
        matRotX = new Mat4RotX(time);
        matRotY = new Mat4RotY(time);
    }

    private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            if (action == GLFW_PRESS || action == GLFW_REPEAT){
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
                    case GLFW_KEY_R:
                        camera = camera.mulRadius(0.9f);
                        break;
                    case GLFW_KEY_F:
                        camera = camera.mulRadius(1.1f);
                        break;
                    case GLFW_KEY_C:
                        perspProjection = !perspProjection;
                        break;
                    case GLFW_KEY_M:
                        if (drawMode < 3){
                            drawMode +=1;
                            System.out.println(drawMode);
                        } else {
                        drawMode = 1;
                        System.out.println(drawMode);
                    }
                        break;
                }
            }
        }
    };

    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback () {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

            if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
                mouseButton1 = true;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                ox = xBuffer.get(0);
                oy = yBuffer.get(0);
            }

            if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE){
                mouseButton1 = false;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);
                camera = camera.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };

    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mouseButton1) {
                camera = camera.addAzimuth((double) Math.PI * (ox - x) / width)
                        .addZenith((double) Math.PI * (oy - y) / width);
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

    public void setProjection(){
        if (perspProjection){
            projection = new Mat4PerspRH(Math.PI / 3, LwjglWindow.HEIGHT / (float) LwjglWindow.WIDTH, 1, 20);
        } else {
            projection = new Mat4OrthoRH(50, 50, 0.1, 100);
        }
    }

    public void setMode(){
        switch (drawMode){
            case 1:
                glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
                break;
            case 2:
                glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
                break;
            case 3:
                glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
                break;
        }
    }

}