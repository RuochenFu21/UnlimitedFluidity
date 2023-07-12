package org.forsteri.unlimitedfluidity.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.function.Predicate;

public class Fragments {
    public static int countHierarchically(Object iterable, Predicate<Object> predicate) {
        if (iterable instanceof Iterable) {
            return countHierarchically((Iterable<?>) iterable, predicate);
        }

        if (iterable.getClass().isArray()) {
            return countHierarchically(getArray(iterable), predicate);
        }

        throw new IllegalArgumentException("argument must be an instance of Iterable or an array");
    }

    private static boolean hasHierarchy(Object iterable) {
        return iterable instanceof Iterable || iterable.getClass().isArray();
    }

    private static final Class<?>[] PRIMITIVES_TYPES = {
            int.class, float.class, double.class, boolean.class,
            byte.class, short.class, long.class, char.class };

    @NotNull
    private static Object[] getArray(Object val){
        int arrayDepth = 0;
        Class<?> componentType = val.getClass();
        while (componentType.isArray()) {
            componentType = componentType.getComponentType();
            arrayDepth++;
        }

        if (arrayDepth == 0) // not an array
            return null;

        final int finalArrayDepth  = arrayDepth;

        Class<?>[] primitiveArrays = Arrays.stream(PRIMITIVES_TYPES).map(
                aClass -> {
                    Class<?> arrayClass = aClass;

                    for (int i = 0; i < finalArrayDepth; i++) {
                        arrayClass = arrayClass.arrayType();
                    }

                    return arrayClass;
                }
        ).toArray(Class<?>[]::new);

        Class<?> valKlass = val.getClass();
        Object[] outputArray = null;

        for(Class<?> arrKlass : primitiveArrays){
            if(valKlass.isAssignableFrom(arrKlass)){
                int arrlength = Array.getLength(val);
                outputArray = new Object[arrlength];
                for(int i = 0; i < arrlength; ++i){
                    outputArray[i] = Array.get(val, i);
                }
                break;
            }
        }

        if(outputArray == null)
            outputArray = (Object[]) val;

        return outputArray;
    }

    public static int countHierarchically(Iterable<?> iterable, Predicate<Object> predicate) {
        int count = 0;
        for (Object object : iterable) {
            if (predicate.test(object)) {
                count++;
            } else if (hasHierarchy(object)) {
                count += countHierarchically(object, predicate);
            }
        }

        return count;
    }

    public static int countHierarchically(Object[] iterable, Predicate<Object> predicate) {
        int count = 0;
        for (Object object : iterable) {
            if (predicate.test(object)) {
                count++;
            } else if (hasHierarchy(object)) {
                count += countHierarchically(object, predicate);
            }
        }

        return count;
    }
}
