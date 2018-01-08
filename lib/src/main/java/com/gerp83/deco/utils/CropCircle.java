package com.gerp83.deco.utils;

/**
 * Created by GErP83
 */

public class CropCircle {

    private float x;
    private float y;
    private float radius;

    void set(float x, float y, float radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }

}