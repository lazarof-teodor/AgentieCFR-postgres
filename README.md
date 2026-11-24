# Agentie CFR

Aplicatie Java Swing pentru gestiunea unei agentii CFR (trenuri, statii, rute, curse, bilete, clienti), cu baza de date PostgreSQL accesata prin JDBC.

## Ce ai nevoie

- PostgreSQL 14 sau mai nou (pgAdmin este optional, dar util)
- JDK 21 (sau mai nou)
- Maven (sau lasa extensia Java sa-l gaseasca)
- VS Code cu extensia **Extension Pack for Java** (`vscjava.vscode-java-pack`). Proiectul ti-o recomanda automat.

## Pasi de instalare

1. Creeaza baza de date. Din linia de comanda, din folderul proiectului:

   ```
   psql -U postgres -c "CREATE DATABASE agentie_cfr;"
   psql -U postgres -d agentie_cfr -f sql/schema.sql
   ```

   Sau in pgAdmin: creeaza baza `agentie_cfr`, apoi deschide Query Tool pe ea si ruleaza `sql/schema.sql`. Scriptul sterge si recreeaza tabelele din baza, deci nu-l rula pe date pe care vrei sa le pastrezi.

2. Configureaza conexiunea. Copiaza `db.properties.example` ca `db.properties` in acelasi folder si completeaza parola ta de PostgreSQL (utilizatorul `postgres`). Fisierul `db.properties` nu se pune pe GitHub.

3. Deschide folderul proiectului in VS Code (File > Open Folder). Extensia Java descarca singura dependentele din `pom.xml`, la prima deschidere.

4. Ruleaza aplicatia: deschide `src/main/java/agentie/cfr/AgentieCFR.java` si apasa Run deasupra metodei `main`, sau din meniul Run and Debug (configuratia `Ruleaza AgentieCFR`).

Din terminal, ruleaza aplicatia cu:

```
mvn compile exec:java -Dexec.mainClass=agentie.cfr.AgentieCFR
```

## Autentificare

Contul initial este:

- utilizator: `admin`
- parola: `admin123` (rol `Admin`)

Schimba parola sau creeaza alt cont de administrator si dezactiveaza acesta.

Rolurile din aplicatie sunt `Admin` (gestioneaza trenuri, statii, rute, curse, utilizatori, rapoarte) si `Casier` (vinde si anuleaza bilete, gestioneaza clientii). Numele trebuie sa ramana exact acestea, pentru ca aplicatia le verifica.

## Testarea fara interfata grafica

`src/main/java/agentie/cfr/test/TestJDBC.java` parcurge un flux complet prin DAO-uri: creeaza statii, tren, ruta, cursa, vagon, loc, client si utilizator, emite un bilet, il anuleaza si verifica locul. Il poti rula din VS Code (Run deasupra metodei `main`) si il poti modifica pentru a testa alte functii. Ruleaza-l dupa ce ai rulat `sql/schema.sql`.

Daca vrei sa adaugi o functie noua, scrie-o in DAO-ul corespunzator (`src/main/java/agentie/cfr/db/dao/`), apeleaz-o din `TestJDBC` si verifica rezultatul in consola.

## Structura

- `pom.xml` - dependentele si versiunea de Java
- `src/main/java/agentie/cfr/db/` - conexiunea JDBC (`ConexiuneDB`) si clasele DAO (accesul la baza de date)
- `src/main/java/agentie/cfr/model/` - clasele entitate
- `src/main/java/agentie/cfr/service/` - logica de autentificare si business
- `src/main/java/agentie/cfr/ui/` - interfata Swing (fereastra principala si paneluri)
- `src/main/java/agentie/cfr/util/` - export PDF si utilitare
- `src/main/java/agentie/cfr/test/` - test JDBC fara interfata grafica
- `sql/schema.sql` - schema bazei de date si date de test
