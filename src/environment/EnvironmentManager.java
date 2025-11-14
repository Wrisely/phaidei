package environment;

import java.awt.Graphics2D;

import trial2dgame.GamePanel;

public class EnvironmentManager {

    GamePanel gp;
    public Lighting lighting;
    
    public EnvironmentManager(GamePanel gp) {
        this.gp = gp;
    }
    public void setup() {
        lighting = new Lighting(gp);
    }
    public void update(){
        // ADDED - Tine: Keep day/dusk cycle update but remove text display
        lighting.update();
    }
    public void draw(Graphics2D g2) {
        // ADDED - Tine: Keep day/dusk cycle drawing but remove text display
        lighting.draw(g2);
    }
    
}