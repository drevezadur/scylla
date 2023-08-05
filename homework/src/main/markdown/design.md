# Conception de l'application

## Organisation des sources

Les sources sont organisées en différents packages.
![Package Diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/gofannon-io/scylla/main/src/main/puml/packages.puml)

* Le package **domain** contient l'API métier et donc celle du package **domain.internal**. Il s'agit de l'interface métier.
* Le package **domain.internal** contient l'implémentation métier. 
* Le package **service** contient l'API de service qui interagit avec les packages **domain** et **domain.internal**
* Le package **service.internal** contient l'implémentation de l'API de service. 


## Les packages
### Le package service
Il contient les interfaces pour interagir avec l'utilisateur 
* L'interface Scenario décrit les interactions avec un scénario
  * `execute(Game)` déroule un scénario sur un jeu donné.
* L'interface ScenarioLoader permet de charger un fichier et de générer le **Scenario** associé.

### Le package service.internal
Ce package contient l'implémentation service de l'application.


### Le package domain
Il contient les interfaces métier
* L'interface **Game** décrit une partie de bataille navale
* L'interface **GameFactory** permet la création d'une partie de bataille navale
* L'interface **GameProvider** est une aide pour gérer une unique instance de **Game**
* L'interface **PlayerBoard** est l'aire de jeu du joueur, à savoir l'état de sa flotte et les actions qu'il peut mener (déployer et tirer).
* L'interface **Ship** représente un navire, avec des accès pour consulter son état uniquement.

### Le package domain.internal
Ce package contient l'implémentation métier de l'application. 


## Séquence de démarrage
La séquence de démarrage est déroulée depuis la méthode `main()`.
Elle s'effectue en deux temps : 
* le chargement du scénario
* l'exécution du scénario


### Le chargement du scénario
Le chargement du scénario s'effectue via la classe `ScenarioLoader`.
L'appel à `ScenarioLoader.loadFromPath(Path)` va charger le contenu du fichier et convertir chaque ligne en instructions.
Les instructions sont du texte (ie celui d'une ligne du fichier) mais le contenu n'est pas validé.

À la fin, l'instance de `Scenario` contient les instructions et une instance de `CommandExecutor` qui permet l'exécution 
d'une instruction.


### L'exécution du scénario
L'exécution du scénario s'effectue en créant une nouvelle instance de `Game`, en convertissant au fûr et à mesure les 
instructions en `Command` et en les exécutant sur l'instance de `Game`.

Lorsqu'une instruction ne peut être traduite en commande, une exception est levée.
Il en est de même, quand une commande ne peut être exécutée.

![Boot Sequence Diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/gofannon-io/scylla/main/src/main/puml/boot-sequence.puml)


## Déploiement

![Deployment Sequence Diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/gofannon-io/scylla/main/src/main/puml/deploy-sequence.puml)


## Tir
La séquence de tir consiste à exécuter une commande de tir à tour de rôle pour chaque joueur.

Une commande de tir est constituée de deux paramètres : 
* le joueur cible 
* la localisation du tir

Le résultat d'une commande de tir est l'une des valeurs suivantes :
* manqué : le tir est tombé dans l'eau. 
Ce tir n'a aucune conséquence.
* déjà effectué : un précédent tir a déjà été à cette localisation. 
Ce tir n'a aucune conséquence.
* touché : le tir a touché un navire.
Si toutes les autres localisations du navire ont subi un tir, alors le navire est coulé.
Quand tous les navires d'un joueur sont coulés, le joueur cible a perdu et le joueur auteur du tir est vainqueur.

### Aperçu
Pour effectuer un tir, il faut obtenir le `PlayerBoard` du tireur auprès du gestionnaire de jeu.
Ensuite, il faut appeler la méthode `fireAt()` en fournissant en paramètre la localisation du tir.
Puis c'est le tour de l'autre joueur de tirer. 
Cela se déroule ainsi de suite jusqu'à la fin de la partie. 

![Fire Overview Sequence Diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/gofannon-io/scylla/main/src/main/puml/fire-sequence.puml)

### Détail
Lorsque le joueur appelle `PlayerBoard.fireAt`, cela est relayé au gestionnaire de jeu `GameImpl`.
`GameImpl` soumet la résolution du tir au `PlayerBoard` de l'adversaire du tireur.
Celui-ci résout le tir et met à jour ses status (Navire, flotte).
Le résultat est récupéré par le gestionnaire de jeu qui met à jour eu besoin le status du jeu, puis il relaye le résultat
au tireur.

![Detailed Fire Sequence Diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/gofannon-io/scylla/main/src/main/puml/full-shot-sequence.puml)



## Fin de partie
La fin de partie est déclenchée quand l'ensemble des navires d'un joueur (Sa flotte) est coulé.

La séquence est la même que celle d'un tir. 
La différence se fait au niveau du gestionnaire de jeu `GameImpl` qui, en exécutant la méthode de vérification et mise 
à jour des status, `updateInternalStatusAfterAction()`.

![End of Game Sequence Diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/gofannon-io/scylla/main/src/main/puml/last-shot-sequence.puml)
