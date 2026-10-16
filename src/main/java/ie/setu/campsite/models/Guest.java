package ie.setu.campsite.models;

import ie.setu.campsite.datastructures.MyLinkedList;

import java.time.LocalDate;

// a guest can have loads of bookings across different campsites
public class Guest {
    private int id;
    private String name;
    private LocalDate dateOfBirth;
    private String contact;
    private String notes;
    private MyLinkedList<Booking> bookings = new MyLinkedList<>();

    public Guest(int id, String name, LocalDate dateOfBirth, String contact, String notes) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.contact = contact;
        this.notes = notes;
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public boolean removeBooking(Booking booking) {
        return bookings.remove(booking);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public MyLinkedList<Booking> getBookings() { return bookings; }

    @Override
    public String toString() {
        return "#" + id + " " + name;
    }
}
