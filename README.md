# Formation

**Cours:**
* Step - Job
* Reader - Processor - Writer

Projet :
* Input
* Output
* Reader
* Processor
* Writer


---
## Ex 1
**Complete Ex1Configuration :**
* Read and parse the file ex1.csv
* Process and validate items from the CSV with the SpringValidator
* Write the model in the database

*Tests : Ex1Tests*

---
Ce qu'on a appris: 
* Differents types d'injections des StepScope (qualifier en inner class, classe externe en final ou this en inner class avec null pour les params) => Resté toujours sur la même logique dans votre projet !!
* L'interet du StepScope pour batch 
    * Permet de créer au runtime le bean avec des paramètres provenant du jobParameters ...
Ce qu'on voit sur le prochain:
* Validation des items sur les champs provenant du reader

---
## Ex 2
**Completé votre Ex1Configuration:**
* StudentCsv : Ajouter les règles de gestion suivante
    * id : Positive required
    * firstName : Without number required
    * lastName : Without number required
    * address : Nullable
    * birth : Respecte le format YYYY-MM-DD
* Si non valide, faite en sorte que le processor renvoi null au lieu d'un StudentBdd (permet de ne pas traiter l'objet)

---
Ce qu'on a appris: 
* Utilisation des annotations de valiations pour les objets provenant du reader
* Possibiltié de skip dans le processior
Ce qu'on voit sur le prochain:
  * Le mécanisme de skip Spring-Batch
  * Les listeners pour logger ces objets skippé 

---
## Ex 3 
**A partir de l'exercice 2 & votre Ex1Configuration:**
* Supprimer le skip manuel dans le processor (return null si exception, laissé l'exception throw)
* Ajouter le mécanisme de skip au niveau du job pour ignorer exceptions suivante : 
  * ValidationException : Exception throw par le validator de spring
  * FlatFileParseException : Exception si le reader n'arrive pas à lire une ligne du CSV
  * DuplicateKeyException : Exception si problème BDD
* Ajouter un listener au niveau du job pour loggé simplement les objects skippés
---
Ce qu'on a appris:
* S-Batch propose un system de skip d'exception 
* S-Batch : si le job n'est pas de type faultTolerant, le job s'arrete a la moindre exception
* Mécanisme de listener pour a peu près tous les use case possible: 
    * AfterRead, BeforeRead, OnReadError
    * AfterProcess, BeforeProcess, OnProcessError
    * AfterWrite, BeforeWrite, OnWriteError
    * AfterStep, BeforeStep
    * AfterJob, BeforeJob
    * ...
=> De même, les listeners peuvent être :
      * Classe qui étent le bon listener et ajouté sur le job
      * Un processor qui porte l'annotation @BeforeStep par exemple. Tous les objets gérés par spring batch (reader, process, writer) auront ces annotations qui seront déclenchés lors du cycle de vie du Job
    
---
## Ex 4
**Completer Ex4Configuration:**
* Reader : Lecture depuis une BDD des données d'un étudiant (même champ que StudentCsv) depuis oneDateSource (ou oneJdbcTemplate)
* Processor : Valide & Transforme en model
* Writer : Ecrire à la fois dans la twoDateSource (ou twoJdbcTemplate) ET dans un fichier l'acquittement sous format CSV
---
Ce qu'on a appris:
* S-Batch : L'acquittement de fichier doit se faire via un writter et non des listeners (pour gérer les processus de rollback)
* S-Batch : Propose un mécanisme de lecture de bdd grâce a du paging ou des cursors
* S-BAtch : stocke ses données en base de données pour son fonctionnement (état d'un job, d'une step ou trigger). DAns un cas de production, il est recommandé d'avoir ces données dans une BDD à part afin de gérer le temps des transactions au mieux, le nombre max de connection (~ le nombre de job pouvant être executé en parallèle * le nombre de thread par job si utilisation d'un partitionner ou autre...) 
**TODO : VOir fetchSize pour le cursor avec @Rha**
Ce qu'on va voir :
* Permettre le retry d'un job en erreur. 3 façons de faire :
    * Automatic : Retry automatique d'un batch via les API spring-batch à partir d'un jobId

---
## Ex 5
**Retry automatic via spring batch API**
**Compléter RetryService => Ex5RetryServiceTests**:
* Compléter la méthode principale de service en injectant les beans spring batch permettant le retry d'un job

Avantage : 
* Permet de passer par la mécanique spring batch pour relancer un job

Désavantage : 
* Possibilité de rejouer une ligne déjà sauvé au par avant (si un chunk entier est passé), donc prévoir des mécanismes de défense contre ca (skip des duplicates, check au niveau du processor ...)
---
## Ex 6 
**Comprendre les transactions BDD sur SpringBatch**
* Reader : Lecture du fichier csv ex6 (reprendre celui de l'ex 1 pour gagner du temps)
* Processor : Dupliquer et completer le processor de l'ex 1 pour ajouter l'assignation de l'identifiant de l'addresse (SELECT nextval('address_seq'))
* Ecrire un writter permettant d'écrire à la fois 
    * Student dans la BDD oneDataSource (ou avec oneJdbcTemplate)
    * Address dans la BDD twoDataSource (ou avec twoDataSource)
    
L'Ex6Tests vous permet de lancer l'import dans fichier de 100 lignes qui sera traité 10 lignes par 10 lignes
Nous allons simuler une exception fatale à la ligne 53 avec une addresse plus longue que ce que la BDD authorize.
Si vous ne gerez pas vos transactions BDD, le test ne simplement pas.

### Comment fonctionne les transactions BDD ?
Attention, on reste dans une vision Chunk pour cette explication !

Voici le schéma qui résume bien le fonctionnement de spring batch lors de step orienté chunk :

_Si vous êtes interessé par plus d'informations, nous vous conseillons de lire cet article qui est très complet sur le sujet : [Transactions in spring batch](https://blog.codecentric.de/en/2012/03/transactions-in-spring-batch-part-1-the-basics/)_

### Finir l'exercice
Maintenant que vous avez la connaissance de transaction:
* Ajouter sur votre StepBuilder un transactionManager (créé le transactionManager qui permet de gérér une "chaîne" de dataSourceTransactionManager)

---
## Ex 7 
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
