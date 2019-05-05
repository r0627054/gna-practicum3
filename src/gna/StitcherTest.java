package gna;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import libpract.Position;
import libpract.Stitch;

public class StitcherTest {

	
	//Only floodFill tests.
	//The other methods need and image input
	@Test
	public void floodFillTest1() {
		Stitch[][] input = {{Stitch.SEAM,Stitch.EMPTY}, {Stitch.EMPTY, Stitch.SEAM}};
		Stitch[][] expectedOutput = {{Stitch.SEAM,Stitch.IMAGE2}, {Stitch.IMAGE1, Stitch.SEAM}};
		new Stitcher().floodfill(input);
		
		for (int i = 0; i < expectedOutput.length; i++) {
			for (int j = 0; j < expectedOutput[0].length; j++) {
				assertEquals(expectedOutput[i][j], input[i][j]);
			}
		}
	}
	
	@Test
	public void floodFillTest2() {
		Stitch[][] input = {{Stitch.SEAM,Stitch.EMPTY,Stitch.EMPTY}, {Stitch.SEAM,Stitch.EMPTY,Stitch.EMPTY},{Stitch.EMPTY,Stitch.SEAM,Stitch.EMPTY},{Stitch.EMPTY,Stitch.EMPTY,Stitch.SEAM}};
		Stitch[][] expectedOutput =  {{Stitch.SEAM,Stitch.IMAGE2,Stitch.IMAGE2}, {Stitch.SEAM,Stitch.IMAGE2,Stitch.IMAGE2},{Stitch.IMAGE1,Stitch.SEAM,Stitch.IMAGE2},{Stitch.IMAGE1,Stitch.IMAGE1,Stitch.SEAM}};
		new Stitcher().floodfill(input);
		
		for (int i = 0; i < expectedOutput.length; i++) {
			for (int j = 0; j < expectedOutput[0].length; j++) {
				assertEquals(expectedOutput[i][j], input[i][j]);
			}
		}
	}

}
