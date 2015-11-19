/*
* file: Minecraft.java
* author: Henry Au, Leon Yen, Marco Hernandez
* class: CS 445 – Computer Graphics
*
* assignment: Checkpoint 2
* date last modified: 11/19/2015
*
* purpose: Display multiple cubes using chunks method (creating a world at 
* least 30 cubes x 30 cubes large), with each cube textured and then randomly
* placed using simplex noise classes provided. Minimum of 6 cube types defined
* with a different texture for each one as follows: Grass, Sand, Dirt, Stone,
* Bedrock.
*/

package minecraft;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.Sys;
import org.lwjgl.util.glu.GLU;
import static org.lwjgl.opengl.GL11.*;

public class Minecraft{
        private Camera fp = new Camera(0f,0f,0f);
        private DisplayMode displayMode;
        
        /*
         * The next three methods are from the notes.
         * createWindow(), and initGL() are used to create
         * the 640x480 black display window.
         */
        private void start(){
            try{
                createWindow();
                initGL();
                fp.gameLoop();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        
        private void createWindow() throws Exception{
            Display.setFullscreen(false);
            DisplayMode d[] = Display.getAvailableDisplayModes();
            
            for(int i = 0; i < d.length; i++){
                if(d[i].getWidth() == 640
                        && d[i].getHeight() == 480
                        && d[i].getBitsPerPixel() == 32){
                    displayMode = d[i];
                    break;
                }
            }
            
            Display.setDisplayMode(displayMode);
            Display.setTitle("Fake Minecraft");
            Display.create();
        }
        
        private void initGL(){
            glClearColor(0.0f,0.0f,0.0f,0.0f);
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            
            GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 300.0f);
            
            glMatrixMode(GL_MODELVIEW);
            glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
            
            glEnableClientState(GL_VERTEX_ARRAY);
            glEnableClientState(GL_COLOR_ARRAY);
            glEnable(GL_DEPTH_TEST);
            
            glEnable(GL_TEXTURE_2D);
            glEnableClientState(GL_TEXTURE_COORD_ARRAY);
        }
        
    public static void main(String[] args){
        Minecraft basic = new Minecraft();
        basic.start();
    }
}
