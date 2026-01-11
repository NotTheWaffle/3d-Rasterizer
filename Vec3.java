public class Vec3 {
	public double x, y, z;

	public Vec3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public void normalize(){
		double r = Math.sqrt(x*x + y*y+ z*z);
		x /= r;
		y /= r;
		z /= r;
	}
	public static Vec3 normalize(Vec3 v){
		double r = Math.sqrt(v.x*v.x + v.y*v.y+ v.z*v.z);
		return new Vec3(
			v.x/r,
			v.y/r,
			v.z/r
		);
	}
	public void add(Vec3 v){
		x += v.x;
		y += v.y;
		z += v.z;
	}
	public static Vec3 add(Vec3 v1, Vec3 v2){
		return new Vec3(v1.x+v2.x, v1.y+v2.y, v1.z+v2.z);
	}
	public void sub(Vec3 v){
		x -= v.x;
		y -= v.y;
		z -= v.z;
	}
	public static Vec3 sub(Vec3 v1, Vec3 v2){
		return new Vec3(v1.x-v2.x, v1.y-v2.y, v1.z-v2.z);
	}
	public void mult(double m){
		x *= m;
		y *= m;
		z *= m;
	}
	public static Vec3 mult(Vec3 v, double m){
		return new Vec3(v.x * m, v.y * m, v.z * m);
	}
	public static double dot(Vec3 v1, Vec3 v2) {
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}
	public static Vec3 cross(Vec3 v1, Vec3 v2){
		return new Vec3(
			v1.y * v2.z - v1.z * v2.y,
			v1.z * v2.x - v1.x * v2.z,
			v1.x * v2.y - v1.y * v2.x
		);
	}
	public Vec4 toVec4(double w){
		return new Vec4(x, y, z, w);
	}
}