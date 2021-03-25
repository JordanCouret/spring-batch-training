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

