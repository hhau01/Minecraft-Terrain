/*
* file: Chunk.java
* author: Henry Au, Leon Yen
* class: CS 445 – Computer Graphics
*
* assignment: Final Checkpoint
* date last modified: 11/23/2015
*
* purpose: Holds information of the chunks which are just multiple blocks
*/

package minecraft;

import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

//Chunk constructor randomly assigns block type to each block in chunk
public class Chunk {
    static final int CHUNK_SIZE = 30;
    static final int CUBE_LENGTH = 2;
    final int MIN_HEIGHT = 8;
    private Block [][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int VBOTextureHandle;
    private int StartX, StartY, StartZ;
    private Random r;
    private Texture texture;
    
    public Chunk(int startX, int startY, int startZ) {
        try{
            texture = TextureLoader.getTexture("PNG", 
                    ResourceLoader.getResourceAsStream("terrain.png"));
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        r= new Random();
        
        /*
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (int x = 0; x < CHUNK_SIZE; x++) {
           for (int y = 0; y < CHUNK_SIZE; y++) {
               for (int z = 0; z < CHUNK_SIZE; z++) {
                   if(r.nextFloat() > 0.7f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Grass);
                    }else if(r.nextFloat() > 0.4f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Dirt);
                    }else if(r.nextFloat() > 0.2f){
                        Blocks[x][y][z] = new Block(Block.BlockType.BlockType_Water);
                    }else{
                        Blocks[x][y][z] = new
                        Block(Block.BlockType.BlockType_Bedrock); //change to Bedrock?
                    }
                }
            } 
        }
        */
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ);
    }
    
    //Renders the chunk and generates the noise(terrain) for the chunk
    public void rebuildMesh(float startX, float startY, float startZ) {
        SimplexNoise noise = new SimplexNoise(30, 0.05, r.nextInt());
        int height;
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE*CHUNK_SIZE*CHUNK_SIZE)*6*12);
        for (float x = 0; x < CHUNK_SIZE; x++) {
            for (float z = 0; z < CHUNK_SIZE; z++) {
                height = Math.abs(StartY + (int)(100*noise.getNoise((int)x, (int)z))*CUBE_LENGTH);
                height += MIN_HEIGHT;
                height %= CHUNK_SIZE;
                for(float y = 0; y < height; y++){
                    VertexPositionData.put(createCube((float)(startX + x*CUBE_LENGTH), 
                            (float)(y*CUBE_LENGTH + (int)(CHUNK_SIZE*.8)), 
                            (float)(startZ + z*CUBE_LENGTH)));

                    //different blocks for each height
                    //only grass at the topmost level of terrain
                    //dirt at levels below the top
                    //bedrock very bottom
                    if(y == height - 1)
                    {
                        VertexColorData.put(createCubeVertexCol(getCubeColor(new Block(Block.BlockType.BlockType_Grass))));
                        VertexTextureData.put(createTexCube((float)0, (float)0 ,new Block(Block.BlockType.BlockType_Grass)));
                    }
                    else if( y == 0 ) //Bedrock only on bottom
                    {
                        VertexColorData.put(createCubeVertexCol(getCubeColor(new Block(Block.BlockType.BlockType_Bedrock))));
                        VertexTextureData.put(createTexCube((float)0, (float)0 ,new Block(Block.BlockType.BlockType_Bedrock)));  
                    }
                    else //Dirt dispersed throughout
                    {
                        VertexColorData.put(createCubeVertexCol(getCubeColor(new Block(Block.BlockType.BlockType_Dirt))));
                        VertexTextureData.put(createTexCube((float)0, (float)0 ,new Block(Block.BlockType.BlockType_Dirt)));
                    }
                }
            }
        }
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    public void render() {
        if(!Frustum.cubeInFrustum(StartX, StartY, StartZ, CHUNK_SIZE, CHUNK_SIZE, CHUNK_SIZE)){
            Minecraft.inView = false;
            return;
        }
        Minecraft.inView = true;
        glPushMatrix();
            glPushMatrix();
            glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
            glColorPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
            glBindTexture(GL_TEXTURE_2D, 1);
            glTexCoordPointer(2, GL_FLOAT, 0, 0L);
            glDrawArrays(GL_QUADS, 0 , CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    //generates array that repeats block color
    private float[] createCubeVertexCol(float[] CubeColorArray) {
        float[] cubeColors = new float[CubeColorArray.length * 4 * 6];
        for (int i = 0; i < cubeColors.length; i++) {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }
    
    //creates a cube
    public static float[] createCube(float x, float y, float z) {
        int offset = CUBE_LENGTH / 2;
        return new float[] {
            // TOP QUAD
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
            // BOTTOM QUAD
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
            // FRONT QUAD
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            // BACK QUAD
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
            // LEFT QUAD
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
            // RIGHT QUAD
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z};
    }
    
    private float[] getCubeColor(Block block) {
        /* CHECKPOINT 1
        switch (block.GetID()) {
            case 1:
                return new float[] { 0, 1, 0 };
            case 2:
                return new float[] { 1, 0.5f, 0 };
            case 3:
                return new float[] { 0, 0f, 1f };
        }
        CHECKPOINT 1 */ 
        return new float[] { 1, 1, 1 };
    }
    
    //adds texture to each cube depending on block type
    public static float[] createTexCube(float x, float y, Block block){
        float offset = (1024f/16)/1024f; //Texture is 128x128 per block. File is 1024x1024
        
        switch(block.GetID()){
            case 0: //Grass
                return new float[] {
                // TOP
                x + offset*3, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*3, y + offset*9,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                // FRONT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                // BACK QUAD
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                // LEFT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1,
                // RIGHT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1,
                x + offset*3, y + offset*1};
                
            case 1: //Sand
                return new float[] {
                // TOP
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                // FRONT QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                // BACK QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                // LEFT QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                // RIGHT QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2};
          
            case 2: //Water
                return new float[] {
                // TOP)
                x + offset*2, y + offset*11,
                x + offset*3, y + offset*11,
                x + offset*3, y + offset*12,
                x + offset*2, y + offset*12,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*2, y + offset*11,
                x + offset*3, y + offset*11,
                x + offset*3, y + offset*12,
                x + offset*2, y + offset*12,
                // FRONT QUAD
                x + offset*2, y + offset*11,
                x + offset*3, y + offset*11,
                x + offset*3, y + offset*12,
                x + offset*2, y + offset*12,
                // BACK QUAD
                x + offset*2, y + offset*11,
                x + offset*3, y + offset*11,
                x + offset*3, y + offset*12,
                x + offset*2, y + offset*12,
                // LEFT QUAD
                x + offset*2, y + offset*11,
                x + offset*3, y + offset*11,
                x + offset*3, y + offset*12,
                x + offset*2, y + offset*12,
                // RIGHT QUAD
                x + offset*2, y + offset*11,
                x + offset*3, y + offset*11,
                x + offset*3, y + offset*12,
                x + offset*2, y + offset*12};
            case 3: //Dirt
                return new float[] {
                // TOP
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // FRONT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // BACK QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // LEFT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // RIGHT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1};
                
            case 4: //Stone
                return new float[] {
                // TOP
                x + offset*0, y + offset*0,
                x + offset*1, y + offset*0,
                x + offset*1, y + offset*1,
                x + offset*0, y + offset*1,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*0, y + offset*0,
                x + offset*1, y + offset*0,
                x + offset*1, y + offset*1,
                x + offset*0, y + offset*1,
                // FRONT QUAD
                x + offset*0, y + offset*0,
                x + offset*1, y + offset*0,
                x + offset*1, y + offset*1,
                x + offset*0, y + offset*1,
                // BACK QUAD
                x + offset*0, y + offset*0,
                x + offset*1, y + offset*0,
                x + offset*1, y + offset*1,
                x + offset*0, y + offset*1,
                // LEFT QUAD
                x + offset*0, y + offset*0,
                x + offset*1, y + offset*0,
                x + offset*1, y + offset*1,
                x + offset*0, y + offset*1,
                // RIGHT QUAD
                x + offset*0, y + offset*0,
                x + offset*1, y + offset*0,
                x + offset*1, y + offset*1,
                x + offset*0, y + offset*1};
          
            case 5: //Bedrock
                return new float[] {
                // TOP
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                // FRONT QUAD
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                // BACK QUAD
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                // LEFT QUAD
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2,
                // RIGHT QUAD
                x + offset*1, y + offset*1,
                x + offset*2, y + offset*1,
                x + offset*2, y + offset*2,
                x + offset*1, y + offset*2};
        }
        return null;
    }
}
