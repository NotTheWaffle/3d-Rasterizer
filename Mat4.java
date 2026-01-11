
import java.util.Arrays;

public class Mat4 {
	public final double[] m;
	private Mat4(){
		this.m = new double[16];
	}
	public static Mat4 identity() {
		Mat4 r = new Mat4();
		for (int i = 0; i < 16; i+=5) r.m[i] = 1;
		return r;
	}
	public static Mat4 multiply(Mat4 a, Mat4 b) {
		Mat4 r = new Mat4();
		for (int row = 0; row < 16; row+=4) {
			for (int col = 0; col < 4; col++) {
				for (int k = 0; k < 4; k++) {
					r.m[row+col] += a.m[row+k] * b.m[k*4+col];
				}
			}
		}
		return r;
	}
	public Vec4 transform(Vec4 v) {
		return new Vec4(
			v.x * m[0] + v.y * m[1] + v.z * m[2] + v.w * m[3],
			v.x * m[4] + v.y * m[5] + v.z * m[6] + v.w * m[7],
			v.x * m[8] + v.y * m[9] + v.z * m[10] + v.w * m[11],
			v.x * m[12] + v.y * m[13] + v.z * m[14] + v.w * m[15]
		);
	}
	@Override
	public int hashCode(){
		return Arrays.hashCode(m);
	}
	@Override
	public boolean equals(Object o){
		if (o == null || !(o instanceof Mat4)){
			return false;
		}
		Mat4 mat = (Mat4) o;
		return Arrays.equals(mat.m, m);
	}
}