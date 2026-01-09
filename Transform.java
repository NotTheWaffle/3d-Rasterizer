public class Transform {
	private Transform(){};
	public static Mat4 rotationX(double a) {
		Mat4 m = Mat4.identity();
		m.m[1][1] =  Math.cos(a);
		m.m[1][2] = -Math.sin(a);
		m.m[2][1] =  Math.sin(a);
		m.m[2][2] =  Math.cos(a);
		return m;
	}

	public static Mat4 rotationY(double a) {
		Mat4 m = Mat4.identity();
		m.m[0][0] =  Math.cos(a);
		m.m[0][2] =  Math.sin(a);
		m.m[2][0] = -Math.sin(a);
		m.m[2][2] =  Math.cos(a);
		return m;
	}

	public static Mat4 rotationZ(double a) {
		Mat4 m = Mat4.identity();
		m.m[0][0] =  Math.cos(a);
		m.m[0][1] = -Math.sin(a);
		m.m[1][0] =  Math.sin(a);
		m.m[1][1] =  Math.cos(a);
		return m;
	}
	public static Mat4 translation(double x, double y, double z) {
		Mat4 m = Mat4.identity();
		m.m[0][3] = x;
		m.m[1][3] = y;
		m.m[2][3] = z;
		return m;
	}
}
