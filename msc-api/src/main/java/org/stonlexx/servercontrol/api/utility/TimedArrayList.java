package org.stonlexx.servercontrol.api.utility;

import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TimedArrayList<T>
        extends ArrayList<T>
        implements List<T> {

    transient long[] elementsMillis = new long[1024 * 1024];

    @Override
    public synchronized boolean add(@NonNull T element) {
        if (super.add(element)) {

            int index = indexOf(element);
            elementsMillis[index] = System.currentTimeMillis();

            return true;
        }

        return false;
    }

    @Override
    public synchronized T remove(int index) {
        T remove = super.remove(index);

        int numMoved = (size() - index - 1);
        if (numMoved > 0) {
            System.arraycopy(elementsMillis, index + 1, elementsMillis, index, numMoved);
        }

        elementsMillis[size()] = 0;
        return remove;
    }

    @Override
    public synchronized boolean remove(Object object) {
        int index = indexOf(object);
        if (index >= 0 && super.remove(object)) {

            int numMoved = (size() - index - 1);
            if (numMoved > 0) {
                System.arraycopy(elementsMillis, index + 1, elementsMillis, index, numMoved);
            }

            elementsMillis[size()] = 0;
            return true;
        }

        return false;
    }

    @Override
    public synchronized T set(int index, @NonNull T element) {
        T oldElement = super.set(index, element);
        elementsMillis[index] = System.currentTimeMillis();

        return oldElement;
    }

    @Override
    public synchronized String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");

        for (T element : this) {
            stringBuilder.append(getElementTime(element))
                    .append("@")
                    .append(element.toString())
                    .append(", ");
        }

        return stringBuilder.substring(0, stringBuilder.length() - 2).concat("]");
    }


    /**
     * Получить время добавления/изменения элмента
     * по его индексу
     *
     * @param index - индекс элемента
     */
    public synchronized long getElementTime(int index) {
        return elementsMillis[index];
    }

    /**
     * Получить время добавления/изменения элмента
     * по объекту элемента
     *
     * @param element - объект элемента
     */
    public synchronized long getElementTime(@NonNull T element) {
        int index = indexOf(element);
        return index <= 0 ? index : getElementTime(index);
    }

}
