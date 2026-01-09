import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game3dold extends Game{

	private final double focalLength;

	private final double[][] zBuffer;

	public final double speed = .01;
	public final double rotSpeed = .01;


	Point[] points;
	Triangle[] triangles;
	
	int cx = width / 2;
	int cy = height / 2;

	public Game3dold(int width, int height, double fov){
		super(width, height);
		
		points = new Point[]{
			new Point(-1, -1, -1),
			new Point(-1, -1, 1),
			new Point(-1, 1, -1),
			new Point(-1, 1, 1),
			new Point(1, -1, -1),
			new Point(1, -1, 1),
			new Point(1, 1, -1),
			new Point(1, 1, 1),
		};

		triangles = new Triangle[] {
			new Triangle(0, 1, 2, points),
			new Triangle(1, 2, 3, points),
			new Triangle(2, 3, 6, points),
			new Triangle(7, 3, 6, points),
			new Triangle(7, 4, 6, points),
			new Triangle(7, 4, 5, points),
			new Triangle(7, 3, 5, points),
			new Triangle(1, 3, 5, points),
			new Triangle(5 ,0, 1, points),
			new Triangle(4 ,0, 5, points),
			new Triangle(0, 2, 6, points),
			new Triangle(0, 4, 6, points),
		};
		try {
			Model model = loadObj("Models/teapot.obj");
			points = model.points();
			triangles = model.triangles();
			System.out.println("successfully loaded");
		} catch (Exception e) {
			System.out.println("failed to load");
			e.printStackTrace();
		}

		this.focalLength = (double) height / (2 * Math.tan(fov/2));
		zBuffer = new double[width][height];
		for (Point point : points){
			point.translateZ(5);
		}
	}
	@Override
	public String name(){
		return "Game 3d";
	}
	@Override
	public void tick(){
		for (Point point : points){
			if (input.keys['D']){
				point.translateX(-speed);
			}
			if (input.keys['A']){
				point.translateX(speed);
			}
			if (input.keys[' ']){
				point.translateY(-speed);
			}
			if (input.keys[Input.SHIFT]){
				point.translateY(speed);
			}
			if (input.keys['W']){
				point.translateZ(-speed);
			}
			if (input.keys['S']){
				point.translateZ(speed);
			}

			if (input.keys[Input.LEFT_ARROW]){
				point.rotateYaw(-rotSpeed);
			}
			if (input.keys[Input.RIGHT_ARROW]){
				point.rotateYaw(rotSpeed);
			}
			if (input.keys[Input.DOWN_ARROW]){
				point.rotatePitch(-rotSpeed);
			}
			if (input.keys[Input.UP_ARROW]){
				point.rotatePitch(rotSpeed);
			}
			if (input.keys['Q']){
				point.rotateRoll(-rotSpeed);
			}
			if (input.keys['E']){
				point.rotateRoll(rotSpeed);
			}
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
			Arrays.fill(zBuffer[x], Double.NEGATIVE_INFINITY);
		}
	}
	@Override
	public void updateFrame(Graphics2D g2d){
		clearZBuffer();
		for (Triangle triangle : triangles){
			triangle.render(g2d, focalLength, cx, cy, zBuffer);
		}
	}
	public static Model loadObj(String filename) throws FileNotFoundException, IOException{
		BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
		List<Point> points = new ArrayList<>();
		List<Point> pointBuffer = new ArrayList<>();
		List<Triangle> triangles = new ArrayList<>();
		for (String line = reader.readLine(); line != null; line = reader.readLine()){
			if (line.length() == 0) continue;
			char type = line.charAt(0);
			if (type == 'v'){
				if (line.charAt(1) == 'n'){
					continue;
				}
				String[] rawValues = line.split(" ");
				points.add(new Point(Double.parseDouble(rawValues[1]),Double.parseDouble(rawValues[2]),Double.parseDouble(rawValues[3])));
			} else if (type == 'f'){
				String[] thesePoints = line.split(" ");
				pointBuffer.clear();
				for (int i = 1; i < thesePoints.length; i++){
					String rawPoint = thesePoints[i];
					int index = Integer.parseInt(rawPoint.split("/")[0]);
					pointBuffer.add(points.get(index-1));
				}
				while (pointBuffer.size()>2){
					triangles.add(new Triangle(0, 1, 2, pointBuffer.toArray(Point[]::new)));
					pointBuffer.remove(1);
				}
			}
		}
		reader.close();

		return new Model(triangles.toArray(Triangle[]::new),points.toArray(Point[]::new));
	}
}