package com.forsteri.unlimitedfluidity.core.flowingGas;

import java.util.Objects;

public class Weighted<T> {
    protected final T value;
    public int weight;

    public Weighted(T value, int weight) {
        this.value = value;
        this.weight = weight;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Weighted<?> weighted = (Weighted<?>) o;
        return weight == weighted.weight && Objects.equals(value, weighted.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, weight);
    }
}
