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
	private List<GObject> target;
	
	private int gridSize = 10;
	
	public DrawingBoard() {
		gObjects = new ArrayList<GObject>();
		target = new ArrayList<GObject>();
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
		clear();
		gObjects.add(group);
		repaint();
	}

	public void deleteSelected() {
		for (GObject gObject : target) {
			gObjects.remove(gObject);
		}
		repaint();
	}
	
	public void clear() {
		gObjects.clear();
		target.clear();
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

		boolean atGO = false, dragging = false, moveObject = false;
		int eX, eY;
		
		private void deselectAll() {
			for (GObject gObject : gObjects) {
				gObject.deselected();
			}
			target.clear();
			repaint();
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			eX = e.getX();
			eY = e.getY();
			atGO = false;
			
			for (GObject gObject : target) {
				if (gObject.pointerHit(eX, eY)) {
					atGO = true;
					break;
				}
			}
			
			for (int i = gObjects.size() - 1; !atGO && i >= 0; i--) {
				GObject gObject = gObjects.get(i);
				if (gObject.pointerHit(eX, eY)) {
					atGO = true;
					deselectAll();
					target.clear();
					gObject.selected();
					target.add(gObject);
					repaint();
					atGO = true;
					break;
				}
			}
			
			if (!atGO) {
				deselectAll();
				target.clear();
				repaint();
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			dragging = true;
			if (atGO) {
				moveObject = true;
				int newX = e.getX();
				int newY = e.getY();
				for (GObject gObject : target) {
					gObject.move(newX - eX, newY - eY);
				}
				eX = newX;
				eY = newY;
				repaint();
			} else {
				moveObject = false;
				int currX = e.getX();
				int currY = e.getY();
				
				repaint();
				Graphics g = getGraphics();
				g.setColor(Color.BLUE);
				
				Graphics2D g2d = (Graphics2D) g;
				g2d.setStroke(new BasicStroke(2));
				
				if (eX <= currX && eY <= currY)
					g2d.drawRect(eX, eY, currX - eX, currY - eY);
				else if (eX > currX && eY <= currY)
					g2d.drawRect(currX, eY, eX - currX, currY - eY);
				else if (eX > currX && eY <= currY)
					g2d.drawRect(eX, currY, currX - eX, eY - currY);
				else
					g2d.drawRect(currX, currY, eX - currX, eY - currY);
			}
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {
			if (dragging && !moveObject) {
				deselectAll();
				target.clear();
				int lastX = e.getX();
				int lastY = e.getY();
				
				for (GObject gObject : gObjects) {
					if (gObject.covered(eX, eY, lastX, lastY)) {
						target.add(gObject);
						gObject.selected();
					}
				}
				repaint();
			}
		}
	}
	
}