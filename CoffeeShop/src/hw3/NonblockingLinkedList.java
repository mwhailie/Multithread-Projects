package hw3;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class NonblockingLinkedList<E> {
	
	private static class Node <E> {
		final E item;
		final AtomicReference<Node<E>> next;

		public Node(E item, Node<E> next) {
			this.item = item;
			this.next = new AtomicReference<Node<E>>(next);
		}
	}

	private final Node<E> dummy = new Node<E>(null, null);
	private final AtomicReference<Node<E>> head = new AtomicReference<Node<E>>(dummy);
	private final AtomicReference<Node<E>> tail = new AtomicReference<Node<E>>(dummy);
//	private AtomicInteger size = new AtomicInteger(0); 
	
//	public int size() {
//		return size.get();
//	}
//	
	public boolean add(E item) {
		Node<E> newNode = new Node<E>(item, null);
		while (true) {
			Node<E> curTail = tail.get();
			Node<E> tailNext = curTail.next.get();
			if (curTail == tail.get()) {
				if (tailNext != null) {
					tail.compareAndSet(curTail, tailNext);
				}else {
					if (curTail.next.compareAndSet(null, newNode)) {
						tail.compareAndSet(curTail, newNode);
//						size.getAndIncrement();
//						System.out.println("head:  "+head.get().next.get().item.toString());
//						System.out.println("tail:  "+tail.get().item.toString());
						return true;
					}
				}
			}
		}
	}
	
	public E poll() {
		Node<E> oldHead;
		AtomicReference<Node<E>> next;
		do {
			oldHead = head.get();
			if (oldHead.next.get() == null) {
//				System.out.println("NOTHEREEEEEEEEEEEEEEEEEEEEE");
				return null;
			}
			next = oldHead.next;
		} while (!head.compareAndSet(oldHead, next.get()));
//		System.out.println("head:  "+head.get().next.get().item.toString());
//		System.out.println("tail:  "+tail.get().item.toString());
		return next.get().item;
//
//		while (true) {
//			ConcurrentLinkedQueue.QNode<E> curHead = head.get();
//			ConcurrentLinkedQueue.QNode<E> headNext = curHead.next.get();
//			AtomicReference<QNode<E>> next = curHead.next;
//			if (next.get() == null) {
//				return null; /// queue is empty
//			}
//			if (curHead == head.get()) {
//				if (headNext != null) {
//					tail.compareAndSet(curHead, headNext);
//				}else {
//					if (curHead.next.compareAndSet(null, newNode)) {
//						tail.compareAndSet(headNext, newNode);
//						return new E();
//					}
//				}
//			}
//		}
	}
}
