# Library Management System

A fully-featured Library Management System implemented in Java, demonstrating solid Object-Oriented Programming principles, SOLID design, and multiple Gang-of-Four design patterns.

---

## Table of Contents

- [Features](#features)
- [Project Structure](#project-structure)
- [Design Patterns Used](#design-patterns-used)
- [SOLID Principles](#solid-principles)
- [Class Diagram](#class-diagram)
- [Getting Started](#getting-started)
- [Running the Demo](#running-the-demo)
- [Key Design Decisions](#key-design-decisions)

---

## Features

### Core
- **Book Management** – Add, remove, update, and search books (by title, author, ISBN, or genre)
- **Patron Management** – Register patrons, update profiles, track borrowing history
- **Lending Process** – Checkout and return books with configurable loan periods
- **Inventory Tracking** – Real-time tracking of available vs. checked-out books

### Extensions
- **Multi-Branch Support** – Multiple library branches with independent inventories
- **Book Transfers** – Transfer available books between branches
- **Reservation System** – Queue-based reservations for checked-out books
- **Automatic Notifications** – Patrons notified when reserved books become available
- **Recommendation System** – Three pluggable strategies: genre-based, author-based, popularity-based

---

## Project Structure

```
src/main/java/com/library/
├── Main.java                          # Entry point / demo
├── LibrarySystem.java                 # Facade — wires all subsystems
│
├── model/
│   ├── Book.java                      # Book entity (with Builder)
│   ├── BookStatus.java                # Enum: AVAILABLE, CHECKED_OUT, RESERVED, …
│   ├── Patron.java                    # Patron entity
│   ├── LoanRecord.java                # Lending transaction record
│   ├── LibraryBranch.java             # Branch entity
│   └── Reservation.java              # Reservation entity
│
├── service/
│   ├── BookService.java               # Book CRUD + search
│   ├── PatronService.java             # Patron CRUD
│   ├── LendingService.java            # Checkout / return / reservations
│   ├── BranchService.java             # Branch management + transfers
│   └── RecommendationService.java     # Recommendation engine
│
├── repository/
│   ├── BookRepository.java            # Interface
│   ├── InMemoryBookRepository.java    # In-memory implementation
│   ├── PatronRepository.java          # Interface
│   └── InMemoryPatronRepository.java  # In-memory implementation
│
├── pattern/
│   ├── observer/
│   │   ├── LibraryObserver.java        # Observer interface
│   │   ├── LibraryEvent.java           # Event data object
│   │   ├── LibraryEventPublisher.java  # Subject (Observable)
│   │   ├── NotificationObserver.java   # Patron notification handler
│   │   └── AuditLogObserver.java       # Audit trail handler
│   │
│   ├── strategy/
│   │   ├── SearchStrategy.java         # Search strategy interface
│   │   ├── SearchStrategies.java       # Title / Author / ISBN / Genre strategies
│   │   ├── RecommendationStrategy.java # Recommendation strategy interface
│   │   └── RecommendationStrategies.java # Genre / Author / Popularity strategies
│   │
│   └── factory/
│       └── BookFactory.java            # Factory for creating Books
│
└── exception/
    ├── LibraryException.java
    ├── BookNotFoundException.java
    ├── PatronNotFoundException.java
    ├── BookNotAvailableException.java
    ├── DuplicateBookException.java
    ├── DuplicatePatronException.java
    └── BranchNotFoundException.java
```

---

## Design Patterns Used

### 1. Observer Pattern
**Location:** `pattern/observer/`

Used to decouple event producers (services) from event consumers (notifications, audit log).

- `LibraryEventPublisher` — the Subject; holds a list of observers and dispatches events
- `LibraryObserver` — the Observer interface with `onEvent(LibraryEvent)`
- `NotificationObserver` — sends patron notifications (e.g., reservation available)
- `AuditLogObserver` — maintains a full immutable audit trail of all library events

```java
eventPublisher.subscribe(new NotificationObserver());
eventPublisher.subscribe(new AuditLogObserver());
// Automatically notified on checkout, return, reservation, etc.
```

### 2. Strategy Pattern
**Location:** `pattern/strategy/`

Allows search and recommendation algorithms to be swapped at runtime without changing the calling code.

**Search strategies:**
- `TitleSearchStrategy` — partial, case-insensitive title match
- `AuthorSearchStrategy` — partial, case-insensitive author match
- `IsbnSearchStrategy` — exact ISBN match
- `GenreSearchStrategy` — partial genre match

```java
library.books().setSearchStrategy(new AuthorSearchStrategy());
library.books().search("Orwell"); // uses author strategy
```

**Recommendation strategies:**
- `GenreBasedRecommendation` — suggests books in patron's preferred genres
- `AuthorBasedRecommendation` — suggests books by authors the patron has read before
- `PopularityBasedRecommendation` — suggests most-borrowed books the patron hasn't read

### 3. Factory Pattern
**Location:** `pattern/factory/BookFactory.java`

Centralises and simplifies `Book` object creation.

```java
Book b = BookFactory.createFictionBook("isbn", "Title", "Author", 2020);
```

### 4. Builder Pattern
**Location:** `model/Book.java` (inner `Builder` class)

Provides a readable, fluent API for constructing `Book` objects with optional fields.

```java
Book b = new Book.Builder("978-...")
    .title("1984")
    .author("George Orwell")
    .publicationYear(1949)
    .genre("Dystopian Fiction")
    .build();
```

### 5. Facade Pattern
**Location:** `LibrarySystem.java`

`LibrarySystem` wires all subsystems and provides a single entry point, hiding the complexity of service and repository wiring from clients.

### 6. Repository Pattern
**Location:** `repository/`

Abstracts all data-access logic behind interfaces (`BookRepository`, `PatronRepository`). The in-memory implementations can be replaced with database-backed implementations without changing any service code.

---

## SOLID Principles

| Principle | Application |
|---|---|
| **S** ingle Responsibility | Each service handles one concern: `BookService` = books only, `LendingService` = loans only, `PatronService` = patrons only |
| **O** pen/Closed | `SearchStrategy` and `RecommendationStrategy` interfaces allow new algorithms without modifying existing code |
| **L** iskov Substitution | All strategy implementations are substitutable for their interfaces without breaking callers |
| **I** nterface Segregation | `BookRepository` and `PatronRepository` are separate; observers implement `LibraryObserver` only |
| **D** ependency Inversion | Services depend on repository interfaces, not concrete classes; strategies are injected, not hard-coded |

---

## Class Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                         LibrarySystem (Facade)                       │
│  +books(): BookService                                               │
│  +patrons(): PatronService                                           │
│  +lending(): LendingService                                          │
│  +branches(): BranchService                                          │
│  +recommendations(): RecommendationService                           │
└──────────────────────────────┬──────────────────────────────────────┘
                                │ creates & wires
          ┌─────────────────────┼──────────────────────┐
          │                     │                      │
          ▼                     ▼                      ▼
   ┌─────────────┐    ┌──────────────────┐   ┌──────────────────────┐
   │ BookService │    │  PatronService   │   │   LendingService     │
   │─────────────│    │──────────────────│   │──────────────────────│
   │+addBook()   │    │+registerPatron() │   │+checkOut()           │
   │+removeBook()│    │+updatePatron()   │   │+returnBook()         │
   │+updateBook()│    │+findById()       │   │+reserveBook()        │
   │+search()    │    │+getAllPatrons()  │   │+cancelReservation()  │
   │+searchWith()│    └────────┬─────────┘   │+getOverdueLoans()    │
   └──────┬──────┘             │             └──────────────────────┘
          │ uses               │ uses
          ▼                    ▼
  ┌────────────────┐  ┌─────────────────┐
  │ BookRepository │  │PatronRepository │   (interfaces)
  │ <<interface>>  │  │ <<interface>>   │
  └────────┬───────┘  └────────┬────────┘
           │                   │
           ▼                   ▼
  ┌──────────────────┐ ┌──────────────────────┐
  │InMemoryBookRepo  │ │InMemoryPatronRepo     │
  └──────────────────┘ └──────────────────────┘

─── Models ────────────────────────────────────────────────────────────

 ┌────────────────────┐    ┌─────────────────────┐
 │       Book         │    │       Patron         │
 │────────────────────│    │─────────────────────│
 │-isbn: String       │    │-patronId: String     │
 │-title: String      │    │-name: String         │
 │-author: String     │    │-email: String        │
 │-publicationYear    │    │-phone: String        │
 │-genre: String      │    │-borrowingHistory     │
 │-status: BookStatus │    │  : List<LoanRecord>  │
 │────────────────────│    │-reservedIsbns: List  │
 │+isAvailable()      │    │-preferredGenres: List│
 │  [Builder inner]   │    │─────────────────────│
 └────────────────────┘    │+addLoanRecord()      │
                           │+addReservation()     │
                           └──────────────────────┘

 ┌────────────────────┐    ┌──────────────────────┐
 │    LoanRecord      │    │    Reservation       │
 │────────────────────│    │──────────────────────│
 │-loanId: String     │    │-reservationId: String│
 │-isbn: String       │    │-isbn: String         │
 │-patronId: String   │    │-patronId: String     │
 │-checkoutDate       │    │-reservationDate      │
 │-dueDate: LocalDate │    │-status: enum         │
 │-returnDate         │    │──────────────────────│
 │────────────────────│    │+isActive(): boolean  │
 │+isActive(): boolean│    └──────────────────────┘
 │+isOverdue(): boolean│
 └────────────────────┘

─── Observer Pattern ──────────────────────────────────────────────────

 ┌──────────────────────────┐
 │  LibraryEventPublisher   │◄────────── subscribed by ──────────────┐
 │  (Subject)               │                                        │
 │  -observers: List        │        ┌───────────────────────┐       │
 │  +subscribe(observer)    │        │   LibraryObserver     │       │
 │  +publish(event)         │        │   <<interface>>       │       │
 └──────────────────────────┘        │   +onEvent(event)     │       │
                                     └───────────┬───────────┘       │
                                                 │ implements        │
                              ┌──────────────────┼────────────┐      │
                              ▼                  ▼            │      │
                  ┌──────────────────┐ ┌──────────────────┐   │      │
                  │NotificationObsvr │ │AuditLogObserver  │───┘      │
                  │+onEvent()        │ │+onEvent()        │          │
                  └──────────────────┘ │+getAuditLog()    │          │
                                       └──────────────────┘          │
                                                                      │
─── Strategy Pattern ──────────────────────────────────────────────────

  ┌────────────────────┐         ┌───────────────────────────┐
  │  SearchStrategy    │         │  RecommendationStrategy   │
  │  <<interface>>     │         │  <<interface>>            │
  │  +search(books,q)  │         │  +recommend(patron,books) │
  └────────┬───────────┘         └──────────────┬────────────┘
           │ implements                         │ implements
  ┌────────┴─────────────────┐   ┌──────────────┴────────────────────┐
  │TitleSearchStrategy       │   │GenreBasedRecommendation           │
  │AuthorSearchStrategy      │   │AuthorBasedRecommendation          │
  │IsbnSearchStrategy        │   │PopularityBasedRecommendation      │
  │GenreSearchStrategy       │   └───────────────────────────────────┘
  └──────────────────────────┘

─── Factory Pattern ───────────────────────────────────────────────────

  ┌─────────────────────────────────────────────────────┐
  │                    BookFactory                       │
  │  +createBook(isbn, title, author, year, genre)       │
  │  +createBook(isbn, title, author, year)              │
  │  +createFictionBook(isbn, title, author, year)       │
  │  +createNonFictionBook(isbn, title, author, year)    │
  └──────────────────────────────────────────┬──────────┘
                                             │ creates
                                             ▼
                                     ┌──────────────┐
                                     │ Book.Builder │
                                     └──────────────┘
```

---

## Getting Started

### Prerequisites

- Java 11 or higher
- No external dependencies — pure Java standard library

### Compile

```bash
# From the project root
find src/main/java -name "*.java" > sources.txt
javac -d out @sources.txt
```

### Run

```bash
java -cp out com.library.Main
```

---

## Running the Demo

`Main.java` demonstrates the full system:

1. Creates two library branches (MAIN, EAST)
2. Adds 6 books using `BookFactory`
3. Registers 3 patrons
4. Demonstrates all 4 search strategies
5. Checks out books and tracks inventory
6. Places a reservation on a checked-out book
7. Returns the book → auto-notifies the waiting patron
8. Transfers a book between branches
9. Generates blended recommendations for a patron
10. Updates and removes a book
11. Prints the last 5 audit log entries

---

## Key Design Decisions

**Why in-memory repositories?**
The assignment focuses on OOP and design patterns, not persistence. All repository logic is behind interfaces so swapping to JPA/Hibernate or a file-based store requires zero service changes.

**Why a Facade (`LibrarySystem`)?**
Clients shouldn't need to wire services and repositories together. The Facade reduces coupling and provides a single bootstrapping point.

**Why `LinkedList` for reservation queues?**
Reservations are FIFO (first come, first served). `LinkedList` gives O(1) enqueue and dequeue, which is semantically correct for a queue.

**Why separate `LoanRecord` from `Patron`?**
Loan records belong to the lending domain, not the patron identity domain. Separating them keeps `Patron` focused on identity and makes the lending history immutable and auditable.
