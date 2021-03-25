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
    