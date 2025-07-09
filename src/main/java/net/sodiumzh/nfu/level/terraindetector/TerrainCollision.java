package net.sodiumzh.nfu.level.terraindetector;

/* WIP */
public class TerrainCollision {





    public static enum Type {
        /** Non-liquid and no collision */
        AIR,
        /** Liquid and no collision */
        LIQUID,
        /** Non-waterlogged block without a standable upper surface */
        STAB,
        /** Waterlogged block without a standable upper surface */
        WATERLOGGED_STAB,
        /** Block with a standable upper surface */
        SOLID;
    }

}
