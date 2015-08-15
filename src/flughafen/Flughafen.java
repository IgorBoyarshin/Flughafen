package flughafen;

import himmel.graphics.Shader;
import himmel.graphics.Window;
import himmel.graphics.renderables.Sprite;
import himmel.graphics.renderers.FastRenderer;
import himmel.graphics.renderers.RenderingSet;
import himmel.math.Matrix4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

/**
 * Created by Igor on 15-Aug-15.
 */
public class Flughafen {
    private Window window;
    private Settings settings;

    private RenderingSet spriteRenderingSet;

    private LoadingScreen loadingScreen;

    public Flughafen() {
        init();
        mainLoop();
        terminate();
    }

    private void init() {
        long initStartTime = System.currentTimeMillis();

        System.setProperty("org.lwjgl.librarypath", "res//natives//windows//x64");

        try {
            settings = new Settings(System.getProperty("user.dir") + "//res//settings.fhs");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println();
            System.err.println("ERR: COULD NOT LOAD SETTINGS");
            terminate();
        }

        window = new Window(settings.getWindowTitle(), settings.getWindowWidth(), settings.getWindowHeight(),
                settings.getWindowAntiAliasing(), settings.getWindowVsync(), settings.getWindowFullscreen(),
                settings.getWindowWireframe(), settings.getWindowLog());
        window.resetMousePos(settings.getWindowWidth() / 2.0f, settings.getWindowHeight() / 2.0f);

        initRenderingSets();

        loadingScreen = new LoadingScreen(window.getWidth(), window.getHeight());

        System.out.println("Init took " + (System.currentTimeMillis() - initStartTime) + "ms");
    }

    private void mainLoop() {
        double lastTime = window.getTimeSinceLaunch();
        final int rendersPerSecond = settings.getRps();
        final int updatesPerSecond = settings.getUps();
        final float timePerRender = 1.0f / rendersPerSecond;
        final float timePerUpdate = 1.0f / updatesPerSecond;
        double rendersTimeCounter = 0.0d;
        double updatesTimeCounter = 0.0d;
        double secondsTimeCounter = 0.0d;
        int upsCounter = 0;
        int fpsCounter = 0;

        while (!window.isClosed() && !window.isKeyPressed(GLFW_KEY_ESCAPE)) {
            double currentTime = window.getTimeSinceLaunch();
            double delta = currentTime - lastTime;
            lastTime = currentTime;

            rendersTimeCounter += delta;
            updatesTimeCounter += delta;
            secondsTimeCounter += delta;

            // Update
            if (updatesPerSecond == -1) {
                updatesTimeCounter = 0;
                float updateArgument = (float) (delta * 1000000.0d);
                update(updateArgument / 1000000.0f);
                upsCounter++;
            } else if (updatesTimeCounter >= timePerUpdate) {
                update(timePerUpdate);
                updatesTimeCounter -= timePerUpdate;
                upsCounter++;
            }

            // Render
            if (rendersPerSecond == -1) {
                rendersTimeCounter = 0;
                window.clear();

                render();

                fpsCounter++;
                window.swapBuffers();
            } else if (rendersTimeCounter >= timePerRender) {
                rendersTimeCounter -= timePerRender;
                window.clear();

                render();

                fpsCounter++;
                window.swapBuffers();
            }

            // FPS and UPS counter
            if (secondsTimeCounter >= 1.0f) {
                secondsTimeCounter -= 1.0f;
                System.out.println("UPS: " + upsCounter + " | FPS: " + fpsCounter + " | Delta: " + (delta * 1000000.0d) + "mks");
                upsCounter = 0;
                fpsCounter = 0;
            }

            window.pollEvents();
        }
    }

    private void update(float delta) {
        loadingScreen.update(delta);
//        if (loadingScreen.isFinished()) {
//            System.out.println("dgf");
//            terminate();
//            System.exit(0);
//        }
    }

    private void render() {
        loadingScreen.render();
    }

    private void initShaders() {
        int values[] = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        IntBuffer textureIDs = BufferUtils.createIntBuffer(values.length);
        textureIDs.put(values);
        textureIDs.rewind();

        spriteRenderingSet.getShader().enable();
        spriteRenderingSet.getShader().setUniform1iv("textures", textureIDs);
        spriteRenderingSet.getShader().setUniformMat4f("pr_matrix",
                Matrix4f.orthographic(0.0f, window.getWidth(), 0.0f, window.getHeight(), -1.0f, 1.0f));
    }

    private void initRenderingSets() {
        spriteRenderingSet = new RenderingSet(
                FastRenderer::new,
                new Shader(System.getProperty("user.dir") + "//res//shaders//sprite.vert",
                        System.getProperty("user.dir") + "//res//shaders//sprite.frag"));

        initShaders();

        Sprite.setRenderingSet(spriteRenderingSet);
    }

    private void terminate() {
        if (window != null) {
            window.terminate();
        }
    }

    public static void main(String[] args) {
        new Flughafen();
    }
}
