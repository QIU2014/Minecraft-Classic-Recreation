package com.eric.level;

public class Tile {
    public static Tile rock  = new Tile(0);
    public static Tile grass = new Tile(1);
    private int tex = 0;

    private Tile(int tex) {
        this.tex = tex;
    }

    public void render(Tesselator t, Level level, int layer, int x, int y, int z) {
        float u0 = this.tex / 16.0f;
        float u1 = u0 + 0.0624375f;
        float v0 = 0.0f;
        float v1 = v0 + 0.0624375f;

        float c1 = 1.0f;
        float c2 = 0.8f;
        float c3 = 0.6f;

        float x0 = x + 0.0f, x1 = x + 1.0f;
        float y0 = y + 0.0f, y1 = y + 1.0f;
        float z0 = z + 0.0f, z1 = z + 1.0f;

        // -Y (bottom)
        if (!level.isSolidTile(x, y - 1, z)) {
            float br = level.getBrightness(x, y - 1, z) * c1;
            if ((br == c1) ^ (layer == 1)) {
                t.color(br, br, br);
                // Quad: (x0,y0,z1) (x0,y0,z0) (x1,y0,z0) (x1,y0,z1)
                t.tex(u0, v1); t.vertex(x0, y0, z1);
                t.tex(u0, v0); t.vertex(x0, y0, z0);
                t.tex(u1, v0); t.vertex(x1, y0, z0);

                t.tex(u0, v1); t.vertex(x0, y0, z1);
                t.tex(u1, v0); t.vertex(x1, y0, z0);
                t.tex(u1, v1); t.vertex(x1, y0, z1);
            }
        }

        // +Y (top)
        if (!level.isSolidTile(x, y + 1, z)) {
            float br = level.getBrightness(x, y, z) * c1;
            if ((br == c1) ^ (layer == 1)) {
                t.color(br, br, br);
                // Quad: (x1,y1,z1) (x1,y1,z0) (x0,y1,z0) (x0,y1,z1)
                t.tex(u1, v1); t.vertex(x1, y1, z1);
                t.tex(u1, v0); t.vertex(x1, y1, z0);
                t.tex(u0, v0); t.vertex(x0, y1, z0);

                t.tex(u1, v1); t.vertex(x1, y1, z1);
                t.tex(u0, v0); t.vertex(x0, y1, z0);
                t.tex(u0, v1); t.vertex(x0, y1, z1);
            }
        }

        // -Z
        if (!level.isSolidTile(x, y, z - 1)) {
            float br = level.getBrightness(x, y, z - 1) * c2;
            if ((br == c2) ^ (layer == 1)) {
                t.color(br, br, br);
                // Quad: (x0,y1,z0) (x1,y1,z0) (x1,y0,z0) (x0,y0,z0)
                t.tex(u1, v0); t.vertex(x0, y1, z0);
                t.tex(u0, v0); t.vertex(x1, y1, z0);
                t.tex(u0, v1); t.vertex(x1, y0, z0);

                t.tex(u1, v0); t.vertex(x0, y1, z0);
                t.tex(u0, v1); t.vertex(x1, y0, z0);
                t.tex(u1, v1); t.vertex(x0, y0, z0);
            }
        }

        // +Z
        if (!level.isSolidTile(x, y, z + 1)) {
            float br = level.getBrightness(x, y, z + 1) * c2;
            if ((br == c2) ^ (layer == 1)) {
                t.color(br, br, br);
                // Quad: (x0,y1,z1) (x0,y0,z1) (x1,y0,z1) (x1,y1,z1)
                t.tex(u0, v0); t.vertex(x0, y1, z1);
                t.tex(u0, v1); t.vertex(x0, y0, z1);
                t.tex(u1, v1); t.vertex(x1, y0, z1);

                t.tex(u0, v0); t.vertex(x0, y1, z1);
                t.tex(u1, v1); t.vertex(x1, y0, z1);
                t.tex(u1, v0); t.vertex(x1, y1, z1);
            }
        }

        // -X
        if (!level.isSolidTile(x - 1, y, z)) {
            float br = level.getBrightness(x - 1, y, z) * c3;
            if ((br == c3) ^ (layer == 1)) {
                t.color(br, br, br);
                // Quad: (x0,y1,z1) (x0,y1,z0) (x0,y0,z0) (x0,y0,z1)
                t.tex(u1, v0); t.vertex(x0, y1, z1);
                t.tex(u0, v0); t.vertex(x0, y1, z0);
                t.tex(u0, v1); t.vertex(x0, y0, z0);

                t.tex(u1, v0); t.vertex(x0, y1, z1);
                t.tex(u0, v1); t.vertex(x0, y0, z0);
                t.tex(u1, v1); t.vertex(x0, y0, z1);
            }
        }

        // +X
        if (!level.isSolidTile(x + 1, y, z)) {
            float br = level.getBrightness(x + 1, y, z) * c3;
            if ((br == c3) ^ (layer == 1)) {
                t.color(br, br, br);
                // Quad: (x1,y0,z1) (x1,y0,z0) (x1,y1,z0) (x1,y1,z1)
                t.tex(u0, v1); t.vertex(x1, y0, z1);
                t.tex(u1, v1); t.vertex(x1, y0, z0);
                t.tex(u1, v0); t.vertex(x1, y1, z0);

                t.tex(u0, v1); t.vertex(x1, y0, z1);
                t.tex(u1, v0); t.vertex(x1, y1, z0);
                t.tex(u0, v0); t.vertex(x1, y1, z1);
            }
        }
    }

    /**
     * Single face, used by the hit overlay. Emits two triangles.
     * face: 0=-Y, 1=+Y, 2=-Z, 3=+Z, 4=-X, 5=+X
     */
    public void renderFace(Tesselator t, int x, int y, int z, int face) {
        float x0 = x + 0.0f, x1 = x + 1.0f;
        float y0 = y + 0.0f, y1 = y + 1.0f;
        float z0 = z + 0.0f, z1 = z + 1.0f;

        if (face == 0) {
            t.vertex(x0, y0, z1); t.vertex(x0, y0, z0); t.vertex(x1, y0, z0);
            t.vertex(x0, y0, z1); t.vertex(x1, y0, z0); t.vertex(x1, y0, z1);
        } else if (face == 1) {
            t.vertex(x1, y1, z1); t.vertex(x1, y1, z0); t.vertex(x0, y1, z0);
            t.vertex(x1, y1, z1); t.vertex(x0, y1, z0); t.vertex(x0, y1, z1);
        } else if (face == 2) {
            t.vertex(x0, y1, z0); t.vertex(x1, y1, z0); t.vertex(x1, y0, z0);
            t.vertex(x0, y1, z0); t.vertex(x1, y0, z0); t.vertex(x0, y0, z0);
        } else if (face == 3) {
            t.vertex(x0, y1, z1); t.vertex(x0, y0, z1); t.vertex(x1, y0, z1);
            t.vertex(x0, y1, z1); t.vertex(x1, y0, z1); t.vertex(x1, y1, z1);
        } else if (face == 4) {
            t.vertex(x0, y1, z1); t.vertex(x0, y1, z0); t.vertex(x0, y0, z0);
            t.vertex(x0, y1, z1); t.vertex(x0, y0, z0); t.vertex(x0, y0, z1);
        } else if (face == 5) {
            t.vertex(x1, y0, z1); t.vertex(x1, y0, z0); t.vertex(x1, y1, z0);
            t.vertex(x1, y0, z1); t.vertex(x1, y1, z0); t.vertex(x1, y1, z1);
        }
    }
}
