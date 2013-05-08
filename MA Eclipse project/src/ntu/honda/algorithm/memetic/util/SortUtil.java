package ntu.honda.algorithm.memetic.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortUtil {

	public static <T extends Comparable<? super T>, E> void sort(List<T> order, List<E> c) {
		try {
			if(order.size()!=c.size())
				throw new Exception("Must be the same size!");
			
			List<KeyValuePair<T, E>> list = new ArrayList<KeyValuePair<T, E>>();
			for(int k=0;k<order.size();k++){
				list.add(new KeyValuePair<T, E>(order.get(k), c.get(k)));
			}
			
			Collections.sort(list, new Comparator<KeyValuePair<T, E>>(){
				@Override
				public int compare(KeyValuePair<T, E> o1, KeyValuePair<T, E> o2) {
					return o1.Key.compareTo(o2.Key);
				}
			});
			
			order.clear();
			c.clear();
			for(KeyValuePair<T, E> p : list) {
				order.add(p.Key);
				c.add(p.Value);
			}
			
		} catch(Exception ex) {
			ex.getStackTrace();
		}
	}
	
}
