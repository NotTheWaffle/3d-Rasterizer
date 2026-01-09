
import java.awt.Color;
import java.awt.image.WritableRaster;

public class Triangle {
	public final Vec4 p1;
	public final Vec4 p2;
	public final Vec4 p3;
	public final Color color;

	public Triangle(Vec4 p1, Vec4 p2, Vec4 p3){
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.color = new Color((int)(Math.random()*16777216));
	}
	public Triangle(int i1, int i2, int i3, Vec4[] points){
		this.p1 = points[i1];
		this.p2 = points[i2];
		this.p3 = points[i3];
		this.color = new Color((int)(Math.random()*16777216));
	}
	public void render(WritableRaster raster, double focalLength, int cx, int cy, double[][] zBuffer, Mat4 cam) {
		Vec4 p1 = cam.transform(this.p1);
		Vec4 p2 = cam.transform(this.p2);
		Vec4 p3 = cam.transform(this.p3);
		
		if (p1.z > 0 || p2.z > 0 || p3.z > 0) return;

		double iz1 = 1.0 / p1.z;
		double iz2 = 1.0 / p2.z;
		double iz3 = 1.0 / p3.z;


		int x1 = (int)( focalLength * p1.x / p1.z) + cx;
		int y1 = (int)(-focalLength * p1.y / p1.z) + cy;

		int x2 = (int)( focalLength * p2.x / p2.z) + cx;
		int y2 = (int)(-focalLength * p2.y / p2.z) + cy;

		int x3 = (int)( focalLength * p3.x / p3.z) + cx;
		int y3 = (int)(-focalLength * p3.y / p3.z) + cy;

		int minX = Math.max(0, Math.min(x1, Math.min(x2, x3)));
		int maxX = Math.min(zBuffer.length - 1, Math.max(x1, Math.max(x2, x3)));
		int minY = Math.max(0, Math.min(y1, Math.min(y2, y3)));
		int maxY = Math.min(zBuffer[0].length - 1, Math.max(y1, Math.max(y2, y3)));

		double area = edge(x1, y1, x2, y2, x3, y3);
		if (area == 0) return;

		int[] rgb = {
			color.getRed(),
			color.getGreen(),
			color.getBlue(),
			255
		};
		double ia = 1/area;
		double w1Incr = (y3-y2) * ia;
		double w2Incr = (y1-y3) * ia;
		double w3Incr = (y2-y1) * ia;

		for (int y = minY; y <= maxY; y++) {
			double w1 = edge(x2, y2, x3, y3, minX-1, y) * ia;
			double w2 = edge(x3, y3, x1, y1, minX-1, y) * ia;
			double w3 = edge(x1, y1, x2, y2, minX-1, y) * ia;
			for (int x = minX; x <= maxX; x++) {

				w1 += w1Incr;
				w2 += w2Incr;
				w3 += w3Incr;

				if (w1 >= 0 && w2 >= 0 && w3 >= 0) {
					double iz = w1 * iz1 + w2 * iz2 + w3 * iz3;

					if (iz < zBuffer[x][y]) {
						zBuffer[x][y] = iz;
						raster.setPixel(x, y, rgb);
					}
				}
			}
		}
	}
	private static double edge(int x1, int y1, int x2, int y2, int x, int y) {
		return (x - x1) * (y2 - y1) - (y - y1) * (x2 - x1);
	}
	@Override
	public int hashCode(){
		return p1.hashCode() ^ p2.hashCode() ^ p3.hashCode();
	}
	@Override
	public boolean equals(Object o){
		if (o == null || !(o instanceof Triangle)){
			return false;
		}
		Triangle t = (Triangle) o;
		return t.p1.equals(p1) && t.p2.equals(p2) && t.p3.equals(p3);
	}
}