package com.fictio.parrot.logic.generic;

import java.util.Arrays;

/**
 * @ClassName: DynamicArray.java
 * @Description: 模仿ArrayList,实现动态数组容器
 * @param <E>
 */
public class DynamicArrayImpl<E> implements DynamicArray<E> {

    private Object[] elementData;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    public DynamicArrayImpl() {
        this.elementData = new Object[DEFAULT_CAPACITY];
    }

    private void ensureCapacity(int minCapacity) {
        int oldCapacity = this.elementData.length;
        if(minCapacity <= oldCapacity) return;
        int newCapacity = oldCapacity * 2;
        if(newCapacity < minCapacity) newCapacity = minCapacity;
        this.elementData = Arrays.copyOf(elementData, newCapacity);
    }

    @Override
    public void add(E e) {
        ensureCapacity(this.size + 1);
        elementData[size ++] = e;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E get(int index) {
        return (E) elementData[index];
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public E set(E e, int index) {
        E oldValue = get(index);
        elementData[index] = e;
        return oldValue;
    }

}
