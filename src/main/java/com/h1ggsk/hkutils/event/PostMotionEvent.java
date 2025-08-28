package com.h1ggsk.hkutils.event;

import meteordevelopment.meteorclient.events.Cancellable;

public class PostMotionEvent extends Cancellable {
	private static final PostMotionEvent INSTANCE = new PostMotionEvent();

	public static PostMotionEvent get() {
		return INSTANCE;
	}
}
