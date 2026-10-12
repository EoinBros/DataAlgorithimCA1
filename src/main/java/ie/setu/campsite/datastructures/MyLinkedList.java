package ie.setu.campsite.datastructures;

import java.util.Iterator;
import java.util.NoSuchElementException;

// my own linked list since we can't use ArrayList/LinkedList for the CA
public class MyLinkedList<T> implements Iterable<T> {
    private Node<T> head;
    private Node<T> tail; // keeping track of the end so add() doesn't have to loop
    private int size;

    public MyLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    // adds to the end of the list
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) { // empty list
            head = newNode;
            tail = newNode;
        } else {
            tail.setNext(newNode);
            tail = newNode;
        }
        size++;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    // walks along the list until it gets to the index
    public T get(int index) {
        checkIndex(index);
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        return current.getData();
    }

    public boolean contains(T data) {
        return indexOf(data) != -1;
    }

    // returns -1 if it's not found
    public int indexOf(T data) {
        Node<T> current = head;
        int index = 0;
        while (current != null) {
            if (data == null ? current.getData() == null : data.equals(current.getData())) {
                return index;
            }
            current = current.getNext();
            index++;
        }
        return -1;
    }

    public T remove(int index) {
        checkIndex(index);
        Node<T> removed;

        if (index == 0) { // removing the first one, just move head along
            removed = head;
            head = head.getNext();
            if (head == null) {
                tail = null;
            }
        } else {
            // need the node before the one being removed
            Node<T> previous = head;
            for (int i = 0; i < index - 1; i++) {
                previous = previous.getNext();
            }
            removed = previous.getNext();
            previous.setNext(removed.getNext()); // skip over it
            if (removed == tail) { // removed the last one so tail has to move back
                tail = previous;
            }
        }

        size--;
        return removed.getData();
    }

    public boolean remove(T data) {
        int index = indexOf(data);
        if (index == -1) {
            return false;
        }
        remove(index);
        return true;
    }

    // just drop the references and java cleans up the rest
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    // lets me use for-each loops on the list
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                T data = current.getData();
                current = current.getNext();
                return data;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = head;
        while (current != null) {
            sb.append(current.getData());
            if (current.getNext() != null) {
                sb.append(", ");
            }
            current = current.getNext();
        }
        return sb.append("]").toString();
    }
}
