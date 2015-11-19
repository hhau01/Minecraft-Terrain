/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minecraft;

/**
 *
 * @author Leon
 */
public class Block {
    private boolean IsActive;
    private BlockType Type;
    private float x, y, z;
    
    public enum BlockType {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5);
        
        private int BlockID;
        
        BlockType(int i) {
            BlockID = i;
        }
    
    }
    
    public Block(BlockType type)
    {
        Type = type;
    }
    
    public void SetActive(boolean active) {
        IsActive = active;
    }
        
    public boolean IsActive() {
        return IsActive;
    }
    
    public int GetID() {
        return Type.BlockID;
    }

    public void SetID(int i) {
        Type.BlockID = i;
    }
}
