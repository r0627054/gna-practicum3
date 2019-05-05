package gna;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import libpract.*;

/**
 * Implement the methods stitch, seam and floodfill.
 */
public class Stitcher
{
	
	private PriorityQueue<Position> positionPQ;
	private int width;
	private int height;
	private double[][] distTo;
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
	 *   //we krijgen al het overlappende gedeelte. Deze grens is altijd van de linker bovenhoek naar de rechter onderhoek.
	 *   //Dijkstra
	 *   //diagonaal
	 *   
	 */
	public List<Position> seam(int[][] image1, int[][] image2) {
		this.setWidth(image1.length);
		this.setHeight(image1[0].length);
		
		this.setPositionPQ(new PriorityQueue<Position>());
		this.setDistTo(new double[this.getWidth()][this.getHeight()]);
		this.setPreviousVertex(new Position[this.getWidth()][this.getHeight()]);
		
		this.performDijkstra(image1, image2);
		
		
		
		return null;
	}
	
	public void performDijkstra(int[][] image1, int[][] image2) {
		// all nodes except the start node are initialised to zero.
		for (int w = 0; w < this.getWidth(); w++) {
			for (int h = 0; h < this.getHeight(); h++) {
				this.setDistoValue(w, h, Double.POSITIVE_INFINITY);
			}
		}
		this.setDistoValue(0, 0, 0);

		Position currentPosition = new Position(0, 0);
		this.getPositionPQ().add(currentPosition);
		while (!currentPosition.equals(this.getTargetPosition())) {
			for (Position neighbor : this.getPossibleNeighbors(currentPosition)) {
				double calcDistance = this.getDistTo()[currentPosition.getX()][currentPosition.getY()]
						+ ImageCompositor.pixelSqDistance(image1[neighbor.getX()][neighbor.getY()],
								image2[neighbor.getX()][neighbor.getY()]);
				if (calcDistance < this.getDistTo()[neighbor.getX()][neighbor.getY()]) {
					this.setDistoValue(neighbor.getX(), neighbor.getY(), calcDistance);
					this.setPreviousVertexValue(neighbor.getX(), neighbor.getY(), currentPosition);
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
		throw new RuntimeException("not implemented yet");
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
		// use seam and floodfill to implement this method
		throw new RuntimeException("not implemented yet");
	}

	
	private void setPositionPQ(PriorityQueue<Position> positionPQ) {
		if(positionPQ == null) {
			throw new IllegalArgumentException("The positionPQ cannot be null.");
		}
		this.positionPQ = positionPQ;
	}
	
	public PriorityQueue<Position> getPositionPQ() {
		return positionPQ;
	}

	public int getWidth() {
		return width;
	}

	private void setWidth(int width) {
		if(width < 0) {
			throw new IllegalArgumentException("Width of an image cannot be negative.");
		}
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	private void setHeight(int height) {
		if(height < 0) {
			throw new IllegalArgumentException("The height of an image cannot be negative.");
		}
		this.height = height;
	}

	public double[][] getDistTo() {
		return distTo;
	}

	private void setDistTo(double[][] distTo) {
		if(distTo == null) {
			throw new IllegalArgumentException("Disto cannot be set to null.");
		}
		this.distTo = distTo;
	}

	public Position[][] getPreviousVertex() {
		return previousVertex;
	}

	private void setPreviousVertex(Position[][] previousVertex) {
		if(previousVertex == null) {
			throw new IllegalArgumentException("EdgeTo cannot be set to null.");
		}
		this.previousVertex = previousVertex;
	}
	
	private void setDistoValue(int x, int y, double value) {
		if(x <0 || y <0) {
			throw new IllegalArgumentException("The positions of disto array cannot be null.");
		}
		distTo[x][y] = value;
	}
	
	private void setPreviousVertexValue(int x, int y, Position previousPosition) {
		if((x <0) || (y<0) || (previousPosition== null)) {
			throw new IllegalArgumentException("Cannot set the previousVertex value because it cannot be null and cannot be outside the grid.");
		}
		previousVertex[x][y] = previousPosition;
	}
	
	
	private Position getTargetPosition() {
		return new Position(this.getWidth()-1, this.getHeight()-1);
	}
	
	private List<Position> getPossibleNeighbors(Position p){
		List<Position> neighbors = new ArrayList<>();
		//left
		if(  (p.getX()-1) >= 0 ) {
			neighbors.add(new Position(p.getX() -1, p.getY()));
		}
		//right
		if( (p.getX()+1) < this.getWidth()  ) {
			neighbors.add(new Position(p.getX() +1, p.getY()));
		}
		//top
		if(  (p.getY()-1) >= 0 ) {
			neighbors.add(new Position(p.getX(), p.getY()-1));
		}
		//bottom
		if( (p.getY()+1) < this.getHeight()  ) {
			neighbors.add(new Position(p.getX(), p.getY()+1));
		}
		
		//left + top
		if( ( (p.getX()-1) >= 0 ) &&  ((p.getY()-1) >= 0 ) ) {
			neighbors.add(new Position(p.getX()-1, p.getY()-1));
		}
		//left + bottom
		if( ( (p.getX()-1) >= 0 ) && ((p.getY()+1) < this.getHeight()) ) {
			neighbors.add(new Position(p.getX()-1, p.getY()+1));
		}
		//right + top
		if( ((p.getX()+1) < this.getWidth()) && ((p.getY()-1) >= 0 )  ) {
			neighbors.add(new Position(p.getX()+1, p.getY()-1));
		}
		//right + bottom
		if( ((p.getX()+1) < this.getWidth())  && ((p.getY()+1) < this.getHeight())) {
			neighbors.add(new Position(p.getX()+1, p.getY()+1));
		}
		return neighbors;
	}
		
}


