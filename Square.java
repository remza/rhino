import java.util.ArrayList;

public class Square {
	private ArrayList<Square> children;
	private ArrayList<Square> accessibleSquares;
	private Square parent;
	private int x, y;
	private int H, G, F;
	
	public Square(int x, int y, int H, int G, Square parent) {
		this.x = x;
		this.y = y;
		this.H = H;
		this.G = G;
		F = G + H;
		this.parent = parent;
		children = new ArrayList<Square>();
		accessibleSquares = new ArrayList<Square>();
	}
	
	public boolean hasDescendant(Square target) {
		boolean hasDescendant = false;
		Square child;
		for (int i = 0; i < children.size(); i++) {
			child = (Square)children.get(i);
			//System.out.println(i + "\ntX:" +target.getX() +" cX:"+ child.getX() +"\ntY:"+ target.getY() +" cY:"+ child.getY());
			if (target.getX() == child.getX() && target.getY() == child.getY()) {
				hasDescendant = true;
				break;
			}	else {
				hasDescendant = child.hasDescendant(target);
				if (hasDescendant) break;
			}
		}
		if (hasDescendant) System.out.println("has descendant: (" + target.getX() +  "," + target.getY() + ")");
		return hasDescendant;
	}

	public void addChild(Square child) {
		children.add(child);
	}
	
	ArrayList<Square> getChildren() {
		return children;
	}
	
	public void addAccessibleSquare(Square square) {
		accessibleSquares.add(square);
	}
	
	public ArrayList<Square> getAccessibleSquares() {
		return accessibleSquares;
	}
	
	public Square getParent() {
		return parent;
	}

	public void setParent(Square parent) {
		this.parent = parent;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

	public int getG() {
		return G;
	}
	
	public void setG(int G) {
		this.G = G;
		F = G + H;
	}
	
	public int getF() {
		return F;
	}
}