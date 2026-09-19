package com.eric.level;

import com.badlogic.gdx.Gdx;
import com.eric.phys.AABB;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Level {
    public final int width;
    public final int height;
    public final int depth;
    private byte[] blocks;
    private int[] lightDepths;
    private ArrayList<LevelListener> levelListeners = new ArrayList<>();

    public Level(int w, int h, int d) {
        this.width = w;
        this.height = h;
        this.depth = d;
        this.blocks = new byte[w * h * d];
        this.lightDepths = new int[w * h];

        for (int x = 0; x < w; x++)
            for (int y = 0; y < d; y++)
                for (int z = 0; z < h; z++) {
                    int i = (y * this.height + z) * this.width + x;
                    this.blocks[i] = (byte) (y <= d * 2 / 3 ? 1 : 0);
                }

        this.calcLightDepths(0, 0, w, h);
        this.load();
    }

    public void load() {
        File f = new File("level.dat");
        if (!f.exists()) return;   // first run — skip quietly
        try {
            DataInputStream dis = new DataInputStream(new GZIPInputStream(Gdx.files.external("level.dat").read()));
            dis.readFully(this.blocks);
            this.calcLightDepths(0, 0, this.width, this.height);
            for (LevelListener l : levelListeners) l.allChanged();
            dis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        File f = new File("level.dat");
        try {
            DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(Gdx.files.external("level.dat").write(false)));
            dos.write(this.blocks);
            dos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void calcLightDepths(int x0, int y0, int x1, int y1) {
        for (int x = x0; x < x0 + x1; x++) {
            for (int z = y0; z < y0 + y1; z++) {
                int oldDepth = this.lightDepths[x + z * this.width];
                int y = this.depth - 1;
                while (y > 0 && !this.isLightBlocker(x, y, z)) y--;
                this.lightDepths[x + z * this.width] = y;
                if (oldDepth != y) {
                    int yl0 = Math.min(oldDepth, y);
                    int yl1 = Math.max(oldDepth, y);
                    for (LevelListener l : levelListeners) l.lightColumnChanged(x, z, yl0, yl1);
                }
            }
        }
    }

    public void addListener(LevelListener l) { levelListeners.add(l); }
    public void removeListener(LevelListener l) { levelListeners.remove(l); }

    public boolean isTile(int x, int y, int z) {
        return x >= 0 && y >= 0 && z >= 0 && x < width && y < depth && z < height
            && blocks[(y * height + z) * width + x] == 1;
    }

    public boolean isSolidTile(int x, int y, int z) { return isTile(x, y, z); }
    public boolean isLightBlocker(int x, int y, int z) { return isSolidTile(x, y, z); }

    public ArrayList<AABB> getCubes(AABB aABB) {
        ArrayList<AABB> list = new ArrayList<>();
        int x0 = (int) aABB.x0, x1 = (int) (aABB.x1 + 1);
        int y0 = (int) aABB.y0, y1 = (int) (aABB.y1 + 1);
        int z0 = (int) aABB.z0, z1 = (int) (aABB.z1 + 1);
        x0 = Math.max(x0, 0); y0 = Math.max(y0, 0); z0 = Math.max(z0, 0);
        x1 = Math.min(x1, width); y1 = Math.min(y1, depth); z1 = Math.min(z1, height);

        for (int x = x0; x < x1; x++)
            for (int y = y0; y < y1; y++)
                for (int z = z0; z < z1; z++)
                    if (isSolidTile(x, y, z)) {
                        list.add(new AABB(x, y, z, x + 1, y + 1, z + 1));
                        System.out.printf("Added to list: new AABB(%s, %s, %s, %s, %s, %s%n", x, y, z, x + 1, y + 1, z + 1);
                    }
        return list;
    }

    public float getBrightness(int x, int y, int z) {
        float dark = 0.8f, light = 1.0f;
        if (x < 0 || y < 0 || z < 0 || x >= width || y >= depth || z >= height) return light;
        return y < lightDepths[x + z * width] ? dark : light;
    }

    public void setTile(int x, int y, int z, int type) {
        if (x >= 0 && y >= 0 && z >= 0 && x < width && y < depth && z < height) {
            blocks[(y * height + z) * width + x] = (byte) type;
            calcLightDepths(x, z, 1, 1);
            for (LevelListener l : levelListeners) l.tileChanged(x, y, z);
        }
    }
}
