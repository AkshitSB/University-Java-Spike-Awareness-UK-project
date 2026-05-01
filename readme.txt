(====SPIKE AWARENESS SYSTEM =====)
(===SID:2506288 // Team: Kafka===)
OVERVIEW

This is a Java console-based application designed to support reporting, reviewing, and managing drink spiking incidents. It includes role-based access control and uses an SQLite database for data storage.

_____________________________________________________________________________________________________________
DEFAULT SUPER ADMIN LOGIN (TESTING ONLY)

Username: HeadAdmin
Password: Admin@123

(These credentials are for development/testing only.)

_____________________________________________________________________________________________________________
USER ROLES

SUPER ADMIN

Can create admin accounts (Reviewer, Moderator, etc.)
Full system access
Manages resources and incidents

REVIEWER

Views pending incident reports
Approves or rejects reports

MODERATOR

Manages educational resources
Can add, edit, and delete resource steps

_____________________________________________________________________________________________________________
MAIN FEATURES

INCIDENT REPORTING

Users can report incidents anonymously or with consent
Stores city, description, date, and consent status
Reports are marked as "pending" until reviewed

INCIDENT REVIEW

Reviewers can view all pending reports
Approve or reject reports
Edit city or description before approval

PUBLIC STORIES

Shows only approved incidents with user consent
Displays most recent reports first

CITY SAFETY REPORT

Calculates risk level per city based on incidents:
LOW / MEDIUM / HIGH

RESOURCE MANAGEMENT

Two guide types:
-Response Guide
-Prevention Guide
Moderators can add, edit, and delete steps

_____________________________________________________________________________________________________________
DATABASE

Database file: spikeawareness.db

Tables:

admins
incident_reports
resources

The database is automatically created if it does not exist.

_____________________________________________________________________________________________________________
HOW TO RUN
Compile all Java files:
javac *.java
Run the program:
java Main

_____________________________________________________________________________________________________________
SOFTWARES USED
Java
JDBC
SQLite

_____________________________________________________________________________________________________________
SECURITY NOTES
Passwords are stored using Java hashCode (basic hashing)
Role-based access control is implemented
Only Super Admin can create new admin accounts

_____________________________________________________________________________________________________________
REFERENCES

Schildt, H. (2018) Java: The Complete Reference. 11th edn.

Oracle (2023) JDBC Basics.
https://docs.oracle.com/javase/tutorial/jdbc/

SQLite (2024) SQLite Documentation.
https://www.sqlite.org/docs.html

Spike Aware UK (2023)
https://www.spikeawareuk.org

W3Schools (2024) SQL Tutorial.
https://www.w3schools.com/sql/
