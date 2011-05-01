package net.awired.visuwall.core.utils;

import java.util.Iterator;
import java.util.List;

import org.springframework.util.AutoPopulatingList;

public class ShrinkList<E> extends AutoPopulatingList<E> {

	private static final long serialVersionUID = 1L;

	public ShrinkList(Class<? extends E> elementClass) {
		super(elementClass);
	}

	/**
	 * Decorates list with shrinkable lazy list.
	 */
	public static <E> List<E> decorate(Class<E> elementClass) {
		return new ShrinkList<E>(elementClass);
	}

	public void shrink() {
		for (Iterator<E> i = super.iterator(); i.hasNext();)
			if (i.next() == null)
				i.remove();
	}

	@Override
	public Iterator<E> iterator() {
		shrink();
		return super.iterator();
	}

}


//public class BindingList<E extends Markable> extends ArrayList<E> {
//	Class cls;
//	public BindingList(Class operatingClass) {
//		cls = operatingClass;
//	}
//
//	public boolean addAll(Collection<? extends E> c) {
//		boolean res = super.addAll(c);
//		for (E e : this) {
//			e.setMarked(false);
//		}
//		return res;
//	}
//
//	public E get(int index) {
//		try {
//			E e = super.get(index);
//			e.setMarked(true);
//			return e;
//		} catch (IndexOutOfBoundsException e) {
//			try {
//				while (index >= size()) {
//					E o = (E) cls.newInstance();
//					o.setMarked(false);
//					add(o);
//				}
//				return super.get(index);
//			} catch (InstantiationException e1) {
//				e1.printStackTrace(); 
//			} catch (IllegalAccessException e1) {
//				e1.printStackTrace();
//			}
//		}
//		throw new RuntimeException("Shit happens.");
//	}
//
//	public void trimToSize() {
//		for (int i = size() - 1; i >= 0; i--) {
//			  E e = super.get(i);
//			if (!e.isMarked()) remove(i);
//		}
//		super.trimToSize();
//
//	}
//}