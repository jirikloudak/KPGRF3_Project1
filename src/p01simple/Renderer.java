package p01simple;
//package lvl2advanced.p01gui.p01simple;

import lwjglutils.OGLBuffers;
import lwjglutils.OGLRenderTarget;
import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import transforms.*;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * @author PGRF FIM UHK
 * @version 2.0
 * @since 2019-09-02
 */
public class Renderer extends AbstractRenderer {

    private int shaderProgram;
    private int locView, locProjection, locType;
    private OGLBuffers buffers, buffers4;
    private Camera camera;
    private Mat4PerspRH projection;
    private OGLTexture2D.Viewer textureViewer;
    private OGLTexture2D textureMosaic;

    private OGLRenderTarget renderTarget;
    private int shaderProgramPostProcessing;

    @Override
    public void init() {
        super.init();

        glEnable(GL_DEPTH_TEST); // zapne z-test (z-buffer) - až po new OGLTextRenderer (uvnitř super.init())
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL); // vyplnění přivrácených i odvrácených stran

        shaderProgram = ShaderUtils.loadProgram("/start");
        locView = glGetUniformLocation(shaderProgram, "view");
        locProjection = glGetUniformLocation(shaderProgram, "projection");
        locType = glGetUniformLocation(shaderProgram, "type");

        shaderProgramPostProcessing = ShaderUtils.loadProgram("/post");

        buffers = GridFactory.generateGrid(50, 50);

        // pro post-processingový krok stačí jeden quad (= 2 trojúhelníky = 4 vrcholy)
        buffers4 = GridFactory.generateGrid(2, 2);

        camera = new Camera()
                .withPosition(new Vec3D(6, 6, 5))
                .withAzimuth(5 / 4f * Math.PI)
                .withZenith(-1 / 5f * Math.PI);

//        Camera cameraLight = new Camera()
//                .withPosition(...);
//        cameraLight = cameraLight.right(1);
//        cameraLight.getPosition();
//
//        Vec3D vec3D = new Vec3D(1, 2, 3);
//        vec3D = vec3D.withX(vec3D.getX() + 1);
//
//        Mat4 model = new Mat4Transl(1, 0, 0);

        projection = new Mat4PerspRH(
                Math.PI / 3,
                LwjglWindow.HEIGHT / (float) LwjglWindow.WIDTH,
                1,
                50
        );

        renderTarget = new OGLRenderTarget(1000, 1000);

        textureViewer = new OGLTexture2D.Viewer();
        try {
            textureMosaic = new OGLTexture2D("./textures/mosaic.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void display() {
        // znovu zapnout z-test (kvůli textRenderer)
        glEnable(GL_DEPTH_TEST);

        renderMainScene();

        renderPostProcessing();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, width, height);
        textureViewer.view(textureMosaic, -1, -1, 0.5);
        textureViewer.view(renderTarget.getColorTexture(), -1, -0.5, 0.5);
        textureViewer.view(renderTarget.getDepthTexture(), -1, 0, 0.5);
        textRenderer.addStr2D(width - 90, height - 3, " (c) PGRF UHK");
    }

    private void renderMainScene() {
        glUseProgram(shaderProgram);

        renderTarget.bind(); // a nastaví si vlastní viewport
        glClearColor(0.5f, 0.0f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUniformMatrix4fv(locView, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjection, false, projection.floatArray());

        // vykreslit první těleso
        glUniform1i(locType, 1);
        buffers.draw(GL_TRIANGLES, shaderProgram);

        // vykreslit druhé těleso (do stejné scény)
        glUniform1i(locType, 2);
        buffers.draw(GL_TRIANGLES, shaderProgram);
    }

    private void renderPostProcessing() {
        glUseProgram(shaderProgramPostProcessing);

        // renderování do obrazovky (framebuffer=0)
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, width, height);
        glClearColor(0.0f, 0.5f, 0.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        renderTarget.bindColorTexture(shaderProgramPostProcessing, "renderTargetTexture", 0);
//        renderTarget.bindDepthTexture(shaderProgramPostProcessing, "nejakejmeno", 1);

        buffers4.draw(GL_TRIANGLES, shaderProgramPostProcessing);
    }

}
