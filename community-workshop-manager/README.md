# Community Workshop Manager

A Spring Boot web application for a community training centre. Staff can record
**instructors** and the **workshops** they teach, with Create, Read and Update
screens for both, built with Spring MVC and JSP. A schedule overview page shows
the result of a JPQL inner join between the two tables.

## Tested versions

| Component | Version |
|-----------|---------|
| Java (OpenJDK) | 21.0.12 |
| Maven (via wrapper) | 3.9.11 (wrapper script 3.3.4) |
| Spring Boot | 4.1.2 |
| Embedded Tomcat / Jasper (JSP) | 11.0.27 (Servlet 6.1) |
| JSTL | Jakarta API 3.0.2, GlassFish implementation 3.0.1 |
| Hibernate ORM | 7.4.13.Final |
| Hibernate Validator | 9.1.5.Final |
| H2 Database | 2.4.240 |
| JUnit Jupiter / Mockito | 6.0.3 / 5.23.0 |

Only a Java 21 JDK is required. The Maven Wrapper downloads Maven on first use.

## Commands

Run from the `community-workshop-manager` folder. On Windows use `mvnw.cmd` instead of `./mvnw`.

```bash
# Run the full test suite
./mvnw clean test

# Build the executable WAR (also runs the tests)
./mvnw clean package

# Start the packaged application
java -jar target/community-workshop-manager.war
```

Then open <http://localhost:8080/>. The root URL redirects to the schedule overview.

To use another port: `java -jar target/community-workshop-manager.war --server.port=9090`.

## Database

* H2 in file mode. Data is stored in `./data/community-workshops.mv.db`, relative
  to the folder the application is started from, so changes survive restarts.
* Tables are created by Hibernate from the entity classes (`spring.jpa.hibernate.ddl-auto=update`).
* A different location can be used with `--app.db.path=/some/folder/workshops`.
* **Reset:** stop the application, delete the `data` folder, and start it again.
  A fresh database is reseeded automatically.
* Tests use a separate in-memory database (`src/test/resources/application.properties`)
  and never touch `./data`.

## Sample data

On start-up `DataSeeder` checks whether the instructor and workshop tables are
empty. Only then does it insert **10 instructors and 10 workshops**. If any rows
already exist it does nothing, so restarting never creates duplicates.
Amara Okafor and Daniel Reyes teach two workshops each, and Lucas Moreau and
Sofia Petrova have none (so they are absent from the joined overview). Seeding
can be turned off with `--app.seed-data=false`.

## Main routes

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/overview` | Joined instructor and workshop schedule (`/` redirects here) |
| GET | `/instructors` | List instructors |
| GET | `/instructors/new` | Create form |
| POST | `/instructors` | Save a new instructor |
| GET | `/instructors/{id}/edit` | Edit form |
| POST | `/instructors/{id}` | Save changes |
| GET | `/workshops` | List workshops |
| GET | `/workshops/new` | Create form (instructor choices loaded from the database) |
| POST | `/workshops` | Save a new workshop |
| GET | `/workshops/{id}/edit` | Edit form |
| POST | `/workshops/{id}` | Save changes |

Successful saves redirect back to the list (Post/Redirect/Get) with a confirmation message.

## Project structure

```
src/main/java/ca/communitycentre/workshops/
  entity/        Instructor, Workshop (JPA entities, one-to-many / many-to-one)
  repository/    InstructorRepository (includes the @Query inner join), WorkshopRepository
  projection/    WorkshopOverview record returned by the join
  service/       InstructorService, WorkshopService, TextNormalizer
  controller/    InstructorController, WorkshopController, OverviewController, GlobalExceptionHandler
  form/          InstructorForm, WorkshopForm (request binding and Bean Validation)
  exception/     RecordNotFoundException, DuplicateValueException, InvalidInstructorException,
                 IntegrityViolationTranslator
  config/        DataSeeder
src/main/webapp/
  WEB-INF/jsp/   JSP views (instructors/, workshops/, overview.jsp, error pages, common/ includes)
  css/site.css   Local stylesheet
src/test/java/   Repository, service, controller and full-stack tests
docs/screenshots Screenshots taken from the running application
submission/      Report PDF
```

## Inner join

`InstructorRepository.findWorkshopOverview()` uses this JPQL query and returns a
typed `WorkshopOverview` record per row:

```java
SELECT new ca.communitycentre.workshops.projection.WorkshopOverview(
    i.id, i.fullName, i.email, i.speciality,
    w.id, w.code, w.title, w.scheduledDate, w.durationMinutes, w.capacity)
FROM Instructor i
INNER JOIN i.workshops w
ORDER BY w.scheduledDate ASC, w.code ASC
```

The rows travel through `WorkshopService.getOverview()` and `OverviewController`
to `overview.jsp`. Instructors without workshops have nothing to join to, so they
do not appear.

## Validation and constraints

* Form models use Bean Validation for required fields, email format, lengths,
  the code pattern, and ranges (duration 15 to 480 minutes, capacity 1 to 200).
* Emails are stored in lower case and workshop codes in upper case, so uniqueness
  does not depend on letter case.
* The database also enforces `NOT NULL` columns, unique constraints on email and
  code, check constraints on duration and capacity, and a non-null foreign key from
  `workshops.instructor_id` to `instructors.id`.
* A `DataIntegrityViolationException` raised on flush or commit is caught in the
  controller (outside the failed transaction) and shown as a form message.

## Limitations

* Deleting records, authentication and paging are outside the scope of this project.
* There is no optimistic locking, so if two people edit the same record at once the
  later save wins.
* Dates are shown in ISO format (YYYY-MM-DD).
