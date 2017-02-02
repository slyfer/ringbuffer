package org.sample;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by ccardone on 02/02/17.
 */
public class RingBuffer<E> implements Queue<E> {

    private Semaphore semaphore;
    private AtomicInteger readCount;
    private AtomicInteger writeCount;

    private E[] objects;

    private final int capacity;

    public RingBuffer(int capacity) {
        this.capacity = capacity;

        objects = (E[]) new Object[capacity];

        readCount = new AtomicInteger(0);
        writeCount = new AtomicInteger(0);
        semaphore = new Semaphore(capacity);
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(E e) {
        return false;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public boolean offer(E e) {
        try {
            semaphore.acquire();
            int andIncrement = writeCount.getAndIncrement();
            objects[andIncrement % capacity] = e;
            return true;
        } catch (InterruptedException e1) {

        }
        return false;
    }

    @Override
    public E remove() {
        return null;
    }

    @Override
    public E poll() {

            semaphore.release();
            int andIncrement = readCount.getAndIncrement();
           return objects[andIncrement % capacity];



    }

    @Override
    public E element() {
        return null;
    }

    @Override
    public E peek() {
        return null;
    }
}
