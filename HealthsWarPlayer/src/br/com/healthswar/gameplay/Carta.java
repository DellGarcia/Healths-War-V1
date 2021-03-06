package br.com.healthswar.gameplay;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public abstract class Carta extends JPanel implements MouseListener, MouseMotionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7106990056333713852L;
	protected String description;
	
	protected boolean virado;
	protected CardLocal local;
	
	protected final String frontImg = "src/br/com/healthswar/assets/card-sm.jpg";
	protected final String backImg  = "src/br/com/healthswar/assets/backImg-sm.png";

	public Carta() {
		virado = true;
		local = CardLocal.DECK;
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public boolean isVirado() {
		return virado;
	}

	public void setVirado(boolean virado) {
		this.virado = virado;
	}

	public CardLocal getLocal() {
		return local;
	}

	public void setLocal(CardLocal local) {
		this.local = local;
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create();
		Image imagem = null;
		int width = 100, height = 141;
		
		try {
			imagem = ImageIO.read(new File(virado?backImg:frontImg));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		g2d.drawImage(imagem, 0, 0, width, height, this);
		
		g2d.dispose();
	}
	
}
