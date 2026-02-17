# Todo's

---
## Funktionale Features
- ~~-Unit Tests reparieren-~~
- ~~-UserController-~~ 
  - ~~-Update durch User-~~
  - update password
- AdminUserController
  - ~~-Update durch Admin-~~
  - delete
  - update password
  - update Role
- Show Password Button
- RegisterController Tests
- Role muss durch Admin gesetzt werden
  - vorher keine Zugriff auf Data
  - kann sein eigenes Profil bearbeiten 
    - auch löschen?
    - Save button nach edit
    - evtl css highlighting editable / readonly
    rollenzuweisung durch admin
- defining Authorities ==> fragen an Wladi
  - ROLE_ADMIN ==> CRUD
  - ROLE USER ==> CRU?
  - ~~-@PreAuthorize funktioniert nicht-~~ 
    - ~~-@EnableMethodSecurity fehlte-~~
- CRUD
  - ~~-read ==> update (für alle) und delete (für admin) buttons-~~
  - update
    - change password view
      - old pwd
      - new pwd
      - confirm pwd
  - ~~-delete-~~
  - ~~-create-~~ done
- Change username to email
- change User Spring security User
- change /req/login to /login
- ~~-/h2-console geht nicht-~~
- 2FA
- Ablauf
  - Wann 2FA?
    - direkt bei Registrierung oder beim ersten Login?
- CSS aufräumen 
  - Variablen definieren
- CSRF aktivieren

---
## Fragen an Wladi
### Usermanagement
- Wie soll das Usermanagement erreichbar sein
  - Direkt von der Startseite vor Login? ==> Ja vor Login
  - Er bekommt keine Rolle zugewiwsen, muss also erst freigeschaltet werden
- Wer soll sich anmelden können?
  - Darf sich jeder anmelden oder werden die Benutzer vom Admin / Superuser hinzgefügt?
    - Es darf sich jeder anmelden
- Wer soll Änderungen an Usern vornehmen können
  - Darf jeder Änderungen an seinem Datensatz / allen Datensätzen vornehmen?
    - Jeder darf seinen Datensatz editieren
  - Muss bei Änderungen das Passort angezeigt werden
  - doppelt angegeben werden? input / repeat
- Welche Rollen gibt es?
  - USER / ADMIN / ==> CUSTOMER?
  - oder muss es feiner aufgeteilt sein?

### 


---
## Unit Tests
- UserController 
- AdminUserController
- RegistrationController Tests
- PasswordMatches Tests reparieren!
- Switch to JUnit6 ==> Apply OpenRewrite (https://docs.openrewrite.org/recipes/java/testing/junit5/junit5bestpractices)
- Test Repositories
- Test Funktionalität
  - UserService
- Test HTML Sites?