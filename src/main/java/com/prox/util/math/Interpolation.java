package com.prox.util.math;

import org.joml.*;

public class Interpolation {

    // Imprecise method, which does not guarantee v = v1 when t = 1, due to floating-point arithmetic error. This method is monotonic
    // This form may be used when the hardware has a native fused multiply-add instruction.
    public static float lerp(float v0, float v1, float t) {
        return v0 + t * (v1 - v0);
    }

    // Precise method, which guarantees v = v1 when t = 1. This method is monotonic only when v0 * v1 < 0. Lerping between same values might not produce the same value
    public static float lerp2(float v0, float v1, float t) {
        return (1 - t) * v0 + t * v1;
    }

    // Triangle Interpolation
    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
}
