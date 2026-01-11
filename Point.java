
import java.awt.Color;

public class Point {
	public final Vec3 point;
	public final Color color;
	public final double radius;
	public Point(Vec3 p, double radius){
		this.point = p;
		this.color = new Color((int)(Math.random()*16777216));
		this.radius = radius;
	}
	public void render(java.awt.image.WritableRaster raster, double focalLength, int cx, int cy, double[][] zBuffer, Transform cam) {
		Vec3 p = cam.applyTo(this.point);
		if (p.z >= 0) return;
		
		int x1 = (int)( focalLength * p.x / p.z) + cx;
		int y1 = (int)(-focalLength * p.y / p.z) + cy;

		int minX = Math.max(0, x1);
		int maxX = Math.min(zBuffer.length - 1, x1);
		int minY = Math.max(0, y1);
		int maxY = Math.min(zBuffer[0].length - 1, y1);

		int[] rgb = {
			color.getRed(),
			color.getGreen(),
			color.getBlue(),
			255
		};

		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {

				if (x * x + y * y < radius * radius) {
					double iz = p.z;


					if (true || iz < zBuffer[x][y]) {
						zBuffer[x][y] = iz;
						raster.setPixel(x, y, rgb);
					}
				}
			}
		}
	}
}
