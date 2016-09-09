/*file: Camera.java
* author: Henry Au, Leon Yen
* class: CS 445 – Computer Graphics
*
* assignment: Final Checkpoint
* date last modified: 11/23/2015
*
* purpose: Stores camera position data and performs transformations
* as if looking through camera. Render() creates a 3D cube (checkpoint 1)
*/

package minecraft;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import static org.lwjgl.opengl.GL11.*;
import java.nio.FloatBuffer;

public class Camera {
        private static Vector3f position = null;
        private float yaw = 0.0f;
        private float pitch = 0.0f;
        public Chunk chunk;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        
        //Camera constructor
        public Camera(float x, float y, float z){ // do coor need to be float if we will always define the world by the cube units
            position = new Vector3f(x,y,z);
            chunk = new Chunk((int) x, (int) y, (int) z);
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
            
            lightPosition.put(position.x-=xOffset).put(
                    position.y).put(position.z+=zOffset).put(1.0f).flip();
            glLight(GL_LIGHT0, GL_POSITION, lightPosition); 
        }
        
        public void walkBackwards(float distance){
            float xOffset = distance*(float)Math.sin(Math.toRadians(yaw));
            float zOffset = distance*(float)Math.cos(Math.toRadians(yaw));
            
            lightPosition.put(position.x+=xOffset).put(
                    position.y).put(position.z-=zOffset).put(1.0f).flip();
            glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        }
        
        public void strafeLeft(float distance){
            float xOffset = distance*(float)Math.sin(Math.toRadians(yaw-90));
            float zOffset = distance*(float)Math.cos(Math.toRadians(yaw-90));
            
            lightPosition.put(position.x-=xOffset).put(
                    position.y).put(position.z+=zOffset).put(1.0f).flip();
            glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        }
        
        public void strafeRight(float distance){
            float xOffset = distance*(float)Math.sin(Math.toRadians(yaw+90));
            float zOffset = distance*(float)Math.cos(Math.toRadians(yaw+90));
            
            lightPosition.put(position.x-=xOffset).put(
                    position.y).put(position.z+=zOffset).put(1.0f).flip();
            glLight(GL_LIGHT0, GL_POSITION, lightPosition);
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
            
            lightPosition.put(position.x).put(
                    position.y).put(position.z).put(1.0f).flip();
            
            glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        }
        
        
        
}

