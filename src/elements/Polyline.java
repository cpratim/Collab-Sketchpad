import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 * @author Amy Morissey and Pratim Chowdhary
 */
public class Polyline implements Shape {
	// TODO: YOUR CODE HERE

	ArrayList<Segment> segments;
	Color color;

	// initialize with a new arraylist and color
	public Polyline(ArrayList<Segment> segments, Color color) {
		this.segments = segments;
		this.color = color;
	}

	@Override
	public void moveBy(int dx, int dy) {
		for (Segment seg: segments) {
			seg.moveBy(dx, dy);
		}
	}

	@Override
	public Color getColor() {
		return this.color;
	}

	@Override
	public void setColor(Color color) {
		// go through each segment and change the color
		for (Segment segment: segments) {
			segment.setColor(color);
		}
	}

	@Override
	public boolean contains(int x, int y) {
		// go through every segment and check if any contain the point
		for (Segment seg: segments) {
			if (seg.contains(x, y)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		// loop through and draw all the segments
		for (Segment seg: segments) {
			seg.draw(g);
		}
	}

	@Override
	public String toString() {
		// seperate each segment with a :
		String message = "polyline ";
		for (Segment segment: segments) {
			message += segment + ":";
		}
		return message;
	}
	// add the segment to the arraylist of segments
	public void addSegment(Segment segment) {
		segments.add(segment);
	}
}