## Ex 5
**Retry automatic via spring batch API**
**Compléter RetryService => Ex5RetryServiceTests**:
* Compléter la méthode principale de service en injectant les beans spring batch permettant le retry d'un job

Avantage : 
* Permet de passer par la mécanique spring batch pour relancer un job

Désavantage : 
* Possibilité de rejouer une ligne déjà sauvé au par avant (si un chunk entier est passé), donc prévoir des mécanismes de défense contre ca (skip des duplicates, check au niveau du processor ...)