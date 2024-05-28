package Model;

import java.awt.Rectangle;

public class Chimney extends Objects{

	private Rectangle rect;

	private boolean isBehindBird = false;
	private int offset;
	private int realOffset;

	public int getRealOffset() {
		return realOffset;
	}

	public void setRealOffset(int realOffset) {
		this.realOffset = realOffset;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public Chimney(int x, int y, int w, int h) {
		super(x, y, w, h);

		rect = new Rectangle(x, y, w, h);
		offset = 2;
	}

	public void update() {

		this.setPosX(this.getPosX() - offset);
		this.rect.setLocation((int) this.getPosX(), (int) this.getPosY());

	}

	public Rectangle getRect() {
		return rect;
	}

	public void setIsBehindBird(boolean b) {
		isBehindBird = b;
	}

	public boolean getIsBehindBird() {
		return isBehindBird;
	}

}