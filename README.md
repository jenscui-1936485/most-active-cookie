# Most Active Cookie

A command-line Java application that processes a cookie log file and returns the most active cookie(s) for a given day.

---

## Problem Description

Given a CSV log file with the following format:


The program returns the most active cookie(s) (i.e., the cookie(s) that appear most frequently) for a specified date.

cookie,timestamp

AtY0laUfhglK3lC7,2018-12-09T14:19:00+00:00

---

## Features

- Parses CSV log file
- Filters cookies by a specific UTC date
- Returns one or multiple most active cookies
- Optimized using sorted log assumption (early termination)
- Built using Maven
- Includes unit tests (JUnit)

---

## Build Instructions

### Prerequisites
- Java 17+
- Maven 3+

### Build the project

```bash
mvn clean package
```

## Running the application

### Option 1： 
```
java -jar target/most-active-cookie-1.0-SNAPSHOT.jar cookie_log.csv -d 2018-12-09
```

### Option 2:
```
./most_active_cookie cookie_log.csv -d 2018-12-09
```

