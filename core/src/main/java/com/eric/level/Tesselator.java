package com.eric.level;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Tesselator {
    public static final int MAX_VERTICES = 100000;
    private static final int FLOATS_PER_VERTEX = 9; // x,y,z, u,v, r,g,b,a

    private final float[] vertices = new float[MAX_VERTICES * FLOATS_PER_VERTEX];
    private int vertexCount = 0;

    private float u, v;
    private float r = 1f, g = 1f, b = 1f, a = 1f;
    private boolean hasColor = false;
    private boolean hasTexture = false;

    public void init() {
        vertexCount = 0;
        hasColor = false;
        hasTexture = false;
        r = g = b = a = 1f;
        u = v = 0f;
    }

    public void tex(float u, float v) {
        this.hasTexture = true;
        this.u = u;
        this.v = v;
    }

    public void color(float r, float g, float b) {
        this.hasColor = true;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void setAlpha(float a) {
        this.a = a;
    }

    public void vertex(float x, float y, float z) {
        int o = vertexCount * FLOATS_PER_VERTEX;
        vertices[o + 0] = x;
        vertices[o + 1] = y;
        vertices[o + 2] = z;
        vertices[o + 3] = u;
        vertices[o + 4] = v;
        vertices[o + 5] = r;
        vertices[o + 6] = g;
        vertices[o + 7] = b;
        vertices[o + 8] = a;
        vertexCount++;
    }

    public int getVertexCount() { return vertexCount; }

    /** Build a fresh Mesh from accumulated vertices, or null if empty. */
    public Mesh buildMesh(Mesh existing) {
        if (existing != null) existing.dispose();
        if (vertexCount == 0) return null;

        Mesh mesh = new Mesh(
            false,
            vertexCount,
            0,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"),
            new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, ShaderProgram.COLOR_ATTRIBUTE)
        );
        mesh.setVertices(vertices, 0, vertexCount * FLOATS_PER_VERTEX);
        return mesh;
    }
}
