package interviews.arrays;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Heap.
 *
 * @author Francois Rousseau
 */
public class Heap<E> implements Iterable<E> {

  private final List<E> heap;
  private final Comparator<E> comparator;

  public Heap(Comparator<E> comparator) {
    this.comparator = comparator;
    heap = new ArrayList<E>();
  }

  public Heap(Collection<E> collection, Comparator<E> comparator) {
    this(comparator);
    for(E e: collection) {
      heap.add(e);
    }
    heapify();
  }

  /**
   * Insert the specified element into this heap.
   */
  public boolean add(E e) throws NullPointerException {
    if(e == null) {
      throw new NullPointerException();
    }
    heap.add(e);  // we add the element at the end of the array
    bubbleUp(size() - 1);  // we "bubble it up" until it reaches its position in the heap
    return true;
  }

  /**
   * Remove all of the elements from this heap.
   */
  public void clear() {
    heap.clear();
  }

  /**
   * Return the comparator used to order the elements in this heap.
   */
  public Comparator<E> comparator() {
    return comparator;
  }

  /**
   * Return true if this heap contains the specified element.
   */
  public boolean contains(E e) {
    return contains(e, 0);
  }

  /**
   * Retrieve, but does not remove, the head of this heap.
   * @throws: NoSuchElementException if the heap is empty.
   */
  public E element() {
    if(isEmpty()) {
      throw new NoSuchElementException();
    }
    return get(0);
  }

  /**
   * Is the heap empty?
   */
  public boolean isEmpty() {
    return heap.size() == 0;
  }

  /**
   * Return an iterator over the elements in this heap.
   */
  @Override
  public Iterator<E> iterator() {
    return heap.iterator();
  }

  /**
   * Insert the specified element into this heap.
   */
  public boolean offer(E e) throws NullPointerException {
    return add(e);
  }

  /**
   * Retrieve, but does not remove, the head of this heap.
   * or returns null if this heap is empty.
   */
  public E peek() {
    try {
      return element();
    } catch(NoSuchElementException e) {
      return null;
    }
  }

  /**
   * Retrieve and remove the head of this heap, or return null if this heap is empty.
   */
  public E poll() {
    try {
      return remove();
    } catch(NoSuchElementException e) {
      return null;
    }
  }

  /**
   * Retrieve and remove the head of this heap.
   * @throws: NoSuchElementException if the heap is empty.
   */
  public E remove() throws NoSuchElementException {
    if(isEmpty()) {
      throw new NoSuchElementException();
    }
    swap(0, size()-1);  // we swap the element to remove with the element at the end of the array
    final E head = heap.remove(size()-1);  // the head is now at the end
    if(size() > 0) {  // if the heap is not empty
      bubbleDown(0);  // we bubble down the element previously at the end
    }
    return head;
  }

  /**
   * Remove a single instance of the specified element from this heap, if it is present.
   */
  public boolean remove(E e) {
    return remove(e, 0);
  }

  /**
   * Return the number of elements in this collection.
   */
  public int size() {
    return heap.size();
  }

  /**
   * Return an array containing all of the elements in this heap.
   */
  public Object[] toArray() {
    return heap.toArray();
  }

  /**
   * Return an array containing all of the elements in this heap.
   * The runtime type of the returned array is that of the specified array.
   */
  public <T> T[] toArray(T[] a) throws ArrayStoreException, NullPointerException {
    return heap.toArray(a);
  }

  @Override
  public String toString() {
    final StringBuffer buffer = new StringBuffer();
    int power = 1;
    for (E e : heap) {
      buffer.append(e + " ");
      power++;
      if((power & power-1) == 0) {  // power of 2;
        buffer.deleteCharAt(buffer.length() - 1).append("\n");
      }
    }
    return buffer.deleteCharAt(buffer.length() - 1).toString();
  }

  //////////////////
  // Private methods
  //////////////////

  private int bubbleUp(int index) {
    final int parent = parent(index);
    if(comparator.compare(get(index), get(parent)) < 0) {
      swap(index, parent);
      return bubbleUp(parent);
    }
    return index;
  }

  private int bubbleDown(int index) {
    int child = index;
    final int left = left(index);
    if(left < size()) {  // there is a left child
      if(comparator.compare(get(child), get(left)) > 0) {
        child = left;  // we should at least swap with the left child
      }
      final int right = left + 1;
      if(right < size()) {  // there is a right child
        if(comparator.compare(get(child), get(right)) > 0) {
          child = right;  // we should swap with the right child
        }
      }
      if(child != index) {  // there is one child with a bigger value
        swap(index, child);
        return bubbleDown(child);
      }
    }
    return index;
  }

  private boolean contains(E e, int index) {
    if(comparator.compare(e, get(index)) == 0) {
      return true;
    }
    final int left = left(index);
    if(left < size()) {  // there is a left child
      if(contains(e, left)) {
        return true;
      }
      final int right = left + 1;
      if(right < size()) {  // there is a right child
        if(contains(e, right)) {
          return true;
        }
      }
    }
    return false;
  }

  private E get(int index) {
    return heap.get(index);
  }

  /**
   * Make the heap in linear time.
   * Bottom-up leads to linear time (O(N)) when top-down leads to linearitmic (O(NlogN)) time.
   * Thus we use this.bubbleDown(i) instead of this.add(e) in the inner loop.
   *
   * Linear time complexity comes from the number of bubbleDown necessary.
   * The last N/2 elements are leaves so 0 x N/2
   * The next N/4 elements are nodes with remaining depth 1 so 1 x N/4
   * The next N/8 elements are nodes with remaining depth 2 so 2 x N/8
   * ...
   * The root is a node with remaining depth logN so log(N) x 1
   * Overall it's 0 x N/2 + 1 x N/4 + 2 x N/8 + ... + log(N) x 1
   *
   * = N * sum_{i = 1}^{log(N)} (i-1)/2^i
   * = N * sum_{i = 1}^{log(N)} (i-1)*x^i           (x=1/2) variable x
   * = N * sum_{i = 0}^{log(N)-1} i*x^(i+1)         (x=1/2) index shifting
   * = N * x^2 * sum_{i = 0}^{log(N)-1} i*x^(i-1)   (x=1/2) index shifting
   * = N * x^2 * sum_{i = 0}^{log(N)-1} d(x^i)/dx   (x=1/2) derivation formula
   * = N * x^2 * d(sum_{i = 0}^{log(N)-1} x^i)/dx   (x=1/2) linearity of derivation
   * = N * x^2 * d([x^log(N) - 1]/[x - 1])/dx       (x=1/2) sum of geometric series
   * = N * x^2 * (log(N)*x^{log(N)-1}*[x - 1] - [x^log(N) - 1])/(x - 1)^2  (x=1/2) derivation
   * = N * (log(N)*1/N - 1/N + 1)
   * = N - log(N) - 1
   * = O(N)
   */
  private void heapify() {
    for(int i = (size() - 1)/2; i >= 0; i--) {  // the last N/2 elements are leaves
      bubbleDown(i);
    }
  }

  private int left(int index) {
    return 2 * index + 1;
  }

  private int parent(int index) {
    return (index - 1) / 2;  // if index == 0 (root), return 0
  }

  private boolean remove(E e, int index) {
    if(comparator.compare(e, get(index)) == 0) {
      swap(index, size() - 1);  // swap the element at the end
      heap.remove(size() - 1);  // remove it
      heapify();  // much easier and guaranteed linear time
      return true;
    }
    final int left = left(index);
    if(left < size()) {  // there is a left child
      if(remove(e, left)) {
        return true;
      }
      final int right = left + 1;
      if(right < size()) {  // there is a right child
        if(remove(e, right)) {
          return true;
        }
      }
    }
    return false;
  }

  private void swap(int from, int with) {
    Collections.swap(heap, from, with);
  }
}
