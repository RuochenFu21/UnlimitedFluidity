package com.forsteri.unlimitedfluidity.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.METHOD, ElementType.MODULE, ElementType.PACKAGE, ElementType.TYPE, ElementType.CONSTRUCTOR, ElementType.FIELD, ElementType.PARAMETER})
public @interface Api {
}
