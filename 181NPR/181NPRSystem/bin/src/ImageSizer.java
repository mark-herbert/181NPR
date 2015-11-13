
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kenneth
 */
public class ImageSizer extends JPanel{
    private Image img;
    private double width;
    private double height;
    
    public ImageSizer(Image img, Dimension d){
        width = d.getWidth();
        height = d.getHeight();
        this.img = img;
        Dimension size = new Dimension(200, 200);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);
    }
    
    public void paintComponent(Graphics g){
        g.drawImage(img, 2, 2, (int) width, (int) height, null);
    }
    
}
