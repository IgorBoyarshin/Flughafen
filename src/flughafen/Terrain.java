package flughafen;

import himmel.graphics.Texture;
import himmel.graphics.layers.Layer;
import himmel.graphics.renderables.Renderable;
import himmel.graphics.renderers.RenderingSet;

/**
 * Created by Igor on 16-Aug-15.
 */
public class Terrain {
    private Layer terrainLayer;
    private RenderingSet renderingSet;
    private Renderable terrain; // Will be an array later
    private final int tileVerticesLength = 8;
    private final float shiftSize = 1.0f;

    public Terrain(RenderingSet renderingSet) {
        this.renderingSet = renderingSet;
        terrainLayer = new Layer();

        initTextures();
        initRenderable();
//        initRenderingSet();
        terrainLayer.update();
    }

    public void render() {
        terrainLayer.render();
    }

    private void initRenderable() {
        terrain = new Renderable(initVertices(), initNormals(), null, initUv(), initIndices(), renderingSet);
        terrainLayer.add(terrain);
    }

    private void initTextures() {
        terrainLayer.add(new Renderable(null,
                new Texture(System.getProperty("user.dir") + "//res//textures//grass-small.png", Texture.TYPE_RGB),
                null, null, renderingSet));
    }

    private float[] initVertices() {
        float[] vertices = new float[3 * tileVerticesLength * tileVerticesLength];
        for (int i = 0; i < vertices.length; i += 3) {
            vertices[i] = (i / 3) / tileVerticesLength * shiftSize;
            vertices[i + 1] = 0.0f;
            vertices[i + 2] = ((i / 3) % tileVerticesLength) * shiftSize;
        }
        return vertices;
    }

    private float[] initNormals() {
        float[] normals = new float[3 * tileVerticesLength * tileVerticesLength];
        for (int i = 0; i < normals.length; i += 3) {
            normals[i] = 0.0f;
            normals[i + 1] = 1.0f;
            normals[i + 2] = 0.0f;
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
