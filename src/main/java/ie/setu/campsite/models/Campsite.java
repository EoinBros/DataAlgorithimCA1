package ie.setu.campsite.models;

import ie.setu.campsite.datastructures.MyLinkedList;

// top level - a campsite holds its camping areas
public class Campsite {
    private String name;
    private String address;
    private int stars;
    private String contact;
    private MyLinkedList<String> facilities = new MyLinkedList<>();
    private MyLinkedList<CampingArea> areas = new MyLinkedList<>();

    public Campsite(String name, String address, int stars, String contact) {
        this.name = name;
        this.address = address;
        setStars(stars);
        this.contact = contact;
    }

    // area ids have to be unique within the campsite, so check first
    public boolean addArea(CampingArea area) {
        if (getArea(area.getId()) != null) {
            return false;
        }
        areas.add(area);
        return true;
    }

    public CampingArea getArea(String id) {
        for (CampingArea a : areas) {
            if (a.getId().equalsIgnoreCase(id)) {
                return a;
            }
        }
        return null;
    }

    public void addFacility(String facility) {
        facilities.add(facility);
    }

    public boolean hasFacility(String facility) {
        for (String f : facilities) {
            if (f.equalsIgnoreCase(facility)) {
                return true;
            }
        }
        return false;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getStars() { return stars; }

    // keeping stars between 1 and 5
    public void setStars(int stars) {
        if (stars < 1) stars = 1;
        if (stars > 5) stars = 5;
        this.stars = stars;
    }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public MyLinkedList<String> getFacilities() { return facilities; }
    public MyLinkedList<CampingArea> getAreas() { return areas; }

    @Override
    public String toString() {
        return name + " (" + stars + "*) - " + address;
    }
}
