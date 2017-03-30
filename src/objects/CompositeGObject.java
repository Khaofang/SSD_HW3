package objects;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class CompositeGObject extends GObject {

	private List<GObject> gObjects;

	public CompositeGObject() {
		super(0, 0, 0, 0);
		gObjects = new ArrayList<GObject>();
	}

	public void add(GObject gObject) {
		gObjects.add(gObject);
	}

	public void remove(GObject gObject) {
		gObjects.remove(gObject);
	}

	@Override
	public void move(int dX, int dY) {
		x += dX;
		y += dY;
		for (GObject gObject : gObjects) {
			gObject.move(dX, dY);
		}
	}
	
	public void recalculateRegion() {
		int xMin = 0, xMax = 0, yMin = 0, yMax = 0;
		for (int i = 0; i < gObjects.size(); i++) {
			if (i == 0) {
				xMin = gObjects.get(i).x;
				yMin = gObjects.get(i).y;
				xMax = gObjects.get(i).x + gObjects.get(i).width;
				yMax = gObjects.get(i).y + gObjects.get(i).height;
			} else {
				if (xMin > gObjects.get(i).x)
					xMin = gObjects.get(i).x;
				if (yMin > gObjects.get(i).y)
					yMin = gObjects.get(i).y;
				if (xMax < gObjects.get(i).x + gObjects.get(i).width)
					xMax = gObjects.get(i).x + gObjects.get(i).width;
				if (yMax < gObjects.get(i).y + gObjects.get(i).height)
					yMax = gObjects.get(i).y + gObjects.get(i).height;
			}
			x = xMin;
			y = yMin;
			width = xMax - xMin;
			height = yMax - yMin;
		}
	}

	@Override
	public void paintObject(Graphics g) {
		for (GObject gObject : gObjects) {
			gObject.paintObject(g);
		}
	}

	@Override
	public void paintLabel(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawString("Grouped", x, y + height + 20);
	}
	
}
