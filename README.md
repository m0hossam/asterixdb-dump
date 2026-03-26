# Prototype for backup/restore utility for AsterixDB [ASTERIXDB-3697]

Run AsterixDB and execute the following queries:

```sql
DROP DATAVERSE Test IF EXISTS;
CREATE DATAVERSE Test;
USE Test;

CREATE TYPE UserType AS {
    id: int,
    name: string
};

CREATE DATASET Users(UserType)
PRIMARY KEY id;

INSERT INTO Users
SELECT VALUE {"id": 1, "name": "Alice"};

INSERT INTO Users
SELECT VALUE {"id": 2, "name": "Bob"};
```

Now run this project using `mvn clean compile && java -cp target/classes:$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout) org.example.Main` and copy the output SQL++ to a temporary text file.

Run `DROP DATAVERSE Test;` on AsterixDB, then paste and run the output you copied earlier, you should get the same database.

**Obviously, this is very primitive and is still a massive work-in-progress.**
