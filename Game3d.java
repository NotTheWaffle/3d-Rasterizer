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

	private final Vec3 light;

	private Transform cam;

	Triangle[] triangles;
	
	int cx = width / 2;
	int cy = height / 2;

	public Game3d(int width, int height, double fov, String model){
		super(width, height);
		this.light = new Vec3(0, -1, 1);
		triangles = loadObj("Models/"+model+".obj", light);
		if (triangles == null){
			System.exit(1);
		}
		cam = new Transform();
		cam.rotateZ(Math.PI);
		cam.translate(0, 0, 1);
		
		this.focalLength = (double) height / (2 * Math.tan(fov/2));
		zBuffer = new double[width][height];
	}

	@Override
	public String name(){
		return "Game 3d";
	}
	@Override
	public void tick(){
		
		if (input.keys['W']) 				cam.translate(0, 0, -speed);
		if (input.keys['A']) 				cam.translate(speed, 0, 0);
		if (input.keys['S']) 				cam.translate(0, 0, speed);
		if (input.keys['D']) 				cam.translate(-speed, 0, 0);
		if (input.keys[' ']) 				cam.translate(0, -speed, 0);
		if (input.keys[Input.SHIFT]) 		cam.translate(0, speed, 0);
	
		if (input.keys[Input.UP_ARROW]) 	cam.rotateX( rotSpeed);
		if (input.keys[Input.DOWN_ARROW]) 	cam.rotateX(-rotSpeed);
		if (input.keys[Input.LEFT_ARROW]) 	cam.rotateY( rotSpeed);
		if (input.keys[Input.RIGHT_ARROW]) 	cam.rotateY(-rotSpeed);
		if (input.keys['Q']) 				cam.rotateZ(-rotSpeed);
		if (input.keys['E']) 				cam.rotateZ( rotSpeed);
		for (Triangle t : triangles){
			t.recolor(cam.getForwardVector().normalize());
		}
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

		
		Vec3 origin = cam.translation;
		Vec3 vector = cam.getForwardVector().scale(-1);
		Vec3 p = null;
		for (Triangle t : triangles){
			Vec3 p2 = t.getIntersection(vector, origin);
			if (p2 == null) continue;
			if (p == null || p.dist(origin) > p2.dist(origin)){
				p = p2;
			}
		}
		p = light;
		if (p != null){
			Point point = new Point(p, .1);
	//		System.out.println(cam.applyTo(p));
		}
		




		g2d.drawImage(image, 0, 0, null);

		


		g2d.drawString(cam.translation.toString(), 0, 80);

			g2d.drawString(p.toString(), 0, 60);
		


		long renderTime = System.nanoTime()-renderStart;
		g2d.drawString("Time (ms):"+renderTime/1_000_000.0,0,20);
		g2d.drawString(cam.getForwardVector().toString(), 0, 40);
	}

	public static Triangle[] loadObj(String filename, Vec3 light){
		light = light.normalize();
		List<Vec3> points = new ArrayList<>();
		List<Triangle> triangles = new ArrayList<>();
		double minX, minY, minZ, maxX, maxY, maxZ;
		minX = minY = minZ = Double.POSITIVE_INFINITY;
		maxX = maxY = maxZ = Double.NEGATIVE_INFINITY;
		
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			List<Vec3> pointBuffer = new ArrayList<>();
			for (String line = reader.readLine(); line != null; line = reader.readLine()){
				if (line.length() == 0) continue;
				if (line.charAt(0) == '#') continue;
				char type = line.charAt(0);
				if (type == 'v'){
					if (line.charAt(1) == 'n' || line.charAt(1) == 't'){
						continue;
					}
					String[] rawValues = line.split(" ");
					Vec3 point = new Vec3(Double.parseDouble(rawValues[1]),Double.parseDouble(rawValues[2]),Double.parseDouble(rawValues[3]));
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
						triangles.add(new Triangle(0, 1, 2, pointBuffer.toArray(Vec3[]::new), light));
						pointBuffer.remove(1);
					}
				}
			}
		} catch (Exception e){
			System.out.println(filename+" failed to load");
			e.printStackTrace();
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

		for (Vec3 point : points){
			point.x = (point.x - minX)*scale-xrange/2;
			point.y = (point.y - minY)*scale-yrange/2;
			point.z = (point.z - minZ)*scale-zrange/2;
		}

		return triangles.toArray(Triangle[]::new);
	}
}