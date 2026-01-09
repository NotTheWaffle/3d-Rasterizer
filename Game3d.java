import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game3d extends Game{

	private final double focalLength;

	private final double[][] zBuffer;

	public final double speed = .02;
	public final double rotSpeed = .03;

	private Mat4 cam;

	Triangle[] triangles;
	
	int cx = width / 2;
	int cy = height / 2;

	public Game3d(int width, int height, double fov, String model){
		super(width, height);
		triangles = loadObj("Models/"+model+".obj");
		cam = Mat4.multiply(Transform.rotationZ(Math.PI), Transform.translation(0, 0, -3));
		this.focalLength = (double) height / (2 * Math.tan(fov/2));
		zBuffer = new double[width][height];
	}

	@Override
	public String name(){
		return "Game 3d";
	}
	@Override
	public void tick(){
		if (input.keys['W']) 			cam = Mat4.multiply(Transform.translation(0, 0,  speed), cam);
		if (input.keys['A']) 			cam = Mat4.multiply(Transform.translation(-speed, 0, 0), cam);
		if (input.keys['S']) 			cam = Mat4.multiply(Transform.translation(0, 0, -speed), cam);
		if (input.keys['D']) 			cam = Mat4.multiply(Transform.translation( speed, 0, 0), cam);
		if (input.keys[' ']) 			cam = Mat4.multiply(Transform.translation(0,  speed, 0), cam);
		if (input.keys[Input.SHIFT]) 	cam = Mat4.multiply(Transform.translation(0, -speed, 0), cam);
	
		if (input.keys[Input.UP_ARROW]) 	cam = Mat4.multiply(Transform.rotationX(rotSpeed), cam);
		if (input.keys[Input.DOWN_ARROW]) 	cam = Mat4.multiply(Transform.rotationX(-rotSpeed), cam);
		if (input.keys[Input.LEFT_ARROW]) 	cam = Mat4.multiply(Transform.rotationY(rotSpeed), cam);
		if (input.keys[Input.RIGHT_ARROW]) 	cam = Mat4.multiply(Transform.rotationY(-rotSpeed), cam);
		if (input.keys['Q']) 				cam = Mat4.multiply(Transform.rotationZ(-rotSpeed), cam);
		if (input.keys['E']) 				cam = Mat4.multiply(Transform.rotationZ(rotSpeed), cam);
	}

	public double getX(double x, double y, double z) {
		return focalLength * (x / z);
	}
	public double getY(double x, double y, double z) {
		return focalLength * (y / z);
	}

	private void clearZBuffer() {
		for (int x = 0; x < width; x++) {
			Arrays.fill(zBuffer[x], Double.POSITIVE_INFINITY);
		}
	}

	@Override
	public void updateFrame(Graphics2D g2d){
		long renderStart = System.nanoTime();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		WritableRaster raster = image.getRaster();

		clearZBuffer();
		for (Triangle triangle : triangles){
			triangle.render(raster, focalLength, cx, cy, zBuffer, cam);
		}

		g2d.drawImage(image, 0, 0, null);
		long renderTime = System.nanoTime()-renderStart;
		g2d.drawString("Time (ms):"+renderTime/1_000_000.0,0,100);
		for (int x = 0; x < 4; x++){
			for (int y = 0; y < 4; y++){
				g2d.drawString((int) (100*cam.m[y][x])/100.0+" ",x*40,y*20+20);
			}
		}
	}

	public static Triangle[] loadObj(String filename){
		List<Vec4> points = new ArrayList<>();
		List<Triangle> triangles = new ArrayList<>();
		double minX, minY, minZ, maxX, maxY, maxZ;
		minX = minY = minZ = Double.POSITIVE_INFINITY;
		maxX = maxY = maxZ = Double.NEGATIVE_INFINITY;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			List<Vec4> pointBuffer = new ArrayList<>();
			for (String line = reader.readLine(); line != null; line = reader.readLine()){
				if (line.length() == 0) continue;
				if (line.charAt(0) == '#') continue;
				char type = line.charAt(0);
				if (type == 'v'){
					if (line.charAt(1) == 'n'){
						continue;
					}
					String[] rawValues = line.split(" ");
					Vec4 point = new Vec4(Double.parseDouble(rawValues[1]),Double.parseDouble(rawValues[2]),Double.parseDouble(rawValues[3]),1);
					if (point.x > maxX) maxX = point.x;
					if (point.x < minX) minX = point.x;
					
					if (point.y > maxY) maxY = point.y;
					if (point.y < minY) minY = point.y;
					
					if (point.z > maxZ) maxZ = point.z;
					if (point.z < minZ) minZ = point.z;

					points.add(point);
				} else if (type == 'f'){
					String[] thesePoints = line.split(" ");
					pointBuffer.clear();
					for (int i = 1; i < thesePoints.length; i++){
						String rawPoint = thesePoints[i];
						int index = Integer.parseInt(rawPoint.split("/")[0]);
						pointBuffer.add(points.get(index-1));
					}
					while (pointBuffer.size()>2){
						triangles.add(new Triangle(0, 1, 2, pointBuffer.toArray(Vec4[]::new)));
						pointBuffer.remove(1);
					}
				}
			}
		} catch (Exception e){
			System.out.println(filename+" failed to load");
			return null;
		}
		System.out.println(filename+" successfully loaded");
		System.out.println(triangles.size()+" triangles");

		double xrange = maxX-minX;
		double yrange = maxY-minY;
		double zrange = maxZ-minZ;
		double maxRange = Math.max(Math.max(xrange,yrange),zrange);

		double scale = 1/maxRange;

		xrange *= scale;
		yrange *= scale;
		zrange *= scale;

		for (Vec4 point : points){
			point.x = (point.x - minX)*scale-xrange/2;
			point.y = (point.y - minY)*scale-yrange/2;
			point.z = (point.z - minZ)*scale-zrange/2;
		}

		return triangles.toArray(Triangle[]::new);
	}
}