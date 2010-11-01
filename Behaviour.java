import lejos.nxt.Sound;

/**
 * << Behaviour is part of the maze solving robotics system Rhino >>
 * Left hand rule. Simple.
 *
 * @author David Hollands
 * @version $0.85$ 
 */
public class Behaviour implements Runnable {
	private State state;
	private Output output;
	private boolean control = true, debug = true;
	private final int LEFT_BOUND = 900;
	private final int RIGHT_BOUND = 900;
	private final int CENTRE_BOUND = 900;//40; //35
	private GridWorld world;
	private static final short [] note = {
	    2349,115, 0,5, 1760,165, 0,35, 1760,28, 0,13, 1976,23, 
	    0,18, 1760,18, 0,23, 1568,15, 0,25, 1480,103, 0,18, 1175,180, 0,20, 1760,18, 
	    0,23, 1976,20, 0,20, 1760,15, 0,25, 1568,15, 0,25, 2217,98, 0,23, 1760,88, 
	    0,33, 1760,75, 0,5, 1760,20, 0,20, 1760,20, 0,20, 1976,18, 0,23, 1760,18, 
	    0,23, 2217,225, 0,15, 2217,218};
	
	// {{{ Behaviour constructor
	/**
	 * Oh behave... 
	 */
	public Behaviour(State my_state, Output my_output, boolean debug) {
		state = my_state;
		output = my_output;
		world = new GridWorld(10, 10, 0, 0, 4, 3);
		this.debug = true;
	}
	// }}}
	
	public void run() {
		try {
			Thread.sleep(100); // a subtle pause
			while(!world.isAtTarget()) {				
				if(state.getLeftReading() > LEFT_BOUND) {
					if(debug)for(int i=0;i<10;i++) System.out.println("TURNING LEFT");
					state.turn();
					output.turnLeft();
					world.updateDirection(3);
					state.charge();
					output.moveForwardOneBlock(control);
					world.updateCurrentBlock();
				} else {
					if(state.getCentreReading() > CENTRE_BOUND) {
						if(debug)for(int i=0;i<10;i++) System.out.println("FOREWORD!");
						state.charge();
						output.moveForwardOneBlock(control);
						world.updateCurrentBlock();
					} else {
						if(state.getRightReading() > RIGHT_BOUND) {
							if(debug)for(int i=0;i<10;i++) System.out.println("TURNING RIGHT");
							state.turn();
							output.turnRight();
							world.updateDirection(1);
							state.charge();
							output.moveForwardOneBlock(control);
							world.updateCurrentBlock();
						} else {
							if(debug)for(int i=0;i<10;i++) System.out.println("ABOUT TURN!");
							state.turn();
							output.aboutTurn();
							world.updateDirection(2);
							state.charge();
							output.moveForwardOneBlock(control);
							world.updateCurrentBlock();
						}
					}
				}
			}

			// sound a tune...
			for(int i=0;i<note.length; i+=2) {
        			final short w = note[i+1];
			        final int n = note[i];
         			if (n != 0) Sound.playTone(n, w*10);
         			Thread.sleep(w*10);
      			}
		} catch(InterruptedException ex) { }
	}
}

class GridWorld {
	
	int[][] world;
	int direction = 0; // north == 0; east == 1; south == 2; west == 3;
	int originX, originY;
	int currentX, currentY;
	int targetX, targetY;
	boolean targetReached = false;
	
	public GridWorld(int x, int y, int startX, int startY, int finishX, int finishY) {
		
		// init
		world = new int[x][y];
		for(int i = 0; i < x - 1; i++) {
			for(int j = 0; j < y - 1; j++) {
				world[i][j] = 0;
			}
		}
		originX = startX;
		originY = startY;
		currentX = startX;
		currentY = startY;
		targetX = finishX;
		targetY = finishY;
		
		// add 1 to start node
		world[originX][originY]++;
	}
	
	public void updateCurrentBlock() {
		switch (direction % 4) {
			case 0: // north
				currentY++;
				break;
			case 1: // east
				currentX++;
				break;
			case 2: // south
			 	currentY--;
				break;
			case 3: // west
				currentX--;
				break;
		}
		// add 1 to new node (path drawing)
		world[currentX][currentY]++;
		
		// check if target node reached
		if(targetX == currentX && targetY == currentY) {
			targetReached = true;
		}
	}
	
	public boolean isAtTarget() {
		System.out.println("" + currentX + currentY);
		return targetReached;
	}
	
	public void updateDirection(int input) {
		direction = direction + input;
	}
}
