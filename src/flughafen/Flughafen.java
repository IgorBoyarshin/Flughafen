package flughafen;

import himmel.graphics.Shader;
import himmel.graphics.Window;
import himmel.graphics.camera.Camera;
import himmel.graphics.camera.Direction;
import himmel.graphics.camera.SimpleCamera;
import himmel.graphics.layers.Layer;
import himmel.graphics.renderables.Cube;
import himmel.graphics.renderables.Sprite;
import himmel.graphics.renderers.FastRenderer;
import himmel.graphics.renderers.Renderer3D;
import himmel.graphics.renderers.RenderingSet;
import himmel.graphics.renderers.TerrainRenderer;
import himmel.math.Matrix4f;
import himmel.math.Vector2f;
import himmel.math.Vector3f;
import himmel.math.Vector4f;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;

/**
 * Created by Igor on 15-Aug-15.
 */
public class Flughafen {
    private Window window;
    private Settings settings;

    private RenderingSet spriteRenderingSet;
    private RenderingSet terrainRenderingSet;
    private RenderingSet object3dRenderingSet;

    private LoadingScreen loadingScreen;
    private Loader loader;
    private Terrain terrain;
    private Layer mainLayer;

    private Camera camera;

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

        loader = new Loader();
        loader.launch();

        camera = new SimpleCamera(new Vector3f(0.0f, 2.0f, 0.0f));

        mainLayer = new Layer();
        Cube cube = new Cube(new Vector3f(0.0f, 0.0f, -5.0f), new Vector3f(2.0f, 2.0f, 2.0f),
                new Vector4f(0.8f, 0.5f, 0.1f, 1.0f), object3dRenderingSet);
        mainLayer.add(cube);

        terrain = new Terrain(terrainRenderingSet);
//        window.setWireframe(true);

        System.out.println("Init took " + (System.currentTimeMillis() - initStartTime) + "ms");
    }

    private void update(float delta) {
//        if (!loadingScreen.isFinished()) {
        if (false) {
            loadingScreen.update(delta);
        } else {
            if (loader.isFinished()) {
                // Everything is loaded, start the game

                keyboard(delta);

                mainLayer.update();
            }
        }
    }

    private void render() {
        window.clear();

//        if (!loadingScreen.isFinished()) {
        if (false) {
            loadingScreen.render();
        } else {
            if (loader.isFinished()) {
                // Everything is loaded, start the game

                mainLayer.render();
                terrain.render();
            }
        }


        window.swapBuffers();
    }

    private void keyboard(float delta) {
        terrainRenderingSet.getShader().enable();
        terrainRenderingSet.getShader().setUniformMat4f("vw_matrix", camera.getViewMatrix());
        object3dRenderingSet.getShader().enable();
        object3dRenderingSet.getShader().setUniformMat4f("vw_matrix", camera.getViewMatrix());

        Vector2f mousePos = window.getMousePos();
        final float speed = 10.0f;
        final float rotationSpeed = 0.18f;
        if (window.isKeyPressed(GLFW_KEY_W)) {
            camera.move(Direction.FORWARD, speed * delta);
        }
        if (window.isKeyPressed(GLFW_KEY_S)) {
            camera.move(Direction.FORWARD, -speed * delta);
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            camera.move(Direction.RIGHT, speed * delta);
        }
        if (window.isKeyPressed(GLFW_KEY_D)) {
            camera.move(Direction.RIGHT, -speed * delta);
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            camera.move(Direction.UP, speed * delta);
        }
        if (window.isKeyPressed(GLFW_KEY_X)) {
            camera.move(Direction.UP, -speed * delta);
        }

        if (window.isKeyPressed(GLFW_KEY_C)) {
            camera.printPosition();
        }

//        camera.setPosition(new Vector3f(camera.getPosition().x, 2.0f, camera.getPosition().z));
        camera.setYaw(camera.getYaw() - rotationSpeed * (mousePos.x - window.getWidth() / 2.0f));
        camera.setPitch(camera.getPitch() - rotationSpeed * (mousePos.y - window.getHeight() / 2.0f));
        window.resetMousePos(window.getWidth() / 2.0f, window.getHeight() / 2.0f);
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

                render();

                fpsCounter++;
            } else if (rendersTimeCounter >= timePerRender) {
                rendersTimeCounter -= timePerRender;

                render();

                fpsCounter++;
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

    private void initShaders() {
        int values[] = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        IntBuffer textureIDs = BufferUtils.createIntBuffer(values.length);
        textureIDs.put(values);
        textureIDs.rewind();

        spriteRenderingSet.getShader().enable();
        spriteRenderingSet.getShader().setUniform1iv("textures", textureIDs);
        spriteRenderingSet.getShader().setUniformMat4f("pr_matrix",
                Matrix4f.orthographic(0.0f, window.getWidth(), 0.0f, window.getHeight(), -1.0f, 1.0f));

        terrainRenderingSet.getShader().enable();
        terrainRenderingSet.getShader().setUniform1iv("textures", textureIDs);
        terrainRenderingSet.getShader().setUniformMat4f("pr_matrix",
                Matrix4f.perspective(window.getWidth(), window.getHeight(), 67.0f, 0.01f, 500.0f));

        object3dRenderingSet.getShader().enable();
        object3dRenderingSet.getShader().setUniform1iv("textures", textureIDs);
        object3dRenderingSet.getShader().setUniformMat4f("pr_matrix",
                Matrix4f.perspective(window.getWidth(), window.getHeight(), 67.0f, 0.01f, 500.0f));
    }

    private void initRenderingSets() {
        spriteRenderingSet = new RenderingSet(
                FastRenderer::new,
                new Shader(System.getProperty("user.dir") + "//res//shaders//sprite.vert",
                        System.getProperty("user.dir") + "//res//shaders//sprite.frag"));
        terrainRenderingSet = new RenderingSet(
                TerrainRenderer::new,
                new Shader(System.getProperty("user.dir") + "//res//shaders//terrain.vert",
                        System.getProperty("user.dir") + "//res//shaders//terrain.frag"));
        object3dRenderingSet = new RenderingSet(
                Renderer3D::new,
                new Shader(System.getProperty("user.dir") + "//res//shaders//object3d.vert",
                        System.getProperty("user.dir") + "//res//shaders//object3d.frag"));

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
