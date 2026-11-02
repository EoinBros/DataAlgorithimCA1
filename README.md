# Campsite Management System

DSA1 CA Exercise 1 – Eoin Brosnan, SETU Waterford

A JavaFX app for running multiple campsites. Everything's stored in my own linked list, no ArrayList or built-in collections.

## What it does

- Add campsites, camping areas, plots/pitches and guests
- Book a guest into a camping area and it picks a suitable free plot
- Cancel bookings and view pending ones by campsite or overall
- Look up a guest and see all their bookings
- Drill down from campsite to area to plot to booking
- Smart find to suggest somewhere to stay
- Remove a plot and move its bookings elsewhere
- Reset everything, and save/load between runs

## Using it

The tabs down the left go in the order you'd set things up:

1. **Campsites** – add a campsite. Save, Load and Reset are here too.
2. **Areas** – pick a campsite and add areas to it
3. **Plots** – pick a campsite and area, add plots, or remove one
4. **Guests** – add guests and view a guest's record
5. **Bookings** – book a guest into an area, or cancel a booking
6. **View** – pending bookings, plus the drill down tree
7. **Smart Find** – say what you want and it suggests a plot

## How it's laid out

- `datastructures` – my linked list (`MyLinkedList` and `Node`)
- `models` – Campsite, CampingArea, Plot, Guest, Booking
- `system` – `CampsiteManager`, where all the logic is
- `controllers` – one controller per tab, plus the FXML files in `resources`

## Running it

Needs Java 17+. In IntelliJ go to Maven → Plugins → javafx → javafx:run.

Tests are in `src/test` – run them with Maven → Lifecycle → test.
