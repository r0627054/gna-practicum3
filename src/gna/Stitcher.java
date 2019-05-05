package gna;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Stack;

import libpract.*;

/**
 * Implement the methods stitch, seam and floodfill.
 */
public class Stitcher
{
	/**
	 * Variable storing the positions used by Dijkstra to find the shortest path.
	 */
	private PriorityQueue<Position> positionPQ;
	
	/**
	 * Variable storing the width of the image.
	 */
	private int width;
	
	/**
	 * Variable storing the height of the image.
	 */
	private int height;
	
	/**
	 * Variable storing the currently shortest distances to the vertices (used by Dijkstra).
	 */
	private double[][] distTo;
	
	/**
	 * Variable storing the 2D array of positions. For every already calculated vertex there is a previous vertex to reach the one on the position. 
	 */
	private Position[][] previousVertex;
	
	/**
	 * Return the sequence of positions on the seam. The first position in the
	 * sequence is (0, 0) and the last is (width - 1, height - 1). Each position
	 * on the seam must be adjacent to its predecessor and successor (if any).
	 * Positions that are diagonally adjacent are considered adjacent.
	 * 
	 * image1 and image2 are both non-null and have equal dimensions.
	 *
	 * Remark: Here we use the default computer graphics coordinate system,
	 *   illustrated in the following image:
	 * 
	 *        +-------------> X
	 *        |  +---+---+
	 *        |  | A | B |
	 *        |  +---+---+
	 *        |  | C | D |
	 *        |  +---+---+
	 *      Y v 
	 * 
	 *   The historical reasons behind using this layout is explained on the following
	 *   website: http://programarcadegames.com/index.php?chapter=introduction_to_graphics
	 * 
	 *   Position (y, x) corresponds to the pixels image1[y][x] and image2[y][x]. This
	 *   convention also means that, when an automated test mentioned that it used the array
	 *   {{A,B},{C,D}} as a test image, this corresponds to the image layout as shown in
	 *   the illustration above.
	 *      
	 */
	public List<Position> seam(int[][] image1, int[][] image2) {
		this.setHeight(image1.length);
		this.setWidth(image1[0].length);
		
		this.setPositionPQ(new PriorityQueue<Position>(this.getPositionComparator()));
		this.setDistTo(new double[this.getHeight()][this.getWidth()]);
		this.setPreviousVertex(new Position[this.getHeight()][this.getWidth()]);
		
		this.performDijkstra(image1, image2);
		
		return this.getShortestPathSolution();
	}
	
	/**
	 * Performs the Dijkstra algorithm to calculate the shortest path from the upper left corner to the bottom right corner.
	 * @param image1 The values of image 1.
	 * @param image2 The values of image 2.
	 */
	private void performDijkstra(int[][] image1, int[][] image2) {
		// all nodes except the start node are initialised to zero.
		for (int h = 0; h < this.getHeight(); h++) {
			for (int w = 0; w < this.getWidth(); w++) {
				this.setDistoValue(h, w, Double.POSITIVE_INFINITY);
			}
		}
		this.setDistoValue(0, 0, 0);

		//Dijkstra calculation and stops when the shortest path to the destination is reached
		Position currentPosition = new Position(0, 0);
		this.getPositionPQ().add(currentPosition);
		while (!currentPosition.equals(this.getTargetPosition())) {
			for (Position neighbor : this.getPossibleNeighbors(currentPosition)) {
				double calcDistance = this.getDistTo()[currentPosition.getY()][currentPosition.getX()]
						+ ImageCompositor.pixelSqDistance(image1[neighbor.getY()][neighbor.getX()],
								image2[neighbor.getY()][neighbor.getX()]);
				if (calcDistance < this.getDistTo()[neighbor.getY()][neighbor.getX()]) {
					this.setDistoValue(neighbor.getY(), neighbor.getX(), calcDistance);
					this.setPreviousVertexValue(neighbor.getY(), neighbor.getX(), currentPosition);
					if (this.getPositionPQ().contains(neighbor)) {
						this.getPositionPQ().remove(neighbor);
					}
					this.getPositionPQ().add(neighbor);
				}
			}
			currentPosition = this.getPositionPQ().poll();
		}
	}
	
	/**
	 * Calculates the shortest path out of the previousVertex 2D array.
	 * @return The shortest path from top left to bottom right.
	 */
	private List<Position> getShortestPathSolution(){
		if(!hasPathTo(this.getTargetPosition())) return null;
		//calculate shortest path starting from the target
		Stack<Position> reversePath = new Stack<Position>();
		Position current = this.getTargetPosition();
		reversePath.add(current);
		while(this.getPreviousVertex()[current.getY()][current.getX()] != null) {
			current = this.getPreviousVertex()[current.getY()][current.getX()];
			reversePath.add(current);
		}
		//reverse the list again
		List<Position> shortestPath = new ArrayList<>();
		while(!reversePath.isEmpty()) {
			shortestPath.add(reversePath.pop());
		}
		return shortestPath;	
	}

	/**
	 * Apply the floodfill algorithm described in the assignment to mask. You can assume the mask
	 * contains a seam from the upper left corner to the bottom right corner. The seam is represented
	 * using Stitch.SEAM and all other positions contain the default value Stitch.EMPTY. So your
	 * algorithm must replace all Stitch.EMPTY values with either Stitch.IMAGE1 or Stitch.IMAGE2.
	 *
	 * Positions left to the seam should contain Stitch.IMAGE1, and those right to the seam
	 * should contain Stitch.IMAGE2. You can run `ant test` for a basic (but not complete) test
	 * to check whether your implementation does this properly.
	 */
	public void floodfill(Stitch[][] mask) {
		//start by filling Image1, the bottom left corner is the starting position.
		//if this one is not colored, the others aren't either.
		this.floodFill(mask, mask.length-1, 0, Stitch.IMAGE1);
		//start by filling Image2, the top right corner is the starting position.
		//if this one is not colored, the others aren't either.
		this.floodFill(mask, 0, mask[0].length-1, Stitch.IMAGE2);
	}

	/**
	 * Fills the mask with the given type on the given coordinates. It does not cross the seam.
	 * @param mask The mask with a given seam.
	 * @param y    The starting y-position.
	 * @param x    The starting x-position.
	 * @param type The Stich type to which it will be set.
	 */
	private void floodFill(Stitch[][] mask, int y, int x, Stitch type) {
		//use of stack because we need to go depth first
		Stack<Position> nextColoring = new Stack<>();
		nextColoring.add(new Position(y, x));
		while(!nextColoring.isEmpty()) {
			Position currentPosition = nextColoring.pop();
			Stitch currentStitch = mask[currentPosition.getY()][currentPosition.getX()];
			if(currentStitch == Stitch.EMPTY) {
				mask[currentPosition.getY()][currentPosition.getX()] = type;
				nextColoring.addAll(this.getBasicNeighbors(currentPosition,mask[0].length,mask.length));
			}
		}
	}
	
	
	/**
	 * Return the mask to stitch two images together. The seam runs from the upper
	 * left to the lower right corner, where in general the rightmost part comes from
	 * the second image (but remember that the seam can be complex, see the spiral example
	 * in the assignment). A pixel in the mask is Stitch.IMAGE1 on the places where
	 * image1 should be used, and Stitch.IMAGE2 where image2 should be used. On the seam
	 * record a value of Stitch.SEAM.
	 * 
	 * ImageCompositor will only call this method (not seam and floodfill) to
	 * stitch two images.
	 * 
	 * image1 and image2 are both non-null and have equal dimensions.
	 */
	public Stitch[][] stitch(int[][] image1, int[][] image2) {
		Stitch[][] mask = new Stitch[image1.length][image1[0].length];
		for (int i = 0; i < mask.length; i++) {
			for (int j = 0; j < mask[0].length; j++) {
				mask[i][j] = Stitch.EMPTY;
			}
		}
		for (Position position : this.seam(image1, image2)) {
			mask[position.getY()][position.getX()] = Stitch.SEAM;
		}
		this.floodfill(mask);		
		return mask;
	}

	/**
	 * Sets the priorityQue of positions.
	 * @param positionPQ The position to which it will be set.
	 * @throws IllegalArgumentException when the positionPQ parameter equals null
	 *                                  | positionPQ == null
	 * @post The positionPQ is set to the given parameter
	 *       | new.getPositionPQ() == positionPQ
	 */
	private void setPositionPQ(PriorityQueue<Position> positionPQ) {
		if(positionPQ == null) {
			throw new IllegalArgumentException("The positionPQ cannot be null.");
		}
		this.positionPQ = positionPQ;
	}
	
	/**
	 * Returns the priorityQueue of positions.
	 * @return The priorityyQueue of positions.
	 */
	public PriorityQueue<Position> getPositionPQ() {
		return positionPQ;
	}

	/**
	 * Returns the width of the image (2D array).
	 * @return The width of the image (2D array).
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the width of the image.
	 * @param width The width of the image.
	 * @throws IllegalArgumentException when the width equals a negative value
	 *                                 | width < 0
	 * @post the width equals the given parameter.
	 *      | new.getWidth() == width
	 */
	private void setWidth(int width) {
		if(width < 0) {
			throw new IllegalArgumentException("Width of an image cannot be negative.");
		}
		this.width = width;
	}

	/**
	 * Returns the height of the image (2D array).
	 * @return The height of the image (2D array).
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Sets the height of the image.
	 * @param height The height of the image.
	 * @throws IllegalArgumentException when the height equals a negative value
	 *                                 | width < 0
	 * @post the height equals the given parameter.
	 *      | new.getHeight() == height
	 */
	private void setHeight(int height) {
		if(height < 0) {
			throw new IllegalArgumentException("The height of an image cannot be negative.");
		}
		this.height = height;
	}

	/**
	 * Returns the Distance to (a certain vertex on the location) 2D matrix.
	 * @return the Distance to (a certain vertex on the location) 2D matrix.
	 */
	public double[][] getDistTo() {
		return distTo;
	}

	/**
	 * Sets the distanceTo array.
	 * @param distTo the 2D array of double to which the array will be set.
	 * @throws IllegalArgumentException when the distTo equals null
	 *                                  |distTo == null
	 * @post the distTo array equals the given parameter
	 *       | new.getDistTo() == distTo
	 */
	private void setDistTo(double[][] distTo) {
		if(distTo == null) {
			throw new IllegalArgumentException("Disto cannot be set to null.");
		}
		this.distTo = distTo;
	}

	/**
	 * Returns the previous vertex 2D array of positions.
	 * @return the previous vertex 2D array of positions.
	 */
	public Position[][] getPreviousVertex() {
		return previousVertex;
	}

	/**
	 * Sets the previousVertex 2D array.
	 * @param previousVertex The previousVertex to which it will be set.
	 * @throws IllegalArgumentException when the previousVertex equals null
	 *                                  | previousVertex == null
	 * @post The previousVertex variable is set to the given parameter.
	 *       | new.getPreviousVertex() == previousVertex
	 */
	private void setPreviousVertex(Position[][] previousVertex) {
		if(previousVertex == null) {
			throw new IllegalArgumentException("EdgeTo cannot be set to null.");
		}
		this.previousVertex = previousVertex;
	}
	
	/**
	 * Sets the distance to value of 1 particular element in the 2D array.
	 * @param y The y-coordinate.
	 * @param x The x-coordinate.
	 * @param value The value to which it will be set.
	 * @throws IllegalArgumentException when at least one of the coordinates is negative.
	 *                                  | x <0 || y <0
	 * @post The distance is set to the given value.
	 *       | new.getDistance()[y][x] == value
	 */
	private void setDistoValue(int y, int x, double value) {
		if(x <0 || y <0) {
			throw new IllegalArgumentException("The positions of disto array cannot be null.");
		}
		distTo[y][x] = value;
	}
	
	/**
	 * Sets the previousVertexValue to the given value.
	 * @param y The y-coordinate.
	 * @param x The x-coordinate.
	 * @param previousPosition The value to of the previous position to which it will be set.
	 * @throws IllegalArgumentException when at least one of the coordinates is below zero or the previousPosition equals null.
	 *                                  | (x <0) || (y<0) || (previousPosition== null)
	 * @post The previousPosition is set on the given coordinates.
	 *        | new.getPreviousVertex()[y][x] == previousPosition
	 */
	private void setPreviousVertexValue(int y, int x, Position previousPosition) {
		if((x <0) || (y<0) || (previousPosition== null)) {
			throw new IllegalArgumentException("Cannot set the previousVertex value because it cannot be null and cannot be outside the grid.");
		}
		previousVertex[y][x] = previousPosition;
	}
		
	/**
	 * Returns the targetPosition. The targetPosition is the final position of the Dijkstra algorithm.
	 * @return The targetPosition (used for Dijkstra's algorithm).
	 */
	private Position getTargetPosition() {
		return new Position(this.getHeight()-1, this.getWidth()-1);
	}
	
	/**
	 * returns whether or not there exists a path to the vertex on the given position. 
	 * @param p The position the vertex is located.
	 * @return True when there exists a path to the vertex; otherwise false.
	 */
	private boolean hasPathTo(Position p) {
		if(p == null) {
			throw new IllegalArgumentException("Path cannot be null to determine if a path exists.");
		}
		return this.getDistTo()[p.getY()][p.getX()] < Double.POSITIVE_INFINITY;
	}
	
	/**
	 * Returns all the possible neighbors of a vertex. (Used in Dijkstra's algorithm).
	 * @param p The position of which the neighbors should be calculated.
	 * @return a List of neighbors of the given position. A neighbor itself is also a position.
	 */
	public List<Position> getPossibleNeighbors(Position p){
		List<Position> neighbors = new ArrayList<>();
		neighbors.addAll(this.getBasicNeighbors(p,this.getWidth(),this.getHeight()));
		
		//left + top
		if( ( (p.getX()-1) >= 0 ) &&  ((p.getY()-1) >= 0 ) ) {
			neighbors.add(new Position(p.getY()-1, p.getX()-1));
		}
		//left + bottom
		if( ( (p.getX()-1) >= 0 ) && ((p.getY()+1) < this.getHeight()) ) {
			neighbors.add(new Position(p.getY()+1, p.getX()-1));
		}
		//right + top
		if( ((p.getX()+1) < this.getWidth()) && ((p.getY()-1) >= 0 )  ) {
			neighbors.add(new Position(p.getY()-1, p.getX()+1));
		}
		//right + bottom
		if( ((p.getX()+1) < this.getWidth())  && ((p.getY()+1) < this.getHeight())) {
			neighbors.add(new Position(p.getY()+1, p.getX()+1));
		}
		return neighbors;
	}
	
	/**
	 * Returns the basic neighbors of a position in a field with given width and height.
	 * A basic neighbor is left, right, top, bottom
	 * @param p The position of which the neighbors need to be calculated.
	 * @param width The width of the field.
	 * @param height The height of the field.
	 * @return The list of neighbors of the given position.
	 */
	private List<Position> getBasicNeighbors(Position p, int width, int height) {
		List<Position> neighbors = new ArrayList<>();
		// left
		if ((p.getX() - 1) >= 0) {
			neighbors.add(new Position(p.getY(), p.getX() - 1));
		}
		// right
		if ((p.getX() + 1) < width) {
			neighbors.add(new Position(p.getY(), p.getX() + 1));
		}
		// top
		if ((p.getY() - 1) >= 0) {
			neighbors.add(new Position(p.getY() - 1, p.getX()));
		}
		// bottom
		if ((p.getY() + 1) < height) {
			neighbors.add(new Position(p.getY() + 1, p.getX()));
		}
		return neighbors;
	}
	
	/**
	 * Returns the comparator used to compare Position objects.
	 * @return The comparator used to compare Position objects.
	 */
	private Comparator<Position> getPositionComparator() {
		return new Comparator<Position>() {
			@Override
			public int compare(Position position1, Position position2) {
				if(position1 == null || position2 == null) {
					return 0;
				}
				if(getDistTo()[position1.getY()][position1.getX()] < getDistTo()[position2.getY()][position2.getX()]) {
					return -1;
				} else if(getDistTo()[position1.getY()][position1.getX()] > getDistTo()[position2.getY()][position2.getX()]) {
					return 1;
				}else {
					return 0;
				}
			}
		};
	}
		
}


