## OceanView Resort Reservation System

OceanView Resort Reservation System is a **Java Servlet–based web application** for managing resort operations, including rooms, guests, reservations, billing, discounts, and reporting.  
The project is built with **Maven**, packaged as a **WAR**, and is intended to be deployed to a Java application server (for example Apache Tomcat).

### Tech stack

- **Language**: Java 11
- **Build tool**: Maven
- **Packaging**: `war`
- **Web layer**: Java Servlets & JSP (JSTL)
- **Database**: MySQL (via `mysql-connector-java`)
- **Connection pool**: HikariCP
- **Messaging**: Apache Kafka (for email notifications and events)
- **Reporting**: OpenPDF (PDF), Apache POI (Excel)
- **Testing**: JUnit 4 & 5, Mockito, JaCoCo for coverage

### Main features

- **Room & Room Type Management**: CRUD operations, occupancy and rate validation.
- **Guest Management**: Capture and manage guest details.
- **Reservation Management**: Create, modify, and cancel reservations with validation.
- **Billing & Discounts**: Bill generation and discount strategies.
- **Admin & Reservationist Dashboards**: KPIs and operational views.
- **Reporting**: Export reports to PDF and Excel formats.
- **Notifications**: Email notifications using Kafka-based messaging.
- **Security & Reliability**: Input validation, XSS-safe output utilities, and test coverage checks.

### Getting started

#### Prerequisites

- Java 11+
- Maven 3.6+
- MySQL database
- A Java EE–compatible application server (e.g., Tomcat 9+)

#### Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/AmiruMallawarachchi/OceanViewResort_Reservation_System.git
   cd OceanViewResort_Reservation_System
   ```

2. **Configure database**

   - Update `src/main/resources/db.properties` with your MySQL connection details (URL, username, password, pool size, etc.).
   - Ensure the database schema is created (tables, constraints, and initial data) according to your SQL scripts or migration process.

3. **Build the project**

   ```bash
   mvn clean package
   ```

   This will:

   - Compile the Java sources.
   - Run the unit tests.
   - Generate JaCoCo coverage reports.
   - Produce a WAR file at `target/OceanViewResort_Reservation_System.war`.

4. **Deploy to application server**

   - Copy the WAR from `target/` to your application server’s deployment directory (for example, `webapps/` for Tomcat).
   - Start or restart the server.
   - Access the application in the browser, typically at:

     ```text
     http://localhost:8080/OceanViewResort_Reservation_System
     ```

### Running tests

Run the full test suite with coverage:

```bash
mvn clean test
```

JaCoCo will generate a coverage report under `target/site/jacoco/`. The build is configured to enforce a minimum overall line coverage threshold.

### Project structure (high level)

- `src/main/java/com/oceanview/resort`  
  - `controller/` – Servlet controllers and dashboard endpoints  
  - `service/` & `service/impl/` – Business logic services  
  - `dao/` & `repository/` – Data access and persistence helpers  
  - `model/` & `dto/` – Domain models and data transfer objects  
  - `strategy/` – Discount and billing strategy implementations  
  - `observer/` – Observer pattern for reservation events and statistics  
  - `messaging/` – Kafka-based email notification components  
  - `util/` – Utility helpers (e.g., HTML/XSS utilities)  
- `src/main/webapp/` – JSP views, dashboards, and configuration (`WEB-INF/web.xml`)  
- `src/test/java/` – Unit and integration tests across layers  
- `Docs/` – Test plan, execution guides, and documentation

### Versioning

The project uses **semantic versioning** (`MAJOR.MINOR.PATCH`).  
Tags are created in the form `v1.0.0`, `v1.1.0`, etc., based on the nature of the changes.

### License

This project is licensed under the **MIT License**. See the `LICENSE` file for details.

