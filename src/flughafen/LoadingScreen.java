package flughafen;

import himmel.graphics.Texture;
import himmel.graphics.layers.Layer;
import himmel.graphics.renderables.Sprite;
import himmel.math.Vector2f;
import himmel.math.Vector3f;
import himmel.math.Vector4f;

/**
 * Created by Igor on 15-Aug-15.
 */
public class LoadingScreen {
    private Layer mainLayer;
    private boolean finished;
    private double elapsedTime;

    private final float width;
    private final float height;

    private Sprite background;
    private Sprite image;
    private Sprite layer;

    // In ms

    private final double delay = 500;
    private final double fading = 1200;
    private final double visialble = 1500;

    public LoadingScreen(final float width, final float height) {
        this.width = width;
        this.height = height;

        finished = false;
        mainLayer = new Layer();
        this.elapsedTime = 0;

        init();
    }

    private void init() {
        background = new Sprite(
                new Vector3f(0.0f, 0.0f, -0.5f), new Vector2f(width, height),
                new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));

        final float imageWidth = 1180.0f;
        final float imageHeight = 340.0f;
        final float imageSpriteWidth = 0.8f * width;
        final float imageSpriteHeight = imageHeight * imageSpriteWidth / imageWidth;
        image = new Sprite(
                new Vector3f((width - imageSpriteWidth) / 2.0f, (height - imageSpriteHeight) / 2.0f, 0.0f),
                new Vector2f(imageSpriteWidth, imageSpriteHeight),
                new Texture(System.getProperty("user.dir") + "//res//Himmel.png", Texture.TYPE_RGB,
                        Texture.FILTER_LINEAR, Texture.FILTER_LINEAR),
                Sprite.getDefaultUvs());

        layer = new Sprite(
                new Vector3f(0.0f, 0.0f, 0.5f), new Vector2f(width, height),
                new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));

        mainLayer.add(background);
        mainLayer.add(image);
        mainLayer.add(layer);
    }

    public void update(float delta) {
        if (finished) {
            return;
        }

        elapsedTime += delta;

        double elapsedTimeMillis = toMillis(elapsedTime);
        if (elapsedTimeMillis >= delay) {
            if (elapsedTimeMillis <= delay + fading) {
                layer.setColor(new Vector4f(0.0f, 0.0f, 0.0f,
                        (float) ((delay + fading - elapsedTimeMillis) / (fading))));
            } else if (elapsedTimeMillis <= delay + fading + visialble) {
                layer.setColor(new Vector4f(0.0f, 0.0f, 0.0f, 0.0f));
            } else if (elapsedTimeMillis <= delay + fading + visialble + fading) {
                layer.setColor(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f -
                        (float) ((delay + fading + visialble + fading - elapsedTimeMillis) / (fading))));
            } else if (elapsedTimeMillis <= delay + fading + visialble + fading + delay) {
                layer.setColor(new Vector4f(0.0f, 0.0f, 0.0f, 1.0f));
            } else {
                finished = true;
                System.out.println("finished");
            }
        }

        mainLayer.update();
    }

    public void render() {
        if (!finished) {
            mainLayer.render();
        }
    }

    public boolean isFinished() {
        return finished;
    }

    private double toMillis(double time) {
        return time * 1000.0f;
    }
}
