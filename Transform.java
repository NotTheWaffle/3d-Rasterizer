public class Transform {
	private Transform(){};
	public static Mat4 rotationX(double a) {
		Mat4 m = Mat4.identity();
		m.m[5] =  Math.cos(a);
		m.m[6] = -Math.sin(a);
		m.m[9] =  Math.sin(a);
		m.m[10] =  Math.cos(a);
		return m;
	}

	public static Mat4 rotationY(double a) {
		Mat4 m = Mat4.identity();
		m.m[0] =  Math.cos(a);
		m.m[2] =  Math.sin(a);
		m.m[8] = -Math.sin(a);
		m.m[10] =  Math.cos(a);
		return m;
	}

	public static Mat4 rotationZ(double a) {
		Mat4 m = Mat4.identity();
		m.m[0] =  Math.cos(a);
		m.m[1] = -Math.sin(a);
		m.m[4] =  Math.sin(a);
		m.m[5] =  Math.cos(a);
		return m;
	}
	public static Mat4 translation(double x, double y, double z) {
		Mat4 m = Mat4.identity();
		m.m[3] = x;
		m.m[7] = y;
		m.m[11] = z;
		return m;
	}
}
