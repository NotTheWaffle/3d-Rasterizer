
import java.util.Arrays;

public class Mat4 {
	public final double[][] m = new double[4][4];
	private Mat4(){
	}
	public static Mat4 identity() {
		Mat4 r = new Mat4();
		for (int i = 0; i < 4; i++) r.m[i][i] = 1;
		return r;
	}
	public static Mat4 multiply(Mat4 a, Mat4 b) {
		Mat4 r = new Mat4();
		for (int row = 0; row < 4; row++) {
			for (int col = 0; col < 4; col++) {
				for (int k = 0; k < 4; k++) {
					r.m[row][col] += a.m[row][k] * b.m[k][col];
				}
			}
		}
		return r;
	}
	public Vec4 transform(Vec4 v) {
		return new Vec4(
			v.x * m[0][0] + v.y * m[0][1] + v.z * m[0][2] + v.w * m[0][3],
			v.x * m[1][0] + v.y * m[1][1] + v.z * m[1][2] + v.w * m[1][3],
			v.x * m[2][0] + v.y * m[2][1] + v.z * m[2][2] + v.w * m[2][3],
			v.x * m[3][0] + v.y * m[3][1] + v.z * m[3][2] + v.w * m[3][3]
		);
	}
	public boolean equals(Object o){
		if (o == null || !(o instanceof Mat4)){
			return false;
		}
		Mat4 mat = (Mat4) o;
		return Arrays.equals(mat.m, m);
	}
}