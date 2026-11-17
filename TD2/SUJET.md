# TD2 — Une dépendance de trop

R304 FI — groupes de 3 à 5, les mêmes qu’au TD1. N’utilisez pas d’IA pendant le TD. Les durées sont indicatives : ce qui n’est pas fini se termine au TD suivant.

**Comment lire ce sujet.** Le « Contexte » se lit une fois. Chaque étape donne ensuite une liste numérotée d’**Actions**, le verbe en gras. « **Sur la feuille :** » dit ce que vous écrivez sur la feuille de groupe, celle du TD1. « **Résultat attendu :** » dit ce que vous devez voir.

## Contexte, à lire une fois

Au TD1, vous avez rendu le moteur imbattable en changeant une seule ligne de `Main`. Cela a suffi parce que la classe qui choisit le coup, `ChooseBestMoveService`, ne nomme aucun évaluateur. Elle ne dépend que de l’interface `EndgameOracle`, que tous les évaluateurs implémentent. Elle reçoit un évaluateur déjà créé, en paramètre de son constructeur.

Aujourd’hui, le code fourni est celui du même moteur, mais il est **mal construit** : dans son constructeur, `ChooseBestMoveService` crée lui-même son évaluateur, avec `new NaiveOracle()`. Le programme joue exactement comme au début du TD1. Pourtant, il ne respecte pas la règle des couches : `ChooseBestMoveService`, qui appartient à la couche application, nomme une classe de la couche des adaptateurs. Vous allez voir les inconvénients de ce défaut. Vous le corrigerez ensuite, et vous prouverez qu’il a disparu.

Désormais, chaque séance se déroule en cinq étapes : la découverte du code fourni et de son défaut, le diagnostic, le principe de la solution, le refactoring (la modification du code sans changement de comportement), la vérification.

**Rappel.** Une flèche de **dépendance** va de la classe dont le code contient le nom d’un type vers ce type. Ce nom peut être le type d’un champ, d’un paramètre ou d’un résultat ; il peut aussi suivre `new` ou `implements`. Un **appel** à l’exécution n’est pas une dépendance : `ChooseBestMoveService` appelait `NaiveOracle` au TD1 sans en dépendre.

## Étape 1 — Le code fourni, et son défaut (15 min)

Tous les chemins sont relatifs à `TD2/src/main/java/org/chesskrk`.

**Actions**

1. **Mettez à jour** votre dépôt local avec **Git > Update Project** (`Ctrl+T`), puis **ouvrez** `TD2/pom.xml` comme au TD1 : **File > Open**, puis **Open as Project** et **This Window**.
2. **Lancez** `infrastructure/Main.java` et **jouez** deux coups. **Résultat attendu :** le moteur joue comme au début du TD1 : il donne des échecs inutiles.
3. **Ouvrez** `application/usecase/ChooseBestMoveService.java` et **lisez** son constructeur et ses imports. **Sur la feuille :** la ligne qui crée l’évaluateur ; le nom de package qui apparaît dans les imports et qui n’y était pas au TD1.
4. **Ouvrez** `infrastructure/Main.java` et **lisez** la méthode `buildUseCase`. **Sur la feuille :** ce que `Main` ne décide plus.
5. **Lancez** tous les tests : faites un clic droit sur le dossier `src/test/java`, puis choisissez **Run 'All Tests'**. **Résultat attendu :** tous les tests réussissent, sauf deux règles de `CleanArchitectureTest`. **Sur la feuille :** pour chacune de ces deux règles, la ligne de code que le message d’erreur désigne, et la règle que ce message énonce.

## Étape 2 — Le diagnostic (20 min)

Le changement demandé est le même qu’au TD1 : évaluer les positions avec `RetrogradeTablebase` au lieu de `NaiveOracle`.

**Actions**

1. **Reprenez** le graphe de dépendances du TD1, **ajoutez** en rouge la nouvelle flèche et **barrez** les flèches qui partaient de `Main` et qui n’existent plus. **Sur la feuille :** la déclaration Java qui justifie la nouvelle flèche.
2. **Estimez** l’impact du changement avec la méthode du TD1 : cherchez les flèches qui arrivent sur ce qui change. **Sur la feuille :** les fichiers à modifier ; dites si une seule ligne de `Main` suffit encore.
3. **Répondez** à la question suivante. On veut écrire un test qui vérifie que `ChooseBestMoveService` choisit le mat le plus rapide. Ce test ne doit ni calculer la table de finales de `RetrogradeTablebase`, ni dépendre des trois cas que `NaiveOracle` reconnaît. Est-ce possible avec le constructeur actuel de `ChooseBestMoveService` ? **Sur la feuille :** votre réponse et sa justification.
4. **Décrivez** le défaut en une phrase : quelle classe dépend d’une classe dont elle ne devrait pas dépendre ? Employez les expressions « responsabilité de choisir un coup » et « responsabilité d’évaluer une position ».

**Faites le point avec l’enseignant** avant de continuer.

## Étape 3 — Le principe de la solution (15 min)

`ChooseBestMoveService` ne doit dépendre que de l’**interface** `EndgameOracle`, et jamais d’une classe qui implémente cette interface. C’est `Main` qui crée l’évaluateur, puis qui le **passe en paramètre au constructeur** de `ChooseBestMoveService` : c’est l’**injection par le constructeur**. Une classe qui crée un objet avec `new` dépend de la classe de cet objet. Une classe qui reçoit un objet par une interface ne dépend que de cette interface.

**Actions**

1. **Dessinez**, à côté du graphe actuel, le graphe à obtenir. Dans ce graphe, `ChooseBestMoveService` ne pointe plus que vers `EndgameOracle`. Quelle classe pointe alors vers `NaiveOracle` ?
2. **Listez** les fichiers qu’il faudra modifier pour obtenir ce graphe, et indiquez pour chacun ce qu’il faudra y changer. **Sur la feuille :** cette liste, avec votre estimation du nombre de lignes à modifier dans chaque fichier.
3. **Vérifiez**, sur le graphe à obtenir, que le changement demandé (remplacer `NaiveOracle` par `RetrogradeTablebase`) ne modifie de nouveau qu’une ligne de `Main`.

## Étape 4 — Le refactoring (25 min)

Un refactoring modifie l’organisation du code sans modifier ce que fait le programme : le moteur doit jouer de la même façon avant et après.

**Actions**

1. **Remplacez**, dans `ChooseBestMoveService.java`, le constructeur par celui-ci, qui **reçoit** l’évaluateur en paramètre :

   ```java
   public ChooseBestMoveService(EndgameOracle oracle) {
       this.oracle = oracle;
   }
   ```

2. **Supprimez** l’import de `NaiveOracle`, devenu inutile. **Résultat attendu :** `ChooseBestMoveService.java` ne contient plus le mot `adapter`.
3. **Corrigez** la méthode `Main.buildUseCase`, qui ne compile plus : c’est maintenant à elle de créer l’évaluateur et de le passer au constructeur de `ChooseBestMoveService`. **Importez** `NaiveOracle` avec `Alt+Entrée`, comme au TD1.
4. **Relancez** `Main` et **rejouez** deux coups. **Résultat attendu :** le moteur joue comme avant.
5. **Relancez** tous les tests. **Résultat attendu :** tous les tests réussissent, y compris `CleanArchitectureTest`.
6. **Validez** vos modifications (commit) avec le message « TD2 : injection de l’évaluateur par le constructeur », puis **poussez**-les (push). Pour l’instant, chacun pousse sur son dépôt personnel, celui des TP. Votre groupe peut aussi choisir le dépôt de l’un d’entre vous comme dépôt commun. Chacun reste alors libre de pousser ses propres essais sur son dépôt personnel.

**Sur la feuille :** les fichiers modifiés et le nombre de lignes modifiées dans chacun ; comparez-les à votre liste de l’étape 3.

## Étape 5 — La vérification (20 min)

Grâce au refactoring, le test de l’étape 2 devient possible : on peut tester `ChooseBestMoveService` avec un **évaluateur factice**. Un évaluateur factice est une petite classe écrite dans le fichier de test. Elle implémente `EndgameOracle` et renvoie les évaluations que l’auteur du test a choisies. Le test n’utilise donc ni `RetrogradeTablebase` ni `NaiveOracle`.

**Actions**

1. **Créez** le package `org.chesskrk.application` sous `src/test/java` (clic droit, puis **New > Package**). **Créez**-y la classe de test `ChooseBestMoveServiceTest` et recopiez-y l’évaluateur factice ci-dessous. **Importez** chaque nom signalé en rouge avec `Alt+Entrée`. Cet évaluateur distingue deux cas : une position de mat est une défaite immédiate pour le camp au trait ; toute autre position est nulle.

   ```java
   static final class EvaluateurFactice implements EndgameOracle {
       @Override
       public Evaluation evaluate(Position position) {
           if (Rules.status(position) == GameStatus.CHECKMATE) {
               return Evaluation.loss(0);
           }
           return Evaluation.DRAW;
       }
   }
   ```

2. **Écrivez** un premier test à partir de la position suivante : roi blanc en b6, tour en h7, roi noir en a8, trait aux blancs. Créez un `ChooseBestMoveService` avec `new EvaluateurFactice()` et vérifiez qu’il choisit le coup h7-h8, qui donne le mat vu au TD1. Une position s’écrit `Position.of(Square.parse("b6"), Square.parse("h7"), Square.parse("a8"), Color.WHITE)`. Un coup s’écrit `new Move(Square.parse("h7"), Square.parse("h8"))`. La méthode `chooseMove` renvoie un `Optional<Move>`.
3. **Lancez** le test. **Résultat attendu :** le test réussit (il s’affiche en vert), et il ne nomme aucune classe du dossier `adapter`.
4. **Écrivez** un second test de votre choix. Par exemple, écrivez un évaluateur factice pour lequel toute position où la tour est encore sur l’échiquier est gagnée pour le camp au trait. Partez d’une position où les noirs sont au trait et où le roi noir est à côté de la tour. Vérifiez que `ChooseBestMoveService` choisit de prendre la tour.
5. **Validez** (commit) et **poussez** (push), comme à l’étape 4.

**Sur la feuille :** ce que votre premier test prouve, et ce qu’il ne prouve pas ; pourquoi il était impossible de l’écrire avant le refactoring.

## Bilan (10 min)

**Sur la feuille**, écrivez une ligne pour chacun de ces quatre points :

1. le défaut : quelle classe dépendait de quelle autre classe, et pourquoi cette dépendance était nuisible ;
2. la solution ; employez les mots « interface `EndgameOracle` » et « injection par le constructeur » ;
3. ce que le test avec l’évaluateur factice a permis de vérifier sans utiliser aucune classe du dossier `adapter` ;
4. la ligne de `Main` qu’il faudrait écrire maintenant pour remplacer `NaiveOracle` par `RetrogradeTablebase`.

**Conservez** la feuille : au TD3, le défaut sera dans le code des règles du jeu.
