package com.eric;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.eric.level.Level;
import com.eric.phys.AABB;

import java.util.List;

public class Player {
    private Level level;
    public float xo, yo, zo;
    public float x, y, z;
    public float xd, yd, zd;
    public float yRot, xRot;
    public AABB bb;
    public boolean onGround = false;

    public PerspectiveCamera camera = new PerspectiveCamera(
        70f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    public Player(Level level) {
        this.level = level;
        this.resetPos();
    }

    void resetPos() {
        float x = (float) Math.random() * level.width;
        float y = level.depth + 10;
        float z = (float) Math.random() * level.height;
        setPos(x, y, z);
    }

    private void setPos(float x, float y, float z) {
        this.x = x; this.y = y; this.z = z;
        float w = 0.3f, h = 0.9f;
        this.bb = new AABB(x - w, y - h, z - w, x + w, y + h, z + w);
    }

    public void turn(float xo, float yo) {
        this.yRot += xo * 0.15f;
        this.xRot -= yo * 0.15f;
        if (this.xRot < -89.9f) this.xRot = -89.9f;
        if (this.xRot >  89.9f) this.xRot =  89.9f;
    }

    public void tick() {

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        float xa = 0f, ya = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.R)) resetPos();
        if (Gdx.input.isKeyPressed(Input.Keys.UP)    || Gdx.input.isKeyPressed(Input.Keys.W)) ya--;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)  || Gdx.input.isKeyPressed(Input.Keys.S)) ya++;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)  || Gdx.input.isKeyPressed(Input.Keys.A)) xa--;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) xa++;
        if ((Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) && onGround)
            this.yd = 0.12f;

        moveRelative(xa, ya, onGround ? 0.02f : 0.005f);
        this.yd -= 0.005f;
        move(xd, yd, zd);
        this.xd *= 0.91f;
        this.yd *= 0.98f;
        this.zd *= 0.91f;
        if (onGround) { this.xd *= 0.8f; this.zd *= 0.8f; }
    }

    public void move(float xa, float ya, float za) {
        float xaOrg = xa, yaOrg = ya, zaOrg = za;
        List<AABB> aABBs = level.getCubes(bb.expand(xa, ya, za));

        for (AABB c : aABBs) ya = c.clipYCollide(bb, ya);
        bb.move(0, ya, 0);
        for (AABB c : aABBs) xa = c.clipXCollide(bb, xa);
        bb.move(xa, 0, 0);
        for (AABB c : aABBs) za = c.clipZCollide(bb, za);
        bb.move(0, 0, za);

        onGround = yaOrg != ya && yaOrg < 0f;
        if (xaOrg != xa) xd = 0f;
        if (yaOrg != ya) yd = 0f;
        if (zaOrg != za) zd = 0f;

        this.x = (bb.x0 + bb.x1) / 2f;
        this.y = bb.y0 + 1.62f;
        this.z = (bb.z0 + bb.z1) / 2f;
    }

    public void moveRelative(float xa, float za, float speed) {
        float dist = xa * xa + za * za;
        if (dist < 0.01f) return;
        dist = speed / (float) Math.sqrt(dist);
        xa *= dist;
        za *= dist;

        float sin = (float) Math.sin(Math.toRadians(yRot));
        float cos = (float) Math.cos(Math.toRadians(yRot));

        this.xd += xa * cos + za * sin;
        this.zd += za * cos - xa * sin;
    }

    public void updateCamera(float a) {
        // Interpolate between previous and current tick positions.
        float ix = xo + (x - xo) * a;
        float iy = yo + (y - yo) * a;
        float iz = zo + (z - zo) * a;

        camera.position.set(ix, iy, iz);

        float cosPitch = (float) Math.cos(Math.toRadians(xRot));
        float sinPitch = (float) Math.sin(Math.toRadians(xRot));
        float cosYaw   = (float) Math.cos(Math.toRadians(yRot));
        float sinYaw   = (float) Math.sin(Math.toRadians(yRot));

        camera.direction.set(-sinYaw * cosPitch, -sinPitch, -cosYaw * cosPitch).nor();
        camera.up.set(-sinYaw * sinPitch, cosPitch, -cosYaw * sinPitch).nor();

        camera.position.mulAdd(camera.direction, 0.3f);

        camera.near = 0.05f;
        camera.far = 1000f;
        camera.fieldOfView = 70f;
        camera.viewportWidth  = Gdx.graphics.getWidth();
        camera.viewportHeight = Gdx.graphics.getHeight();
        camera.update();
    }

    public float getLookX() {
        return (float) (-Math.sin(Math.toRadians(yRot)) * Math.cos(Math.toRadians(xRot)));
    }
    public float getLookY() {
        return (float) (-Math.sin(Math.toRadians(xRot)));
    }
    public float getLookZ() {
        return (float) (-Math.cos(Math.toRadians(yRot)) * Math.cos(Math.toRadians(xRot)));
    }
}
