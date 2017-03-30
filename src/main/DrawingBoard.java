package main;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import objects.*;

public class DrawingBoard extends JPanel {

	private MouseAdapter mouseAdapter; 
	private List<GObject> gObjects;
	private GObject target;
	
	private int gridSize = 10;
	
	public DrawingBoard() {
		gObjects = new ArrayList<GObject>();
		mouseAdapter = new MAdapter();
		addMouseListener(mouseAdapter);
		addMouseMotionListener(mouseAdapter);
		setPreferredSize(new Dimension(800, 600));
	}
	
	public void addGObject(GObject gObject) {
		gObjects.add(gObject);
		repaint();
	}
	
	public void groupAll() {
		CompositeGObject group = new CompositeGObject();
		for (GObject gObject : gObjects) {
			group.add(gObject);
		}
		group.recalculateRegion();
		gObjects.clear();
		gObjects.add(group);
		repaint();
	}

	public void deleteSelected() {
		if (target != null) {
			gObjects.remove(target);
			repaint();
		}
	}
	
	public void clear() {
		gObjects.clear();
		target = null;
		repaint();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		paintBackground(g);
		paintGrids(g);
		paintObjects(g);
	}

	private void paintBackground(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	private void paintGrids(Graphics g) {
		g.setColor(Color.lightGray);
		int gridCountX = getWidth() / gridSize;
		int gridCountY = getHeight() / gridSize;
		for (int i = 0; i < gridCountX; i++) {
			g.drawLine(gridSize * i, 0, gridSize * i, getHeight());
		}
		for (int i = 0; i < gridCountY; i++) {
			g.drawLine(0, gridSize * i, getWidth(), gridSize * i);
		}
	}

	private void paintObjects(Graphics g) {
		for (GObject gObject : gObjects) {
			gObject.paint(g);
		}
	}

	class MAdapter extends MouseAdapter {

		int eX, eY;
		
		private void deselectAll() {
			for (GObject gObject : gObjects) {
				gObject.deselected();
			}
			target = null;
			repaint();
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			eX = e.getX();
			eY = e.getY();
			deselectAll();
			target = null;
			for (int i = gObjects.size() - 1; i >= 0; i--) {
				GObject gObject = gObjects.get(i);
				if (gObject.pointerHit(eX, eY)) {
					gObject.selected();
					target = gObject;
					repaint();
					break;
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (target != null) {
				int newX = e.getX();
				int newY = e.getY();
				target.move(newX - eX, newY - eY);
				eX = newX;
				eY = newY;
				repaint();
			}
		}
	}
	
}