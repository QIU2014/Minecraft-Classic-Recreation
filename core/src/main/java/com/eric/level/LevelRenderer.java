package com.eric.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.eric.HitResult;
import com.eric.Player;

public class LevelRenderer implements LevelListener {
    private static final int CHUNK_SIZE = 16;
    private final Level level;
    private final Chunk[] chunks;
    private final int xChunks, yChunks, zChunks;
    private final Tesselator t = new Tesselator();

    public LevelRenderer(Level level) {
        this.level = level;
        level.addListener(this);
        this.xChunks = level.width / CHUNK_SIZE;
        this.yChunks = level.depth / CHUNK_SIZE;
        this.zChunks = level.height / CHUNK_SIZE;
        this.chunks = new Chunk[xChunks * yChunks * zChunks];

        for (int x = 0; x < xChunks; x++)
            for (int y = 0; y < yChunks; y++)
                for (int z = 0; z < zChunks; z++) {
                    int x0 = x * 16, y0 = y * 16, z0 = z * 16;
                    int x1 = Math.min((x + 1) * 16, level.width);
                    int y1 = Math.min((y + 1) * 16, level.depth);
                    int z1 = Math.min((z + 1) * 16, level.height);
                    chunks[idx(x, y, z)] = new Chunk(level, x0, y0, z0, x1, y1, z1);
                }
    }

    private int idx(int x, int y, int z) {
        return (x + y * xChunks) * zChunks + z;
    }

    public void render(Player player, int layer, ShaderProgram shader) {
//        Chunk.rebuiltThisFrame = 0;
        Frustum frustum = Frustum.getFrustum(player.camera.projection, player.camera.view);

        for (Chunk chunk : chunks) {
            if (chunk != null && frustum.cubeInFrustum(chunk.aabb)) {
                chunk.render(layer, shader);
            }
        }
    }

    public HitResult pick(Player player) {
        final float reach = 3.0f;

        float ox = player.x;
        float oy = player.y;
        float oz = player.z;

        float dx = player.getLookX();
        float dy = player.getLookY();
        float dz = player.getLookZ();
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 1e-6f) return null;
        dx /= len;
        dy /= len;
        dz /= len;

        int x = (int) Math.floor(ox);
        int y = (int) Math.floor(oy);
        int z = (int) Math.floor(oz);

        int stepX = dx > 0 ? 1 : -1;
        int stepY = dy > 0 ? 1 : -1;
        int stepZ = dz > 0 ? 1 : -1;

        float tDeltaX = dx == 0 ? Float.MAX_VALUE : Math.abs(1f / dx);
        float tDeltaY = dy == 0 ? Float.MAX_VALUE : Math.abs(1f / dy);
        float tDeltaZ = dz == 0 ? Float.MAX_VALUE : Math.abs(1f / dz);

        float tMaxX = dx == 0 ? Float.MAX_VALUE
            : ((dx > 0 ? (x + 1 - ox) : (ox - x))) * tDeltaX;
        float tMaxY = dy == 0 ? Float.MAX_VALUE
            : ((dy > 0 ? (y + 1 - oy) : (oy - y))) * tDeltaY;
        float tMaxZ = dz == 0 ? Float.MAX_VALUE
            : ((dz > 0 ? (z + 1 - oz) : (oz - z))) * tDeltaZ;

        int face = -1;

        for (int steps = 0; steps < 256; steps++) {
            if (level.isSolidTile(x, y, z)) {
                // If origin was already inside a solid tile, default to top face.
                if (face == -1) face = 1;
                return new HitResult(x, y, z, 0, face);
            }

            if (tMaxX < tMaxY && tMaxX < tMaxZ) {
                if (tMaxX > reach) break;
                x += stepX;
                tMaxX += tDeltaX;
                // Moving +X → entered the -X face (index 4)
                // Moving -X → entered the +X face (index 5)
                face = stepX > 0 ? 4 : 5;
            } else if (tMaxY < tMaxZ) {
                if (tMaxY > reach) break;
                y += stepY;
                tMaxY += tDeltaY;
                // Moving +Y → entered the -Y face (index 0)
                // Moving -Y → entered the +Y face (index 1)
                face = stepY > 0 ? 0 : 1;
            } else {
                if (tMaxZ > reach) break;
                z += stepZ;
                tMaxZ += tDeltaZ;
                // Moving +Z → entered the -Z face (index 2)
                // Moving -Z → entered the +Z face (index 3)
                face = stepZ > 0 ? 2 : 3;
            }
        }
        return null;
    }

    public void renderHit(HitResult h, ShaderProgram shader) {
        if (h == null) return;
        float alpha = (float) Math.sin(System.currentTimeMillis() / 100.0) * 0.2f + 0.4f;

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        t.init();
        t.color(1f, 1f, 1f);
        t.setAlpha(alpha);
        Tile.rock.renderFace(t, h.x, h.y, h.z, h.f);

        Mesh m = t.buildMesh(null);
        if (m != null) {
            m.render(shader, GL20.GL_TRIANGLES);
            m.dispose();
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void setDirty(int x0, int y0, int z0, int x1, int y1, int z1) {
        x0 /= 16; x1 /= 16;
        y0 /= 16; y1 /= 16;
        z0 /= 16; z1 /= 16;
        x0 = Math.max(x0, 0); y0 = Math.max(y0, 0); z0 = Math.max(z0, 0);
        x1 = Math.min(x1, xChunks - 1); y1 = Math.min(y1, yChunks - 1); z1 = Math.min(z1, zChunks - 1);
        for (int x = x0; x <= x1; x++)
            for (int y = y0; y <= y1; y++)
                for (int z = z0; z <= z1; z++)
                    chunks[idx(x, y, z)].setDirty();
    }

    @Override public void tileChanged(int x, int y, int z) {
        setDirty(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
    }
    @Override public void lightColumnChanged(int x, int z, int y0, int y1) {
        setDirty(x - 1, y0 - 1, z - 1, x + 1, y1 + 1, z + 1);
    }
    @Override public void allChanged() {
        setDirty(0, 0, 0, level.width, level.depth, level.height);
    }

    public void dispose() {
        for (Chunk c : chunks) if (c != null) c.dispose();
    }
}
