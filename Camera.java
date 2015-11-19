/*file: Camera.java
* author: Henry Au, Leon Yen, Marco Hernandez
* class: CS 445 – Computer Graphics
*
* assignment: Checkpoint 2
* date last modified: 11/19/2015
*
* purpose: Stores camera position data and performs transformations
* as if looking through camera. Render() creates a 3D cube (checkpoint 1)
*/

package minecraft;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;


public class Camera {
        private static Vector3f position = null;
        private Vector3f IPosition = null;
        private float yaw = 0.0f;
        private float pitch = 0.0f;
        private Vector3f me;
        
        //Camera constructor
        public Camera(float x, float y, float z){
            position = new Vector3f(x,y,z);
            IPosition = new Vector3f(x,y,z);
            IPosition.x = 0f;
            IPosition.y = 15f;
            IPosition.z = 0f;
        }
        
        public void yaw(float amount){
            yaw += amount;
        }
        public void pitch(float amount){
            pitch -= amount;
        }
        public void walkForward(float distance){
            float xOffset = distance*(float)Math.sin(Math.toRadians(yaw));
            float zOffset = distance*(float)Math.cos(Math.toRadians(yaw));
            position.x -= xOffset;
            position.z += zOffset;
        }
        
        public void walkBackwards(float distance){
            float xOffset = distance*(float)Math.sin(Math.toRadians(yaw));
            float zOffset = distance*(float)Math.cos(Math.toRadians(yaw));
            position.x += xOffset;
            position.z -= zOffset;
        }
        
        public void strafeLeft(float distance){
            float xOffset = distance*(float)Math.sin(Math.toRadians(yaw-90));
            float zOffset = distance*(float)Math.cos(Math.toRadians(yaw-90));
            position.x -= xOffset;
            position.z += zOffset;
        }
        
        public void strafeRight(float distance){
            float xOffset = distance*(float)Math.sin(Math.toRadians(yaw+90));
            float zOffset = distance*(float)Math.cos(Math.toRadians(yaw+90));
            position.x -= xOffset;
            position.z += zOffset;
        }
        
        public void moveUp(float distance){
            position.y -= distance;        
        }
        
        public void moveDown (float distance) {
            position.y += distance;
        }
        
        public void lookThrough() {
            glRotatef(pitch, 1.0f, 0.0f, 0.0f);
            glRotatef(yaw, 0.0f, 1.0f, 0.0f);
            glTranslatef(position.x, position.y, position.z);
        }
        
        public void gameLoop() {
            Camera camera = new Camera(0, 0, 0);
            float dx = 0.0f;
            float dy = 0.0f;
            float dt = 0.0f;
            float lastTime = 0.0f;
            long time = 0;
            float mouseSensitivity = 0.09f;
            float movementSpeed = 0.35f;
            Mouse.setGrabbed(true);
            
            while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
                time = Sys.getTime();
                lastTime = time;
                
                dx = Mouse.getDX();
                dy = Mouse.getDY();
                camera.yaw(dx * mouseSensitivity);
                camera.pitch(dy * mouseSensitivity);
                
                //Move forward
                if(Keyboard.isKeyDown(Keyboard.KEY_W)){
                    camera.walkForward(movementSpeed);
                }
                
                //Move backwards
                if(Keyboard.isKeyDown(Keyboard.KEY_S)){
                    camera.walkBackwards(movementSpeed);
                }
                
                //Strafe left
                if(Keyboard.isKeyDown(Keyboard.KEY_A)){
                    camera.strafeLeft(movementSpeed);
                }
                
                //Strafe right
                if(Keyboard.isKeyDown(Keyboard.KEY_D)){
                    camera.strafeRight(movementSpeed);
                }
                
                //Move up
                if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
                    camera.moveUp(movementSpeed);
                }
                
                //Move down
                if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                    camera.moveDown(movementSpeed);
                }
                
                glLoadIdentity();
                camera.lookThrough();
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                
                
                render();
                
                Display.update();
                Display.sync(60);
            }

            Display.destroy();
        }
        
        /*
        //Render simple 3D cube w/ six different colors
        private void render(){
            try{
                glBegin(GL_QUADS);
                    //Top
                    glColor3f(0.0f,0.0f,1.0f); //Color
                    glVertex3f(1.0f,1.0f,-1.0f);
                    glVertex3f(-1.0f,1.0f,-1.0f);
                    glVertex3f(-1.0f,1.0f,1.0f);
                    glVertex3f(1.0f,1.0f,1.0f);

                    //Bottom
                    glColor3f(0.0f,1.0f,0.0f); //Color
                    glVertex3f(1.0f,-1.0f,1.0f);
                    glVertex3f(-1.0f,-1.0f,1.0f);
                    glVertex3f(-1.0f,-1.0f,-1.0f);
                    glVertex3f(1.0f,-1.0f,-1.0f);

                    //Front
                    glColor3f(1.0f,0.0f,1.0f); //Color
                    glVertex3f(1.0f,1.0f,1.0f);
                    glVertex3f(-1.0f,1.0f,1.0f);
                    glVertex3f(-1.0f,-1.0f,1.0f);
                    glVertex3f(1.0f,-1.0f,1.0f);

                    //Back
                    glColor3f(1.0f,0.0f,0.0f); //Color
                    glVertex3f(1.0f,-1.0f,-1.0f);
                    glVertex3f(-1.0f,-1.0f,-1.0f);
                    glVertex3f(-1.0f,1.0f,-1.0f);
                    glVertex3f(1.0f,1.0f,-1.0f);

                    //Left
                    glColor3f(1.0f,1.0f,0.0f); //Color
                    glVertex3f(-1.0f,1.0f,1.0f);
                    glVertex3f(-1.0f,1.0f,-1.0f);
                    glVertex3f(-1.0f,-1.0f,-1.0f);
                    glVertex3f(-1.0f,-1.0f,1.0f);

                    //Right
                    glColor3f(0.0f,1.0f,1.0f); //Color
                    glVertex3f(1.0f,1.0f,-1.0f);
                    glVertex3f(1.0f,1.0f,1.0f);
                    glVertex3f(1.0f,-1.0f,1.0f);
                    glVertex3f(1.0f,-1.0f,-1.0f);
                glEnd();
                
                glBegin(GL_LINE_LOOP);
                    //Top
                    glColor3f(0.0f,0.0f,0.0f); //Color
                    glVertex3f(1.0f,1.0f,-1.0f);
                    glVertex3f(-1.0f,1.0f,-1.0f);
                    glVertex3f(-1.0f,1.0f,1.0f);
                    glVertex3f(1.0f,1.0f,1.0f);
                glEnd();
                
                glBegin(GL_LINE_LOOP);
                    //Bottom
                    glVertex3f(1.0f,-1.0f,1.0f);
                    glVertex3f(-1.0f,-1.0f,1.0f);
                    glVertex3f(-1.0f,-1.0f,-1.0f);
                    glVertex3f(1.0f,-1.0f,-1.0f);
                glEnd();
                
                glBegin(GL_LINE_LOOP);
                    //Front
                    glVertex3f(1.0f,1.0f,1.0f);
                    glVertex3f(-1.0f,1.0f,1.0f);
                    glVertex3f(-1.0f,-1.0f,1.0f);
                    glVertex3f(1.0f,-1.0f,1.0f);
                glEnd();
                
                glBegin(GL_LINE_LOOP);
                    //Back
                    glVertex3f(1.0f,-1.0f,-1.0f);
                    glVertex3f(-1.0f,-1.0f,-1.0f);
                    glVertex3f(-1.0f,1.0f,-1.0f);
                    glVertex3f(1.0f,1.0f,-1.0f);
                glEnd();
                
                glBegin(GL_LINE_LOOP);
                    //Left
                    glVertex3f(-1.0f,1.0f,1.0f);
                    glVertex3f(-1.0f,1.0f,-1.0f);
                    glVertex3f(-1.0f,-1.0f,-1.0f);
                    glVertex3f(-1.0f,-1.0f,1.0f);
                glEnd();
                
                glBegin(GL_LINE_LOOP);
                    //Right
                    glVertex3f(1.0f,1.0f,-1.0f);
                    glVertex3f(1.0f,1.0f,1.0f);
                    glVertex3f(1.0f,-1.0f,1.0f);
                    glVertex3f(1.0f,-1.0f,-1.0f);
                glEnd();
                
            }
            catch(Exception e){
            
            }
        }
        */
}

