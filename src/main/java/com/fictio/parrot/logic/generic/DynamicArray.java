package com.fictio.parrot.logic.generic;

public interface DynamicArray<E> {
    public void add(E e);
    public E get(int index);
    public int size();
    // 插入新的,返回旧的
    public E set(E e, int index);
}
