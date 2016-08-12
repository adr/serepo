package ch.hsr.isf.serepo.data.atom.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AtomTitle {

	String prefix() default "";
	
	String suffix() default "";
	
}
