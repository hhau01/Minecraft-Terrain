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
        
        public Chunk chunk;
        
        //Camera constructor
        public Camera(float x, float y, float z){ // do coor need to be float if we will always define the world by the cube units
            position = new Vector3f(x,y,z);
            chunk = new Chunk((int) x, (int) y, (int) z);
            
            /*IPosition = new Vector3f(x,y,z); ONLY FOR CHECKPOINT 1
            IPosition.x = 0f;
            IPosition.y = 15f;
            IPosition.z = 0f; */
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
        
        
        
}

