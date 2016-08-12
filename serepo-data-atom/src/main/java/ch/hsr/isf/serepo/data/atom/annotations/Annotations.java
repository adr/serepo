package ch.hsr.isf.serepo.data.atom.annotations;

import java.lang.annotation.Annotation;

public class Annotations {

	private Annotations() {
	}

	public static boolean isPresent(Object instance, Class<? extends Annotation> annotation) {
		return isPresent(instance.getClass(), annotation);
	}

	public static boolean isPresent(Class<?> clazz, Class<? extends Annotation> annotation) {
		return clazz.isAnnotationPresent(annotation);
	}

	/**
	 * If the given Class of the instance is not annotated with the given
	 * {@link Annotation} a {@link RuntimeException} is thrown.
	 * 
	 * @param instance
	 * @param annotation
	 */
	public static void throwExceptionIfNotPresent(Object instance, Class<? extends Annotation> annotation) {
		throwExceptionIfNotPresent(instance.getClass(), annotation);
	}

	/**
	 * If the given Class is not annotated with the given {@link Annotation} a
	 * {@link RuntimeException} is thrown.
	 * 
	 * @param clazz
	 * @param annotation
	 */
	public static void throwExceptionIfNotPresent(Class<?> clazz, Class<? extends Annotation> annotation) {
		if (!isPresent(clazz, annotation)) {
			String message = String.format("The class '%s' is not annotated with '%s'.", clazz.getName(),
					annotation.getSimpleName());
			throw new RuntimeException(message);
		}
	}

}
