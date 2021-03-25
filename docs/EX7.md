**Allez plus loin et comprendre le retry**
Dans l'exercice précedent, on a vu que 50 lignes étaient persisté.

Batch a executé 5 fois :
    * Ouverture d'une transaction
    * Lire 10 items
    * Process chaque item un à un
    * Ecrire les 10 items
    * Commit la transaction

A la 6ème fois, il obtient une exception lors de l'écriture des items (```Value too long for column "FULLNAME VARCHAR(255)"```).
Batch effectue donc :
    * Rollback de la transaction
    * Stop ici et ne fait rien pour les 9 items sans exceptions

**Ajouter sur votre stepBuidler un ```.skip(DataIntegrityViolationException.class)``` pour ignorer l'exception et accepter qu'une ligne est skippé si l'adresse fait plus de 255 caractères**

Lancer maintenant le Ex7Tests pour valider le fait que nous avons bien nos 99 lignes en BDD !

### Que s'est-il passé ?
```
2021-03-25 16:45:30.119  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=50, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.120  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=51, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.121  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=52, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.121  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=53, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.122  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=54, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.123  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=55, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.123  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=56, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.124  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=57, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.125  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=58, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.125  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=59, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.262  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=50, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.269  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=51, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.276  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=52, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.283  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=53, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.287  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=54, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.313 ERROR 54819 --- [           main] fr.rha.jco.batch.listener.Ex3Listener    : WRITE : PreparedStatementCallback; SQL [INSERT INTO address (id, fullName, student_id) VALUES (?, ?,  ?)]; Value too long for column "FULLNAME VARCHAR(255)": "'150 Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam vulputate, sapien sit amet vestibulum scelerisque, sem tortor ... (344)"; SQL statement:
INSERT INTO address (id, fullName, student_id) VALUES (?, ?,  ?) [22001-200]; nested exception is org.h2.jdbc.JdbcSQLDataException: Value too long for column "FULLNAME VARCHAR(255)": "'150 Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nam vulputate, sapien sit amet vestibulum scelerisque, sem tortor ... (344)"; SQL statement:
INSERT INTO address (id, fullName, student_id) VALUES (?, ?,  ?) [22001-200] = StudentBddWithAddress(super=StudentBdd(id=53, firstName=Prenom, lastName=Nom), addressId=64)
2021-03-25 16:45:30.318  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=55, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.324  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=56, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.330  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=57, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.341  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=58, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.346  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=59, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.359  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=60, firstName=Prenom, lastName=Nom) is valid
2021-03-25 16:45:30.360  INFO 54819 --- [           main] fr.rha.jco.batch.processor.Ex6Processor  : StudentCsv(id=61, firstName=Prenom, lastName=Nom) is valid
```

Lorsque Batch arrive sur le chunk avec une erreur, il va adopter un mécanisme de défense pour identifier la ligne qui pose soucis.
Batch effectue dans l'ordre :
* Ouvre une transaction BDD
* Lit 10 items
* Process 1 à 1 les items
* Ecriture => EXCPETION à SKIPPER
* Rollback de la transaction
* Pour chaque item du chunk en erreur :
    * Ouvre une transacion BDD
    * Lit 1 item
    * Process l'item
    * Ecriture d'un item 
        * Si exception
            * Rollback l'item
            * Skip l'item
        * Sinon 
            * Commit d'un item
* Reprend en mode 10 items par 10 items


Génial non ? 

Maintenant, essayons d'optimiser un peu tout ça.
**Au niveau processor, ajouter un if pour retourner null si l'addresse fait plus de 255 caractères**

Résultat : On n'obtient plus ce mécanisme de Batch qui retry 1 à 1 les items car il n'y a plus d'exception provenant d'un seul item et non du chunk

Bien réfléchir à quand utiliser le skip de SpringBatch ou non :) 

``````