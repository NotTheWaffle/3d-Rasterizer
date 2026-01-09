
import java.util.concurrent.locks.LockSupport;


public class Main {
	public static void main(String[] args){
		runGame(new Game3d(820,512, Math.PI/2, "cessna"), 60);
	}
	public static void runGame(final Game game, final double fps){
		final Window window = new Window(game);
		final long frameLength = (long) (1_000_000_000/fps);
		while (true){
			long targetTime = System.nanoTime() + frameLength;

			game.tick();
			window.render();
			
			long remaining = targetTime - System.nanoTime();
			if (remaining > 100_000) {
				LockSupport.parkNanos(remaining - 2_000);
			}
			while (System.nanoTime() < targetTime) {
				Thread.onSpinWait();
			}
		}
	}
}