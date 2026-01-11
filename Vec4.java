public class Vec4 {
	public double x, y, z, w;

	public Vec4(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	public Vec3 toVec3(){
		return new Vec3(x*w, y*w, z*w);
	}
}