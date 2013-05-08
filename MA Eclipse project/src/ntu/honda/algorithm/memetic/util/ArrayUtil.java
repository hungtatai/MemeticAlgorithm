package ntu.honda.algorithm.memetic.util;

public class ArrayUtil {
	
	public static void swap(Object[] objs, int index1, int index2) {
		Object tmp = objs[index1];
		objs[index1] = objs[index2];
		objs[index2] = tmp;
	}
	
	public static Integer[] newIntegerArray(int size) {
		Integer[] array = new Integer[size];
		for(int i=0;i<size;i++)
			array[i] = 0;
		return array;
	}
	
	public static Boolean[] newBooleanArray(int size) {
		Boolean[] array = new Boolean[size];
		for(int i=0;i<size;i++)
			array[i] = false;
		return array;
	}
}
