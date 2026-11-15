# TD2 — Une classe qui connaît ce qu’elle ne devrait pas

R304 FI — groupes de 3 à 5, les mêmes qu’au TD1. Pas d’IA pendant le TD. Les durées sont indicatives : ce qui n’est pas fini se termine au TD suivant.

**Comment lire ce sujet.** Le « Contexte » se lit une fois. Chaque étape donne ensuite une liste numérotée d’**Actions**, le verbe en gras. « **Sur la feuille :** » dit ce que vous écrivez sur la feuille de groupe, celle du TD1. « **Résultat attendu :** » dit ce que vous devez voir.

## Contexte, à lire une fois

Au TD1, vous avez rendu le moteur imbattable en changeant une ligne de `Main`, et une seule : le choix d’un coup, `ChooseBestMoveService`, ne nomme aucun évaluateur. Il ne dépend que du **contrat d’évaluation d’une position**, l’interface `EndgameOracle`, et reçoit l’évaluateur tout fait, par son constructeur.

Aujourd’hui, le code fourni est le même moteur, mais **mal construit** : `ChooseBestMoveService` crée lui-même son évaluateur, avec `new NaiveOracle()`, dans son constructeur. Le programme joue exactement comme au TD1. Pourtant, il viole la règle unique des couches : le choix, dans l’application, nomme une classe d’adaptateur. Vous allez mesurer ce que cela coûte, puis faire disparaître le défaut et prouver qu’il a disparu.

Les séances suivent désormais cinq étapes : le code fourni avec un défaut, le diagnostic, le principe de la solution, le refactoring, la vérification.

**Rappel.** Une flèche de **dépendance** va de la classe qui écrit le nom d’un type vers ce type : champ, paramètre, résultat, `new`, `implements`. Un **appel** à l’exécution n’est pas une dépendance : `ChooseBestMoveService` appelait `NaiveOracle` au TD1 sans en dépendre.

## Étape 1 — Le code fourni, et son défaut (15 min)

Tous les chemins sont relatifs à `TD2/src/main/java/org/chesskrk`.

**Actions**

1. **Mettez à jour** votre clone, **Git > Update Project** (`Ctrl+T`), puis **ouvrez** `TD2/pom.xml` comme au TD1 : **File > Open**, **Open as Project**, **This Window**.
2. **Lancez** `infrastructure/Main.java` et **jouez** deux coups. **Résultat attendu :** le moteur joue comme au début du TD1, des échecs sans suite.
3. **Ouvrez** `application/usecase/ChooseBestMoveService.java` et **lisez** son constructeur et ses imports. **Sur la feuille :** la ligne qui crée l’évaluateur ; le nom de package qui apparaît dans les imports et qui n’y était pas au TD1.
4. **Ouvrez** `infrastructure/Main.java`, méthode `buildUseCase`. **Sur la feuille :** ce que `Main` ne décide plus.
5. **Lancez** tous les tests : clic droit sur `src/test/java`, **Run 'All Tests'**. **Résultat attendu :** tout passe sauf deux règles de `CleanArchitectureTest`. **Sur la feuille :** la ligne de code que son message désigne, et la règle qu’il énonce.

## Étape 2 — Le diagnostic (20 min)

Le changement annoncé est celui du TD1 : évaluer avec `RetrogradeTablebase` au lieu de `NaiveOracle`.

**Actions**

1. **Reprenez** le graphe de dépendances du TD1, **ajoutez** la flèche nouvelle en rouge et **barrez** celles qui ont disparu de `Main`. **Sur la feuille :** la déclaration Java qui justifie la flèche nouvelle.
2. **Estimez** l’impact du changement avec la méthode du TD1, flèches qui arrivent sur ce qui change. **Sur la feuille :** quels fichiers doivent être modifiés ; est-ce encore une ligne de `Main` ?
3. **Répondez** à cette question : on veut un test qui vérifie que le choix prend le mat le plus rapide, sans lancer la table ni dépendre des trois règles de `NaiveOracle`. Avec ce constructeur, est-ce possible ? **Sur la feuille :** votre réponse et pourquoi.
4. **Nommez** le défaut en une phrase, avec les mots « responsabilité de choisir un coup » et « responsabilité d’évaluer une position » : qui dépend de quoi, de trop ?

**Faites un point avec l’enseignant** avant de continuer.

## Étape 3 — Le principe de la solution (15 min)

Le choix ne doit dépendre que du **contrat** d’évaluation, jamais d’une classe qui le tient. L’évaluateur lui est **donné par son constructeur**, tout fait, par celui qui assemble : c’est l’**injection par le constructeur**. Créer, c’est dépendre ; recevoir, non.

**Actions**

1. **Dessinez** le graphe cible, à côté du graphe actuel : `ChooseBestMoveService` ne pointe plus que vers `EndgameOracle` ; qui pointe vers `NaiveOracle` ?
2. **Listez** les fichiers qui changeront pour obtenir ce graphe, et dans quel sens. **Sur la feuille :** cette liste ; combien de lignes pour chacun, à votre avis.
3. **Vérifiez** sur le graphe cible que le changement annoncé, brancher la table, redevient une ligne de `Main`.

## Étape 4 — Le refactoring (25 min)

Un refactoring change la construction du programme sans changer ce qu’il fait : le moteur doit jouer pareil avant et après.

**Actions**

1. **Remplacez**, dans `ChooseBestMoveService.java`, le constructeur par un constructeur qui **reçoit** l’évaluateur :

   ```java
   public ChooseBestMoveService(EndgameOracle oracle) {
       this.oracle = oracle;
   }
   ```

2. **Supprimez** l’import de `NaiveOracle`, devenu inutile. **Résultat attendu :** `ChooseBestMoveService.java` ne contient plus le mot `adapter`.
3. **Corrigez** `Main.buildUseCase`, qui ne compile plus : c’est lui qui crée l’évaluateur et le passe au service ; **importez** `NaiveOracle` avec `Alt+Entrée`, comme au TD1.
4. **Relancez** `Main` et **rejouez** deux coups. **Résultat attendu :** le moteur joue comme avant.
5. **Relancez** tous les tests. **Résultat attendu :** `CleanArchitectureTest` passe.
6. **Validez** (commit), message « TD2 : injection de l’évaluateur par le constructeur », et **poussez** (push). Pour l’instant, chacun pousse sur son dépôt personnel, celui des TP ; un groupe peut aussi choisir le dépôt de l’un de vous comme dépôt du groupe, sans empêcher chacun de pousser ses propres essais sur le sien.

**Sur la feuille :** les fichiers modifiés et le nombre de lignes, comparés à votre liste de l’étape 3.

## Étape 5 — La vérification (20 min)

Le refactoring a rendu possible le test de l’étape 2 : un test du choix avec un **évaluateur factice**, une classe écrite dans le test, qui tient le contrat d’évaluation et répond ce qu’on veut. Ni la table ni `NaiveOracle` n’interviennent.

**Actions**

1. **Créez** le package `org.chesskrk.application` sous `src/test/java` (clic droit, **New > Package**), puis la classe de test `ChooseBestMoveServiceTest`, avec cet évaluateur factice ; **importez** chaque nom souligné en rouge avec `Alt+Entrée` : une position mat vaut une défaite immédiate pour le camp au trait, toute autre position vaut nulle.

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

2. **Écrivez** un test : roi blanc b6, tour h7, roi noir a8, les blancs au trait ; le service construit avec `new EvaluateurFactice()` doit choisir le coup h7–h8, le mat du TD1. Une position s’écrit `Position.of(Square.parse("b6"), Square.parse("h7"), Square.parse("a8"), Color.WHITE)`, un coup `new Move(Square.parse("h7"), Square.parse("h8"))`, et `chooseMove` rend un `Optional<Move>`.
3. **Lancez** le test. **Résultat attendu :** vert, sans qu’aucune classe du dossier `adapter` soit nommée dans le test.
4. **Écrivez** un second test, à votre choix. Par exemple : un évaluateur factice qui déclare gagnée pour le camp au trait toute position où la tour est encore là ; les noirs au trait, roi noir à côté de la tour : le service doit la prendre.
5. **Validez** (commit) et **poussez** (push), comme à l’étape 4.

**Sur la feuille :** ce que ce test prouve, et ce qu’il ne prouve pas ; pourquoi il était impossible avant le refactoring.

## Bilan (10 min)

**Sur la feuille**, une ligne chacune :

1. le défaut : qui dépendait de qui, et pourquoi c’était de trop ;
2. la solution, avec les mots « contrat d’évaluation d’une position » et « injection par le constructeur » ;
3. ce que le test avec l’évaluateur factice a permis de vérifier sans adaptateur ;
4. la ligne de `Main` qui brancherait la table maintenant.

**Conservez** la feuille : au TD3, le défaut sera dans les règles du jeu, et il faudra le remonter dans le domaine.
