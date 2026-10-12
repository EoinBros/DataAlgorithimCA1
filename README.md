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

## Running it

Needs Java 17+. In IntelliJ go to Maven → Plugins → javafx → javafx:run.

Tests are in `src/test` – run them with Maven → Lifecycle → test.
