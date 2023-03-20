package org.forsteri123.unlimitedfluidity.core;

public interface Unspongable {
    default boolean spongable(){
        return false;
    }
}
