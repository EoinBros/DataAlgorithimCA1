package ie.setu.campsite.datastructures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MyLinkedListTest {
    private MyLinkedList<String> list;

    // new empty list for each test
    @BeforeEach
    void setUp() {
        list = new MyLinkedList<>();
    }

    @Test
    void newListIsEmpty() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void addIncreasesSize() {
        list.add("A");
        list.add("B");
        assertEquals(2, list.size());
        assertFalse(list.isEmpty());
    }

    @Test
    void getReturnsItemsInOrderAdded() {
        list.add("A");
        list.add("B");
        list.add("C");
        assertEquals("A", list.get(0));
        assertEquals("B", list.get(1));
        assertEquals("C", list.get(2));
    }

    @Test
    void getWithBadIndexThrows() {
        list.add("A");
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
    }

    @Test
    void removeFirstItem() {
        list.add("A");
        list.add("B");
        assertEquals("A", list.remove(0));
        assertEquals(1, list.size());
        assertEquals("B", list.get(0));
    }

    @Test
    void removeMiddleItem() {
        list.add("A");
        list.add("B");
        list.add("C");
        assertEquals("B", list.remove(1));
        assertEquals("[A, C]", list.toString());
    }

    // making sure tail gets updated when the last item is removed
    @Test
    void removeLastItemThenAddStillWorks() {
        list.add("A");
        list.add("B");
        list.remove(1);
        list.add("C");
        assertEquals("[A, C]", list.toString());
    }

    @Test
    void removeByValue() {
        list.add("A");
        list.add("B");
        assertTrue(list.remove("A"));
        assertFalse(list.remove("Z"));
        assertEquals(1, list.size());
    }

    @Test
    void containsAndIndexOf() {
        list.add("A");
        list.add("B");
        assertTrue(list.contains("B"));
        assertFalse(list.contains("Z"));
        assertEquals(1, list.indexOf("B"));
        assertEquals(-1, list.indexOf("Z"));
    }

    @Test
    void clearEmptiesTheList() {
        list.add("A");
        list.add("B");
        list.clear();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
        list.add("C");
        assertEquals("C", list.get(0));
    }

    @Test
    void forEachLoopVisitsEveryItem() {
        list.add("A");
        list.add("B");
        list.add("C");
        StringBuilder result = new StringBuilder();
        for (String s : list) {
            result.append(s);
        }
        assertEquals("ABC", result.toString());
    }
}
