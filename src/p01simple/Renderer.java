package p01simple;
//package lvl2advanced.p01gui.p01simple;

import lwjglutils.OGLBuffers;
import lwjglutils.OGLTexture2D;
import lwjglutils.ShaderUtils;
import transforms.Camera;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

/**
 * @author PGRF FIM UHK
 * @version 2.0
 * @since 2019-09-02
 */
public class Renderer extends AbstractRenderer {

    private int shaderProgram;
    private int locView, locProjection, locType;
    private OGLBuffers buffers;
    private Camera camera;
    private Mat4PerspRH projection;
    private OGLTexture2D.Viewer textureViewer;
    private OGLTexture2D textureMosaic;

    @Override
    public void init() {
        super.init();
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        glEnable(GL_DEPTH_TEST); // zapne z-test (z-buffer) - až po new OGLTextRenderer (uvnitř super.init())
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL); // vyplnění přivrácených i odvrácených stran
        shaderProgram = ShaderUtils.loadProgram("/start");

        locView = glGetUniformLocation(shaderProgram, "view");
        locProjection = glGetUniformLocation(shaderProgram, "projection");
        locType = glGetUniformLocation(shaderProgram, "type");

        buffers = GridFactory.generateGrid(50, 50);

        camera = new Camera()
                .withPosition(new Vec3D(6, 6, 5))
                .withAzimuth(5 / 4f * Math.PI)
                .withZenith(-1 / 5f * Math.PI);

        projection = new Mat4PerspRH(
                Math.PI / 3,
                LwjglWindow.HEIGHT / (float) LwjglWindow.WIDTH,
                1,
                20
        );

        textureViewer = new OGLTexture2D.Viewer();
        try {
            textureMosaic = new OGLTexture2D("./textures/mosaic.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void display() {
        glUseProgram(shaderProgram);

        // znovu zapnout z-test (kvůli textRenderer)
        glEnable(GL_DEPTH_TEST);

        // nutno opravit viewport (kvůli textRenderer)
        glViewport(0, 0, width, height);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glUniformMatrix4fv(locView, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(locProjection, false, projection.floatArray());

        // vykreslit první těleso
        glUniform1i(locType, 1);
        buffers.draw(GL_TRIANGLES, shaderProgram);

        // vykreslit druhé těleso (do stejné scény)
        glUniform1i(locType, 2);
        buffers.draw(GL_TRIANGLES, shaderProgram);

        textureViewer.view(textureMosaic, -1, -1, 0.5);
        textRenderer.addStr2D(width - 90, height - 3, " (c) PGRF UHK");
    }

}
