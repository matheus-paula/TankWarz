package br.fatec.tank;



import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;


public class Shot extends Tank{

	public Shot(double x, double y, double a, Color cor, int id) {
		super(x, y, a, cor, id);
		
	}

	public void mover(){
		if(estaAtivo){
			x = x + Math.sin(Math.toRadians(angulo)) * 10;
			y = y - Math.cos(Math.toRadians(angulo)) * 10;
		}
		if(x < -5 || x >ArenaSettings.getLarguraTela() || y<-5 || y> ArenaSettings.getAlturaTela()) {
			estaAtivo = false;
		}
		
	}
	public void draw(Graphics2D g2d){
		//Armazenamos o sistema de coordenadas original.
		AffineTransform antes = g2d.getTransform();
		//Criamos um sistema de coordenadas para o tanque.
		AffineTransform depois = new AffineTransform();
		depois.translate(x, y);
		depois.rotate(Math.toRadians(angulo));
		//Aplicamos o sistema de coordenadas.
		g2d.transform(depois);
		//Desenhamos o missil
		g2d.setColor(cor);
		g2d.fillRect(-3, -3, 4, 4);
		
		//Aplicamos o sistema de coordenadas
		g2d.setTransform(antes);
	}

}
