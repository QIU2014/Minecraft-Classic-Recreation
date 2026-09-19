package com.eric.level;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.eric.Textures;
import com.eric.phys.AABB;

public class Chunk {
    public AABB aabb;
    public final Level level;
    public final int x0, y0, z0;
    public final int x1, y1, z1;

    private boolean dirty = true;
    private final Mesh[] meshes = new Mesh[2];

    private static final Texture texture = Textures.loadTexture("terrain.png");
    private static final Tesselator t = new Tesselator();

    public static int rebuiltThisFrame = 0;
    public static int updates = 0;

    public Chunk(Level level, int x0, int y0, int z0, int x1, int y1, int z1) {
        this.level = level;
        this.x0 = x0; this.y0 = y0; this.z0 = z0;
        this.x1 = x1; this.y1 = y1; this.z1 = z1;
        this.aabb = new AABB(x0, y0, z0, x1, y1, z1);
    }

    private void rebuild(int layer) {
        updates++;
//        rebuiltThisFrame++;

        t.init();
        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                for (int z = z0; z < z1; z++) {
                    if (level.isTile(x, y, z)) {
                        int tileTex = (y == level.depth * 2 / 3) ? 0 : 1;
                        if (tileTex == 0) Tile.rock.render(t, level, layer, x, y, z);
                        else              Tile.grass.render(t, level, layer, x, y, z);
                    }
                }
            }
        }

        meshes[layer] = t.buildMesh(meshes[layer]);
    }

    public void render(int layer, ShaderProgram shader) {
        if (dirty) {
            rebuild(0);
            rebuild(1);
            dirty = false;
        }
        Mesh m = meshes[layer];
        if (m != null) {
            texture.bind(0);
            m.render(shader, GL20.GL_TRIANGLES);
        }
    }

    public void setDirty() { dirty = true; }

    public void dispose() {
        for (int i = 0; i < 2; i++) {
            if (meshes[i] != null) { meshes[i].dispose(); meshes[i] = null; }
        }
    }
}
