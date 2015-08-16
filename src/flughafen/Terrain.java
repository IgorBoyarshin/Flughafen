package flughafen;

import himmel.graphics.Texture;
import himmel.graphics.layers.Layer;
import himmel.graphics.renderables.Renderable;
import himmel.graphics.renderers.RenderingSet;
import himmel.math.Matrix4f;
import himmel.math.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Igor on 16-Aug-15.
 */
public class Terrain {
    private Layer terrainLayer;
    private RenderingSet renderingSet;
    private Renderable terrain; // Will be an array later
    private final int tileVerticesLength = 64;
    private final float shiftSize = 2.0f;
    private final Matrix4f terrainStaticMatrix;

    private final float minHeight = -5.0f;
    private final float maxHeight = 5.0f;

    public Terrain(RenderingSet renderingSet) {
        this.renderingSet = renderingSet;
        terrainLayer = new Layer();
        terrainStaticMatrix = new Matrix4f(1.0f).translate(
                new Vector3f(-tileVerticesLength / 2.0f * shiftSize, 0.0f, -tileVerticesLength / 2.0f * shiftSize));

        initTextures();
        initRenderable();
//        initRenderingSet();
        terrainLayer.update();
    }

    public void render() {
        terrainLayer.render();
    }

    private void initRenderable() {
        float[] vertices = initVertices();
        terrain = new Renderable(vertices, initNormals(vertices), null, initUv(), initIndices(), renderingSet);
        terrainLayer.add(terrain);
    }

    private void initTextures() {
        terrainLayer.add(new Renderable(null,
                new Texture(System.getProperty("user.dir") + "//res//textures//grass-small.png", Texture.TYPE_RGB),
                null, null, renderingSet));
//        terrainLayer.add(new Renderable(null,
//                new Texture(System.getProperty("user.dir") + "//res//textures//heightmap.png", Texture.TYPE_RGB),
//                null, null, renderingSet));
    }

    private float[] initVertices() {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(System.getProperty("user.dir") + "//res//textures//heightmap-small.png"));
//            byte[] data = ((DataBufferByte) i.getRaster().getDataBuffer()).getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final int maxColor = 256 * 256 * 256;
        final int imageWidth = image.getWidth();
        final int imageHeight = image.getHeight();

        float[] vertices = new float[3 * tileVerticesLength * tileVerticesLength];
        for (int i = 0; i < vertices.length; i += 3) {
            int xx = (i / 3) / tileVerticesLength;
            int yy = (i / 3) % tileVerticesLength;
            float y = -image.getRGB(xx, yy);
            y /= maxColor;
            y *= (maxHeight - minHeight);
            y -= -minHeight;

            Vector3f vertex = new Vector3f((i / 3) / tileVerticesLength * shiftSize, y, ((i / 3) % tileVerticesLength) * shiftSize);
            vertex = terrainStaticMatrix.multiply(vertex);

            vertices[i] = vertex.x;
            vertices[i + 1] = vertex.y;
            vertices[i + 2] = vertex.z;
        }
        return vertices;
    }

    private float[] initNormals(float vertices[]) {
        float[] normals = new float[3 * tileVerticesLength * tileVerticesLength];
        for (int i = 0; i < normals.length; i += 3) {
            int xx = (i / 3) / tileVerticesLength;
            int zz = (i / 3) % tileVerticesLength;
            Vector3f normal;
            if (xx == 0 || zz == 0 || xx == tileVerticesLength - 1 || zz == tileVerticesLength - 1) {
                normal = new Vector3f(0.0f, 1.0f, 0.0f);
            } else {
                normal = new Vector3f(
                        vertices[((xx - 1) * tileVerticesLength + zz) * 3 + 1] - vertices[((xx + 1) * tileVerticesLength + zz) * 3 + 1],
                        2.0f,
                        vertices[(xx * tileVerticesLength + zz + 1) * 3 + 1] - vertices[(xx * tileVerticesLength + zz - 1) * 3 + 1]);
            }
            float vectorLength = (float) Math.sqrt(normal.x * normal.x + normal.y * normal.y + normal.z * normal.z);
            normal.x /= vectorLength;
            normal.y /= vectorLength;
            normal.z /= vectorLength;

            normals[i] = normal.x;
            normals[i + 1] = normal.y;
            normals[i + 2] = normal.z;
        }
        return normals;
    }

    private float[] initUv() {
        float[] uvs = new float[2 * tileVerticesLength * tileVerticesLength];
        for (int i = 0; i < uvs.length; i += 2) {
            uvs[i] = (i / 2) / tileVerticesLength / (1.0f * (tileVerticesLength - 1));
            uvs[i + 1] = ((i / 2) - (tileVerticesLength) * ((i / 2) / tileVerticesLength)) / (1.0f * (tileVerticesLength - 1));
        }
        return uvs;
    }

    private short[] initIndices() {
        short[] indices = new short[6 * (tileVerticesLength - 1) * (tileVerticesLength - 1)];
        short counter = 0;
        for (int x = 0; x < tileVerticesLength - 1; x++) {
            for (int y = 0; y < tileVerticesLength - 1; y++) {
                indices[counter] = (short) (x * tileVerticesLength + y);
                indices[counter + 1] = (short) (x * tileVerticesLength + y + 1);
                indices[counter + 2] = (short) ((x + 1) * tileVerticesLength + y + 1);
                indices[counter + 3] = (short) (x * tileVerticesLength + y);
                indices[counter + 4] = (short) ((x + 1) * tileVerticesLength + y + 1);
                indices[counter + 5] = (short) ((x + 1) * tileVerticesLength + y);

                counter += 6;
            }
        }
        return indices;
    }

//    private void initRenderingSet() {
//
//    }
}
