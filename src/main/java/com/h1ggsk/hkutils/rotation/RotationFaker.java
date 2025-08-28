package com.h1ggsk.hkutils.rotation;

import com.h1ggsk.hkutils.event.PostMotionEvent;
import com.h1ggsk.hkutils.event.PreMotionEvent;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public final class RotationFaker {
	private boolean fakeRotation;
	private float serverYaw;
	private float serverPitch;
	private float realYaw;
	private float realPitch;


	@EventHandler
	public void onPreMotion(PreMotionEvent event)
	{
		if(!fakeRotation)
			return;

		ClientPlayerEntity player = MeteorClient.mc.player;
		realYaw = player.getYaw();
		realPitch = player.getPitch();
		player.setYaw(serverYaw);
		player.setPitch(serverPitch);
	}

	@EventHandler
	public void onPostMotion(PostMotionEvent event)
	{
		if(!fakeRotation)
			return;

		ClientPlayerEntity player = MeteorClient.mc.player;
		player.setYaw(realYaw);
		player.setPitch(realPitch);
		fakeRotation = false;
	}

	public void faceVectorPacket(Vec3d vec)
	{
		Rotation needed = RotationUtils.getNeededRotations(vec);
		ClientPlayerEntity player = MeteorClient.mc.player;

		fakeRotation = true;
		serverYaw =
			RotationUtils.limitAngleChange(player.getYaw(), needed.yaw());
		serverPitch = needed.pitch();
	}

	public void faceVectorClient(Vec3d vec)
	{
		Rotation needed = RotationUtils.getNeededRotations(vec);

		ClientPlayerEntity player = MeteorClient.mc.player;
		player.setYaw(
			RotationUtils.limitAngleChange(player.getYaw(), needed.yaw()));
		player.setPitch(needed.pitch());
	}

	public void faceVectorClientIgnorePitch(Vec3d vec)
	{
		Rotation needed = RotationUtils.getNeededRotations(vec);

		ClientPlayerEntity player = MeteorClient.mc.player;
		player.setYaw(
			RotationUtils.limitAngleChange(player.getYaw(), needed.yaw()));
		player.setPitch(0);
	}

	public float getServerYaw()
	{
		return fakeRotation ? serverYaw : MeteorClient.mc.player.getYaw();
	}

	public float getServerPitch()
	{
		return fakeRotation ? serverPitch : MeteorClient.mc.player.getPitch();
	}
}
