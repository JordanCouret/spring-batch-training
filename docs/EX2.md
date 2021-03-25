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

