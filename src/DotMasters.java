import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

import java.awt.Cursor;

public class DotMasters extends JFrame implements MouseListener,
		MouseMotionListener {

	private boolean clicked;
	private Image dbImage;
	private Graphics dbg;
	private int fps = 30;
	private int time = 5000;
	private int targetSize = 50;
	private double lasttime;
	private int circlesize = 10;
	private int currFrame, currPoint;
	private Cursor blankCursor;

	private BufferedImage image;
	private Graphics2D buffer;
	private Point target;
	
	private boolean gameover;
	
	private Point MousePosition;

	private ArrayList<Point[]> circles = new ArrayList<Point[]>();

	public DotMasters() {
		 this.setExtendedState(JFrame.MAXIMIZED_BOTH);

//		this.setSize(300, 300);
		this.setUndecorated(true);
		this.setTitle(this.getClass().getName());
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

		blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
		    cursorImg, new Point(0, 0), "blank cursor");
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		circles.add(new Point[time*fps/1000]);

		this.pack();
		setVisible(true);
		image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		buffer = image.createGraphics();
		
		Timer t = new Timer("DotMasters");
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				GameLoop();

			}

		}, 0, 1000 / fps);
	}

	public void GameLoop() {
		if (MousePosition == null || gameover) {
			return;
		}
		
		if (Collision()){
			gameover = true;
			return;
		}
		
		circles.get(currPoint)[currFrame + 1] = new Point(MousePosition);

		if (new Rectangle(target.x, target.y, targetSize, targetSize).contains(circles.get(currPoint)[currFrame])){
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			currFrame = 0;
			circles.add(new Point[time*fps/1000]);
			currPoint++;
			MousePosition = null;
		} else if (currFrame == time*fps/1000 - 2){
			gameover = true;
		}else {
			currFrame++;
		}
	}

	private boolean Collision() {
		if (currPoint == 0){
			return false;
		}
		
		Point p1 = circles.get(currPoint)[currFrame];
		for (int i=0; i<currPoint; i++){
			Point p2 = circles.get(i)[currFrame];
			if (p2 == null){
				continue;
			}
			if (p1.distance(p2) < 2*circlesize){
				return true;
			}
		}
		return false;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;

		
		g2d.setColor(Color.ORANGE);
		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g2d.setColor(Color.black);
		for (int i = 0; i <= currPoint; i++) {
			if (i == currPoint && currFrame == 0) {
				continue;
			}
			Point p = circles.get(i)[currFrame == 0 ? 0 : currFrame];
			if (p == null){
				continue;
			}
			g2d.fillOval(p.x - circlesize, p.y - circlesize, circlesize * 2,
					circlesize * 2);
		}

		if (target != null && MousePosition != null){
			g2d.setColor(new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), 255));
			g2d.fillRoundRect(target.x, target.y, targetSize, targetSize, 1, 1);
		}
		
		if (gameover){
			g2d.setColor(Color.RED);
			g2d.setFont(new Font("newfont", Font.BOLD, 100));
			g2d.drawString("GAME OVER", 200, 200);
		}
		
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		if (MousePosition == null){
			this.setCursor(blankCursor);
			MousePosition = arg0.getPoint();
			target = new Point((int)(Math.random()*getWidth()), (int)(Math.random()*getHeight()));
			int xdist = target.x < getWidth()-target.x ? target.x : getWidth()-target.x;
			int ydist = target.y < getHeight()-target.y ? target.y : getHeight()-target.y;
			
			if (ydist<xdist){
				if (target.y > getHeight()/2){
					target.y = getHeight()-targetSize;
				}else{
					target.y = 0;
				}
			}else{
				if (target.x > getWidth()/2){
					target.x = getWidth()-1-targetSize;
				}else{
					target.x = 0;
				}
			}
			
			circles.get(currPoint)[0] = MousePosition;
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if (MousePosition != null){
			gameover = true;
		}

	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (MousePosition != null){
			MousePosition = arg0.getPoint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

}
