package trial2dgame;

import entity.Entity;

public class EventHandler {
    
    GamePanel gp;
    EventRect eventRect[][][];
    Entity eventMaster;
    
    int previousEventX, previousEventY;
    boolean canTouchEvent = true;
    int tempMap, tempCol, tempRow;
    
    public EventHandler(GamePanel gp) {
        this.gp = gp;
        
        eventMaster = new Entity(gp);
        
        eventRect = new EventRect[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];
        
        int map = 0;
        int col = 0;
        int row = 0;
        while(map < gp.maxMap && col < gp.maxWorldCol && row < gp.maxWorldRow) {
            
            eventRect[map][col][row] = new EventRect();
            eventRect[map][col][row].x = 23;
            eventRect[map][col][row].y = 23;
            eventRect[map][col][row].width = 2;
            eventRect[map][col][row].height = 2;
            eventRect[map][col][row].eventRectDefaultX = eventRect[map][col][row].x;
            eventRect[map][col][row].eventRectDefaultY = eventRect[map][col][row].y;
        
            col++;
            if(col == gp.maxWorldCol) {
                col = 0;
                row++;
                
                if(row == gp.maxWorldRow) {
                    row = 0;
                    map++;
                }
            }
            
        }
        setDialogue();        
    }    
        
    public void setDialogue() {
        
        eventMaster.dialogues[0][0] = "You fell into a pit!";

        eventMaster.dialogues[1][0] = "You drink the water. Your life recovered.\n(The progress has been saved)";
    }
    
    public void checkEvent () {
        
        // CHECK IF THE MC IS MORE THAN 1 TILE AWAY FROM LAST EVENT
        int xDistance = Math.abs(gp.player.worldX - previousEventX); //ABSOLUTE NUM
        int yDistance = Math.abs(gp.player.worldY - previousEventY);
        int distance = Math.max(xDistance,  yDistance);
        if(distance > gp.tileSize) {
            canTouchEvent = true;
        }
        
        if(canTouchEvent == true) { //map,x,y,direction
            // ADDED - Tine: Portal activation when all monsters defeated on map 0
            if (gp.currentMap == 0 && gp.areAllMonstersDefeated(0)) {
                if (hit(0, 20, 20, "any") == true) { // Center portal coordinates
                    teleport(1, 25, 37); // Teleport to map 1
                }
            }
        } 
    }
    
    public boolean hit (int map, int col, int row, String reqDirection) {
            
        boolean hit = false;

        if(map == gp.currentMap) { //cannot hit event in other maps
            gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
            gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;
            eventRect[map][col][row].x = col * gp.tileSize + eventRect[map][col][row].x;
            eventRect[map][col][row].y = row * gp.tileSize + eventRect[map][col][row].y;
                            
            if (gp.player.solidArea.intersects(eventRect[map][col][row]) && eventRect[map][col][row].eventDone == false) {
               if(gp.player.direction.contentEquals(reqDirection) || reqDirection.contentEquals("any")) {
                    hit = true;
                    
                    previousEventX = gp.player.worldX;
                    previousEventY = gp.player.worldY;
                    
                }
            }
                
            gp.player.solidArea.x = gp.player.solidAreaDefaultX;
            gp.player.solidArea.y = gp.player.solidAreaDefaultY;
            eventRect[map][col][row].x = eventRect[map][col][row].eventRectDefaultX;
            eventRect[map][col][row].y = eventRect[map][col][row].eventRectDefaultY;                    
        }
            
        return hit;
    }    

    public void teleport(int map, int col, int row) {
        
        gp.gameState = gp.transitionState;
        tempMap = map;
        tempCol = col;
        tempRow = row;
        canTouchEvent = false;
        gp.playSE(2);
    }
    
    public void damagePit(int gameState) {        
        
        gp.gameState = gameState;
        gp.playSE(6);
        eventMaster.startDialogue(eventMaster, 0); // (eventMaster, dialogue set dialogues[this one][])
        gp.player.life -= 1;
//        eventRect[col][row].eventDone = true;
        canTouchEvent = false; // LET'S MC MOVE AWAY 1 TILE AFTER TRIGGER
//        gp.playSE(11);
    }
    
    public void healingPool(int gameState) { // gp.saveLoad.save(); // TO SAVE PROGRESS
    
        if(gp.keyH.enterPressed == true) {
            gp.gameState = gameState;
            gp.player.attackCanceled = true;
            gp.playSE(2);
            eventMaster.startDialogue(eventMaster, 1);
            gp.player.life = gp.player.getMaxLife();
            gp.aSetter.setMonster(); //respawn monster
            gp.saveLoad.save(); // TO SAVE PROGRESS
        }
    }
    
    public void speak(Entity entity) {
        
        if(gp.keyH.enterPressed == true) {
            gp.gameState = gp.dialogueState;
            gp.player.attackCanceled = true;
            entity.speak();
        }
    }
    
}