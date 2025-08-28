package com.h1ggsk.hkutils.event;

import meteordevelopment.meteorclient.events.Cancellable;

public class PreMotionEvent extends Cancellable {
	private static final PreMotionEvent INSTANCE = new PreMotionEvent();

	public static PreMotionEvent get() {
		return INSTANCE;
	}
}
