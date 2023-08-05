# Manuel utilisateur de Scylla homework

## Introduction
Scylla Homework est une implémentation façon "devoir d'étudiant" montrant une 
implémentation minimale du fameux jeu de bataille navale.

La page wiki sur la bataille navale [S1] décrit les règles.

Le périmètre de l'application est l'exécution d'un scénario décrit dans un fichier qui est passé en paramètre.



## Précisions sur les règles
Ce chapitre décrit juste quelques précisions sur les règles standard du jeu.

Les navires à déployer sont :

| Nom               | Identifiant    | Longueur (Cases) |
|-------------------|----------------|------------------|
| Porte-avions      | CARRIER        | 5                |
| Cuirassé          | BATTLESHIP     | 4                |
| Croiseur          | CRUISER        | 3                |
| Sous-marin        | SUBMARINE      | 3                |
| Contre-torpilleur | DESTROYER      | 2                |




## Exécution du programme

```shell
$ ./homework ./battle-scenario.txt
```


## Format des fichiers scénarios

Un fichier scénario est un fichier texte encodé en UTF-8.
Chaque ligne constitue une instruction. 
Il existe quatre types de lignes :
* les lignes dites vides qui sont composées uniquement d'espace et de tabulation.
  * Ces lignes sont ignorées.
* les lignes de commentaire qui commencent par un dièse ('#') 
    * Ces lignes sont ignorées.
* les lignes contenant un ordre de déploiement. Par exemple : `B deploy BATTLESHIP 1,0 COLUMN`.
  *  Ces lignes sont converties en instructions de déploiement.
* les lignes contenant un ordre de tir. Par exemple : `A fire 2,0`.
    *  Ces lignes sont converties en instructions de tir.


## Sources
* [S1] [Wikipédia Bataille Navale](https://fr.wikipedia.org/wiki/Bataille_navale_(jeu))
