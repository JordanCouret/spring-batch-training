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

