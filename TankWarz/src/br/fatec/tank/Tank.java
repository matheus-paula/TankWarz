package br.fatec.tank;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

public class Tank{
	protected double x,y;
	protected String name;
	protected double angulo;
	protected int anguloc,cont=0;
	protected Shot tiro;
	protected double velocidade;
	protected Color cor;
	protected boolean estaAtivo;
	protected int meuTanqueAtivo;
	private long tempo;
	private int id;
	
	public Tank(double x, double y, double a, Color cor, int id){
		this.x = x;
		this.y = y;
		this.name = "";
		this.angulo = a;
		this.cor = cor;
		this.velocidade = 0.4;
		this.estaAtivo = false;
		this.id = id;
	}
	
	public void aumentarVelocidade(){
		if(velocidade < 3) velocidade = velocidade + 0.4;
	}
	public void diminuirVelocidade(){
		if(velocidade > 0) velocidade = velocidade - 0.4;
	}
	public void calculaTempo(){
		if(! estaAtivo){
			if(System.currentTimeMillis() - tempo > 5000){
				if(velocidade > 0) 
					velocidade = 1.2;
				else
					velocidade = -1.2;
			}
		}
	}
	public Shot getTiro() {
		return tiro;
	}
	public void setTiro(Shot tiro) {
		this.tiro = tiro;
	}
	public int getMeuTanqueAtivo() {
		return meuTanqueAtivo;
	}
	public void setMeuTanqueAtivo(int meuTanqueAtivo) {
		this.meuTanqueAtivo = meuTanqueAtivo;
	}
	public String getName() {
		return name;
	}
	public void setName(String s) {
		this.name = s;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getAngulo() {
		return angulo;
	}
	public void setAngulo(double angulo) {
		this.angulo = angulo;
	}
	public int getAnguloc() {
		return anguloc;
	}
	public void setAnguloc(int anguloc) {
		this.anguloc = anguloc;
	}
	public double getVelocidade() {
		return velocidade;
	}
	public void setVelocidade(double velocidade) {
		this.velocidade = velocidade;
	}
	public Color getCor() {
		return cor;
	}
	public void setCor(Color cor) {
		this.cor = cor;
	}
	public boolean isEstaAtivo() {
		return estaAtivo;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getTempo() {
		return tempo;
	}
	public void setTempo(long agora){
		this.tempo = agora; 
	}
	public void girarHorario(int a){
		angulo += a;
		if(angulo >= 360)
			angulo = angulo - 360;
	}
	public void girarAntiHorario(int a){
		angulo -= a;
		if(angulo <= 0)
			angulo = 360-a;
	}
	public void mover(){
		x = x + Math.sin(Math.toRadians(angulo)) * velocidade;
		y = y - Math.cos(Math.toRadians(angulo)) * velocidade;
		
		if (x<=30 ){
			if(angulo >= 270 && angulo < 360) angulo = 360 - angulo;
			if(angulo > 180 && angulo <= 270) angulo = 360 - angulo;
			if(velocidade < 0){
				velocidade *= -0.4;
				girarHorario(5);
			}
			
		}
		if (y<=30){
			if(angulo > 270 && angulo <= 360) angulo = 360 - angulo + 180;
			if(angulo >= 0 && angulo < 90) angulo = 360 - angulo - 180;
			if(velocidade < 0){
				velocidade *= -0.4;
			}
		}
		if (y>= ArenaSettings.getAlturaTela() - 30){
			if(angulo > 90 && angulo < 180) angulo = 360 - angulo - 180;
			if(angulo >= 180 && angulo < 270) angulo = 360 - angulo + 180;
			if(velocidade < 0){
				velocidade *= -0.4;
				girarAntiHorario(5);
			}
			
		}
		if (x>= ArenaSettings.getLarguraTela() - 30){
			if(angulo > 0 && angulo <= 90) angulo= 360 - angulo;
			if(angulo >= 90 && angulo < 180) angulo= 360 - angulo;
			if(velocidade < 0){
				velocidade *= -0.4;
			}
		}
	}
	
	public void setEstaAtivo(boolean estaAtivo){
		this.estaAtivo = estaAtivo;
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
		//CORPO DO TANQUE
		g2d.setColor(cor);
		g2d.fillRect(-10, -12, 20, 24);
		//ESTEIRAS
		for(int i = -12; i <= 8; i += 4){
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.fillRect(-15, i, 5, 4);
			g2d.fillRect(10, i, 5, 4);
			g2d.setColor(Color.BLACK);
			g2d.fillRect(-15, i, 5, 4);
			g2d.fillRect(10, i, 5, 4);
		}
		//CANHÃO
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(-3, -25, 6, 25);
		g2d.setColor(cor);
		g2d.drawRect(-3, -25, 6, 25);
		//Se o tanque estiver ativo
		//Desenhamos uma margem
		if(estaAtivo){
			g2d.setColor(new Color(120,120,120));
			Stroke linha = g2d.getStroke();
			g2d.setStroke(new BasicStroke(1f,BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND,0,
					new float[]{8},0));
			g2d.drawRect(-24, -32, 48, 55);
			g2d.setStroke(linha);
		}
		//DESENHA MARGEM DE TANQUE SELECIONADO
		if(name != null && name.length() > 0){
			char [] pname = name.toCharArray();
			g2d.setColor(cor);
			Stroke linha = g2d.getStroke();
			g2d.setStroke(new BasicStroke(1f,BasicStroke.CAP_ROUND,
					BasicStroke.JOIN_ROUND,0,
					new float[]{8},0));
			g2d.drawRect(-24, -32, 48, 55);
			g2d.setStroke(linha);
			g2d.drawChars(pname, 0, pname.length, -20, 40);
		}
		//Aplicamos o sistema de coordenadas
		g2d.setTransform(antes);
	}
	
	public Shape getRectEnvolvente(){
		AffineTransform at = new AffineTransform();
		at.translate(x,y);
		at.rotate(Math.toRadians(angulo));
		Rectangle rect = new Rectangle(-24,-32,48,55);
		return at.createTransformedShape(rect);
	}
	
}