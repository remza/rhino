//import lejos.nxt.Sound;
import java.util.ArrayList;
/**
 * << Behaviour is part of the maze solving robotics system Rhino >>
 *
 * @author David Hollands - A* algorithm adapted from Dave Cohen slides.
 * @version $0.1a$ 
 */
public class Behaviour2 implements Runnable {
	private State state;
	private Output output;
	private boolean control = true;
	private int LEFT_BOUND = 1000;
	private int RIGHT_BOUND = 1000;
	private int CENTRE_BOUND = 1000; //40; //35
//	private GridWorld world;
/*	private static final short [] note = {
	    2349,115, 0,5, 1760,165, 0,35, 1760,28, 0,13, 1976,23, 
	    0,18, 1760,18, 0,23, 1568,15, 0,25, 1480,103, 0,18, 1175,180, 0,20, 1760,18, 
	    0,23, 1976,20, 0,20, 1760,15, 0,25, 1568,15, 0,25, 2217,98, 0,23, 1760,88, 
	    0,33, 1760,75, 0,5, 1760,20, 0,20, 1760,20, 0,20, 1976,18, 0,23, 1760,18, 
	    0,23, 2217,225, 0,15, 2217,218};*/
	
	
		// stuff
		int targetX, targetY;
		int direction = 0; // facing north
		Square[][] world = new Square[10][10];
		Square currentSquare;
		
		int goalX = 2;
		int goalY = 2;
		Square goalSquare = new Square(goalX, goalY, 0, -1, null); // -1 (unknown) distance from start
		
		int startX = 0;
		int startY = 0;
		Square startSquare = new Square(startX, startY, H(startX, startY), 0, null);
		
		// for movement purposes
		Square targetSquare;
		
		// not ness at mo
		//world [0][0] = startSquare;
		//world [1][2] = goalSquare;
		// only way of accessing is by loop etc..
		
		
		ArrayList<Square> openSquares = new ArrayList<Square>();
		ArrayList<Square> closedSquares = new ArrayList<Square>();
	
	// {{{ Behaviour constructor
	/**
	 * 
	 */
	public Behaviour2(State my_state, Output my_output) {
    state = my_state;
		output = my_output;
		world[startX][startY] = startSquare;
		world[goalX][goalY] = goalSquare;
	}
	// }}}
	
/*	void setSquare(int x, int y, Square square) {
		for (int i = 0; i == x; i++) {
      for (int j = 0; j == y; j++) {
        world[i][j] = square;
      }
		}
	} */
	
	
	public void run() {
		try {
				
			Square temp;
			Square temp2;
			ArrayList<Square> accessibleSquares;
			
			// add start node to open list
			openSquares.add(startSquare);
			
			// repeat until either: 
			// the target square is on the closed list (the path to goal has been found)
			// or the open list is empty (there is no path)
			System.out.println("Outside loop");
			while (!openSquares.isEmpty() && !isGoalSquareClosed()) {
				
				System.out.println("Inside loop");
				// find and move to lowest F open square
				targetSquare = getLowestCostOpenSquare();
				if (targetSquare != startSquare) {
					System.out.println("move to open \n (" + targetSquare.getX() + "," + targetSquare.getY() + ")");
					Thread.sleep(0);
					moveToOpenSquare(targetSquare); // also updates current square					
				} else {
					System.out.println("at start square");
					currentSquare = startSquare;
				}
				
				// should be at lowest cost open square
				// close current square by removing from open list and placing in closed list
				openSquares.remove(openSquares.indexOf(currentSquare));
				closedSquares.add(currentSquare);
/*				System.out.println(
					currentSquare.getX() + "," + currentSquare.getY() + "\n" +
					currentSquare.getX() + "," + currentSquare.getY() + "\n" +
					currentSquare.getX() + "," + currentSquare.getY() + "\n" +
					currentSquare.getX() + "," + currentSquare.getY() + "\n" +
					currentSquare.getX() + "," + currentSquare.getY() + "\n" +
					currentSquare.getX() + "," + currentSquare.getY() + "\n" +
					currentSquare.getX() + "," + currentSquare.getY() + "\n" +
					currentSquare.getX() + "," + currentSquare.getY() + "\n"
				);
*/				
				// discover accessible squares via sensors
 				accessibleSquares = getAccessibleSquares();

				// filter out closed accessible Squares
				for (int i = 0; i < accessibleSquares.size(); i++) {
					temp = (Square) accessibleSquares.get(i);
					for (int j = 0; j < closedSquares.size(); j++) {
						temp2 = (Square) closedSquares.get(j);
						if (temp.getX() == temp2.getX() && temp.getY() == temp2.getY()) {
							accessibleSquares.remove(accessibleSquares.indexOf(temp));
						}
					}
				}

				
				// for all open accessible squares...
				for (int i = 0; i < accessibleSquares.size(); i++) {
					temp = (Square) accessibleSquares.get(i);
					for (int j = 0; j < openSquares.size(); j++) {
						temp2 = (Square) openSquares.get(j);
						
						// If the square is on the open list already, check to see if this path to the
						// square is better, using G cost as the measure. A lower G cost means
						// that this is a better path. If so, change the parent of the square to the
						// current square, and recalculate the G and F scores of the square.
						if (temp.getX() == temp2.getX() && temp.getY() == temp2.getY()) {
							if (currentSquare.getG() + 1 <= temp2.getG()) {
								temp2.setG(currentSquare.getG() + 1);
								temp2.setParent(currentSquare); // i.e currentSquare...
								accessibleSquares.remove(accessibleSquares.indexOf(temp));
							}
						} 
					}
				}

				
				// If the square is not on the open list, add it to the open list. Make the
				// current square the parent of this square. Calculate the F, G, and H
				// costs of the square.
				
				for (int i = 0; i < accessibleSquares.size(); i++) {
					temp = (Square) accessibleSquares.get(i);
					openSquares.add(temp);
				}
				
				
			}
			
			
		} catch(InterruptedException ex) { }
	}
	
	private ArrayList<Square> getAccessibleSquares() {
		ArrayList<Square> accessibleSquares = new ArrayList<Square>();
		Square temp;
		int tempX;
		int tempY;
		
		if (state.getLeftReading() > LEFT_BOUND) {
			switch (direction % 4) {
				case 0: // north
					tempX = currentSquare.getX() - 1;
					tempY = currentSquare.getY();
					break;
				case 1: // east
					tempX = currentSquare.getX();
					tempY = currentSquare.getY() + 1;
					break;
				case 2: // south
					tempX = currentSquare.getX() + 1;
					tempY = currentSquare.getY();
					break;
				default: // west
					tempX = currentSquare.getX();
					tempY = currentSquare.getY() - 1;
					break;
			}
			// create currently accessible square, check if already exists in open/closed list
			if (world[tempX][tempY] == null) {
				temp = new Square(tempX, tempY, H(tempX, tempY), currentSquare.getG() + 1, currentSquare);
				world[tempX][tempY] = temp;
				currentSquare.addChild(temp);
			} else {
				temp = world[tempX][tempY];
			}
			accessibleSquares.add(temp);
			
			// if tempX & tempY is equal to any descendant of any adjacent? ancestor 
			
		}
		
		if (state.getCentreReading() < CENTRE_BOUND) { // light sensor
			switch (direction % 4) {
				case 0: // north
					tempX = currentSquare.getX();
					tempY = currentSquare.getY() + 1;
					break;
				case 1: // east
					tempX = currentSquare.getX() + 1;
					tempY = currentSquare.getY();
					break;
				case 2: // south
					tempX = currentSquare.getX();
					tempY = currentSquare.getY() - 1;
					break;
				default: // west
					tempX = currentSquare.getX() - 1;
					tempY = currentSquare.getY();
					break;
			}
			if (world[tempX][tempY] == null) {
				temp = new Square(tempX, tempY, H(tempX, tempY), currentSquare.getG() + 1, currentSquare);
				world[tempX][tempY] = temp;
				currentSquare.addChild(temp);
			} else {
				temp = world[tempX][tempY];
			}
			accessibleSquares.add(temp);
			
		}
		
		if (state.getRightReading() > RIGHT_BOUND) {
			switch (direction % 4) {
				case 0: // north
					tempX = currentSquare.getX() + 1;
					tempY = currentSquare.getY();
					break;
				case 1: // east
					tempX = currentSquare.getX();
					tempY = currentSquare.getY() - 1;
					break;
				case 2: // south
					tempX = currentSquare.getX() - 1;
					tempY = currentSquare.getY();
					break;
				default: // west
					tempX = currentSquare.getX();
					tempY = currentSquare.getY() + 1;
					break;
			}
			if (world[tempX][tempY] == null) {
				temp = new Square(tempX, tempY, H(tempX, tempY), currentSquare.getG() + 1, currentSquare);
				world[tempX][tempY] = temp;
				currentSquare.addChild(temp);
			} else {
				temp = world[tempX][tempY];
			}
			accessibleSquares.add(temp);
		}
		
		return accessibleSquares;
	}
	
	private void moveToOpenSquare(Square target) {
		boolean isAtTargetSquare = false;
		while (!isAtTargetSquare) {
			while (!currentSquare.hasDescendant(target)) {
				//System.out.println("currentSquare doesn't have move target as descendant");
				moveToCloseRelative(currentSquare.getParent());
				currentSquare = currentSquare.getParent();
			}
			Square child;
			for (int i = 0; i < currentSquare.getChildren().size(); i++) {
//				System.out.println(i + " child");
				child = (Square) currentSquare.getChildren().get(i);
				if (child.getX() == target.getX() && child.getY() == target.getY()) { // ??? getx instead
					moveToCloseRelative(child);
					currentSquare = child;
					isAtTargetSquare = true;
				  break;	
				} else if (child.hasDescendant(target)) {
					moveToCloseRelative(child);
					currentSquare = child;
					break;
				}
			}
		}
	}
	
	private void moveToCloseRelative(Square relative) {
		//System.out.println(relative.getX() + " " + relative.getY());
		try {
			Thread.sleep(10);
		} catch (InterruptedException ex) {
			
		}
		if (relative.getX() == currentSquare.getX()) {
			if (relative.getY() > currentSquare.getY()) { // relative is north
				switch (direction % 4) { // robot facing...
					case 0: // north
						forward();
						break;
					case 1: // east
						left();
						break;
					case 2: // south
						backward();
						break;
					case 3: // west
						right();
						break;
				}
			} else { // relative is south
				switch (direction % 4) { // robot facing...
					case 0: // north
						backward();
						break;
					case 1: // east
						right();
						break;
					case 2: // south
						forward();
						break;
					case 3: // west
						left();
						break;					
				}
			}
		} else {
			if (relative.getX() > currentSquare.getX()) { // relative is east
				switch (direction % 4) { // robot facing...
					case 0: // north
						right();
						break;
					case 1: // east
						forward();
						break;
					case 2: // south
						left();
						break;
					case 3: // west
						backward();
						break;
				}
			} else { // relative is west
				switch (direction % 4) { // robot facing..
					case 0: // north
						left();
						break;
					case 1: // east
						backward();
						break;
					case 2: // south
						right();
						break;
					case 3: // west
						forward();
						break;
				}
			}
		}
	}
	
	private void forward() {
		state.charge();
		output.moveForwardOneBlock(control);
	}
	
	private void left() {
		state.turn();
		output.turnLeft();
		updateDirection(3);
		state.charge();
		output.moveForwardOneBlock(control);
	}
	
	private void right() {
		state.turn();
		output.turnRight();
		updateDirection(1);
		state.charge();
		output.moveForwardOneBlock(control);
	}
	
	private void backward() { // via aboutTurn and forwardOneBlock
		state.turn();
		output.aboutTurn();
		updateDirection(2);
		state.charge();
		output.moveForwardOneBlock(control);
	}
	
	public void updateDirection(int input) {
		direction = direction + input;
	}
	
	// Manhattan estimator function H - admissible for non-diagonal grid world
	private int H(int x, int y) {
		return Math.abs(x - goalSquare.getX()) + Math.abs(y - goalSquare.getY());
	}
			
	private Square getLowestCostOpenSquare() {
		
		Square lowest = (Square) openSquares.get(0);
		
		if (openSquares.size() > 1) {
			Square temp;
			for (int i = 0; i < openSquares.size(); i++) {
				temp = (Square) openSquares.get(i);
				if (temp.getF() < lowest.getF()) lowest = temp;
			}
		}
		
		return lowest;
	}	
			
	private boolean isGoalSquareClosed() {
		
		Square temp;
		boolean isGoalSquareClosed = false;
		for (int i = 0; i < closedSquares.size(); i++) {
			temp = (Square) closedSquares.get(i);
			
			if (temp.getX() == goalSquare.getX() && temp.getY() == goalSquare.getY()) {
				isGoalSquareClosed = true;
				break;
			}
		}
		
		return isGoalSquareClosed;
	}		
		
}
