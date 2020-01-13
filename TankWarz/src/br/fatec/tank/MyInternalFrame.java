package br.fatec.tank;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JInternalFrame;

public @SuppressWarnings("serial")
class MyInternalFrame extends JInternalFrame{
	private AlphaComposite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f);
  
	public AlphaComposite getComp() {
		return comp;
	}
	public void setComp(AlphaComposite comp) {
		this.comp = comp;
	}
	public MyInternalFrame(String title, int x, int y, int w, int h, final float alpha) {
	      super(title);
	      setClosable(true);
	      setBounds(x, y, w, h);
	      setVisible(true);
	   }

	   @Override
	   public void paint(Graphics g) {
	      Graphics2D g2 = (Graphics2D) g;
	      g2.setComposite(comp);
	      super.paint(g);
	   }
	   public void setAlpha(float alpha) {
	      comp = comp.derive(alpha);
	   }
	}

