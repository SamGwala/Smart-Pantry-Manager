# Smart Pantry Manager

An Android application (Java) that helps reduce food waste by tracking the
ingredients a user has at home and suggesting only the recipes they can
make **right now**, using strictly the ingredients already in their pantry.

## Why this app?

Many people throw away food because they forget what they have, or they
buy extra ingredients for a recipe instead of using what's already in the
kitchen. Smart Pantry Manager solves this with a strict-matching rule: a
recipe is only suggested if every single ingredient it needs is already in
the pantry, in a large enough quantity. No partial matches, no "almost"
recipes cluttering the main list.

## Features

- Add, edit, and delete pantry ingredients (name, quantity, unit, optional expiry date)
- Pantry list screen backed by a RecyclerView and a custom adapter
- A seeded collection of 18 recipes, each with an ingredient list and method
- Suggested Recipes screen applying the strict-matching algorithm
- An "Almost There" list showing recipes missing exactly one ingredient
- Recipe detail screen with full ingredients and steps
- Settings screen for expiry alerts and unit preference
- Bottom navigation between Pantry, Recipes, and Settings

## Database

**SQLite**, implemented locally on-device using `SQLiteOpenHelper`
(`DatabaseHelper.java`). Chosen because:
- The brief's own module content covers SQLiteOpenHelper directly, and it
  keeps the app fully offline with no backend to configure or pay for.
- The data (a personal pantry) is inherently local to one user/device, so
  cloud sync isn't a core requirement for this use case.
- It's straightforward to demonstrate genuine CRUD and persistence
  (close and reopen the app) without any external dependency risk on
  demo day.

Three tables:
- `pantry_items` — the user's ingredients (full CRUD)
- `recipes` — the seeded recipe book (read-only in the app)
- `recipe_ingredients` — the ingredients each recipe requires (read-only)

## Strict-Matching Algorithm

See `database/RecipeMatcher.java`. For each recipe, it loops through every
required ingredient and checks whether the pantry has that ingredient
(name-normalised for simple plural differences, e.g. "tomato" vs
"tomatoes") in at least the needed quantity. If even one required
ingredient is missing, the recipe is excluded from the main suggestions
list. A recipe missing exactly one ingredient is shown separately in an
"Almost There" list.

## Setup / Run Instructions

1. Clone this repository.
2. Open the project folder in **Android Studio** (Hedgehog or newer recommended).
3. Let Gradle sync (it will download the AndroidX/Material dependencies).
4. Run on an emulator or physical device with **minSdk 24 (Android 7.0)** or higher.
5. On first launch, the app seeds its own recipe book automatically no
   extra setup needed.

## Tech Stack

- Java (no Kotlin)
- Android Studio / Gradle
- SQLite via `SQLiteOpenHelper`
- RecyclerView + custom Adapters
- Material Components (BottomNavigationView, TextInputLayout, FAB)


