package com.h1ggsk.hkutils.event;

public class MouseUpdateEvent {
    public double deltaX;
    public double deltaY;
    public double defaultDeltaX;
    public double defaultDeltaY;

    public static final MouseUpdateEvent INSTANCE = new MouseUpdateEvent();

    private MouseUpdateEvent() {
    }

    public static MouseUpdateEvent set(double deltaX, double deltaY) {
        INSTANCE.deltaX = deltaX;
        INSTANCE.deltaY = deltaY;
        INSTANCE.defaultDeltaX = deltaX;
        INSTANCE.defaultDeltaY = deltaY;
        return INSTANCE;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(double deltaX) {
        this.deltaX = deltaX;
    }

    public double getDeltaY() {
        return deltaY;
    }

    public void setDeltaY(double deltaY) {
        this.deltaY = deltaY;
    }

    public double getDefaultDeltaX() {
        return defaultDeltaX;
    }

    public double getDefaultDeltaY() {
        return defaultDeltaY;
    }

    public void addDeltaX(double deltaX) {
        this.deltaX += deltaX;
    }

    public void addDeltaY(double deltaY) {
        this.deltaY += deltaY;
    }
}
