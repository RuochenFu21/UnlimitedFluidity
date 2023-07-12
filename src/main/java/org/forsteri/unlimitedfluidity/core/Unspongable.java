package org.forsteri.unlimitedfluidity.core;

public interface Unspongable {
    default boolean spongable(){
        return false;
    }
}
