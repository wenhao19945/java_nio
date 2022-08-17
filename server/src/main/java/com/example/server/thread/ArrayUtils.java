package com.example.server.thread;

import java.lang.reflect.Array;

/**
 * @author WenHao
 * @ClassName ArrayUtils
 * @date 2022/8/5 16:43
 * @Description
 */
public class ArrayUtils {

  public static <T> T[] add(final T[] array, final T element) {
    final Class<?> type;
    if (array != null) {
      type = array.getClass().getComponentType();
    } else if (element != null) {
      type = element.getClass();
    } else {
      throw new IllegalArgumentException("Arguments cannot both be null");
    }
    @SuppressWarnings("unchecked") // type must be T
    final
    T[] newArray = (T[]) copyArrayGrow1(array, type);
    newArray[newArray.length - 1] = element;
    return newArray;
  }

  public static <T> T[] addAll(final T[] array1, @SuppressWarnings("unchecked") final T... array2) {
    if (array1 == null) {
      return clone(array2);
    } else if (array2 == null) {
      return clone(array1);
    }
    final Class<?> type1 = array1.getClass().getComponentType();
    @SuppressWarnings("unchecked") // OK, because array is of type T
    final T[] joinedArray = (T[]) Array.newInstance(type1, array1.length + array2.length);
    System.arraycopy(array1, 0, joinedArray, 0, array1.length);
    try {
      System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
    } catch (final ArrayStoreException ase) {
      // Check if problem was due to incompatible types
      /*
       * We do this here, rather than before the copy because:
       * - it would be a wasted check most of the time
       * - safer, in case check turns out to be too strict
       */
      final Class<?> type2 = array2.getClass().getComponentType();
      if (!type1.isAssignableFrom(type2)) {
        throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of "
            + type1.getName(), ase);
      }
      throw ase; // No, so rethrow original
    }
    return joinedArray;
  }

  private static Object copyArrayGrow1(final Object array, final Class<?> newArrayComponentType) {
    if (array != null) {
      final int arrayLength = Array.getLength(array);
      final Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
      System.arraycopy(array, 0, newArray, 0, arrayLength);
      return newArray;
    }
    return Array.newInstance(newArrayComponentType, 1);
  }

  public static <T> T[] clone(final T[] array) {
    if (array == null) {
      return null;
    }
    return array.clone();
  }

}
