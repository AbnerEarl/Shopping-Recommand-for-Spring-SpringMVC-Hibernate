package datastructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class FixSizePriorityQueue<E extends Comparable<E>> {

	private PriorityQueue<E> queue;
	private int maxSize; // 堆的最大容量

	public FixSizePriorityQueue(int maxSize) {
		if (maxSize <= 0)
			throw new IllegalArgumentException();
		this.maxSize = maxSize;
		this.queue = new PriorityQueue<E>(maxSize);
	}

	public void add(E e) {
		if (queue.size() < maxSize) { // 未达到最大容量，直接添加
			queue.add(e);
		} else { // 队列已满
			E peek = queue.peek();
			if (e.compareTo(peek) > 0) { // 将新元素与当前堆顶元素比较，保留较大的元素
				queue.poll();
				queue.add(e);
			}
		}
	}

	public List<E> sortedList() {
		List<E> list = new ArrayList<E>(queue);
		Collections.sort(list,new Comparator<E>() {

			@Override
			public int compare(E o1, E o2) {
				
				return o2.compareTo(o1);
			}
		}); // PriorityQueue本身的遍历是无序的，最终需要对队列中的元素进行排序
		return list;
	}
}
