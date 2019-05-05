package gna;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import libpract.Position;

public class StitcherTest {

	@Test
	public void driesTest() {
		Stitcher s = new Stitcher();
		s.setWidth(50);
		s.setHeight(80);
		Position start = new Position(0, 49);
		System.out.print("Staaaart ");
		sysoutPosition(start);
		for(Position p: s.getPossibleNeighbors(start)) {
			sysoutPosition(p);
		}
	}
	
	public void sysoutPosition(Position p) {
		System.out.println("Position: X="+p.getX()+"    Y="+p.getY());
	}
	
	

}
