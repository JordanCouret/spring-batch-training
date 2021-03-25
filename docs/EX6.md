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
