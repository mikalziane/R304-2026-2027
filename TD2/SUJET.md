# TD2 — Une dépendance de trop

R304 FI — groupes de 3 à 5, les mêmes qu’au TD1. N’utilisez pas d’IA pendant le TD. Ce sujet occupe deux à trois séances. Les durées sont indicatives : ce qui n’est pas fini se termine au TD suivant.

**Comment lire ce sujet.** Le « Contexte » se lit une fois. Chaque étape donne ensuite une liste numérotée d’**Actions** ; le verbe de chaque action est en gras. « **Sur la feuille :** » indique ce que vous devez écrire sur la feuille de groupe, celle du TD1. « **Résultat attendu :** » indique ce que vous devez voir.

## Contexte, à lire une fois

Le code fourni aujourd’hui est une version du moteur d’échecs que vous connaissez. Ce moteur joue mal : il donne des échecs inutiles. Surtout, son code contient une **dépendance nuisible** : la classe qui choisit le coup, `ChooseBestMoveService`, crée elle-même son évaluateur dans son constructeur, avec `new NaiveOracle()`. Or `ChooseBestMoveService` appartient à la couche application, et `NaiveOracle` à la couche des adaptateurs : la règle des couches n’est pas respectée.

Vous allez d’abord mesurer ce que cette dépendance coûte. Vous la supprimerez ensuite, et vous prouverez qu’elle a disparu.

Désormais, chaque TD se déroule en cinq étapes : la découverte du code fourni et de son défaut, le diagnostic, le principe de la solution, le refactoring (la modification du code sans changement de comportement) et la vérification. Deux prolongements suivent : ils montrent ce que le refactoring rend possible.

**Rappel.** Une flèche de **dépendance** va de la classe dont le code contient le nom d’un type vers ce type. Ce nom peut être le type d’un champ, d’un paramètre ou du résultat d’une méthode ; il peut aussi suivre `new` ou `implements`. Un **appel** à l’exécution n’est pas une dépendance. Une classe peut appeler une méthode d’un objet sans dépendre de la classe de cet objet : il suffit que le code de la classe appelante ne désigne cet objet que par une interface.

## Étape 1 — Le code fourni et son défaut (20 min)

Sauf indication contraire, les chemins des fichiers Java sont donnés à partir de `TD2/src/main/java/org/chesskrk`.

**Actions**

1. **Récupérez** le dossier `TD2` dans votre dépôt local, comme vous l’avez fait pour le TD1 : **Git > Update Project** (`Ctrl+T`). **Résultat attendu :** le dossier `TD2` apparaît dans le panneau **Project** ; sinon, appelez l’enseignant. **Ouvrez** ensuite `TD2/pom.xml` : **File > Open**, puis **Open as Project** et **This Window**.
2. **Lancez** `infrastructure/Main.java` et **jouez** deux coups. **Résultat attendu :** le moteur répond à chaque coup, et il donne des échecs inutiles.
3. **Ouvrez** `application/usecase/ChooseBestMoveService.java`, puis **lisez** le constructeur et les imports. **Sur la feuille :** la ligne qui crée l’évaluateur ; l’import qui désigne une classe d’une autre couche, et le nom de cette couche.
4. **Ouvrez** `infrastructure/Main.java` et **lisez** la méthode `buildUseCase`. **Sur la feuille :** quelle classe choisit l’évaluateur que le moteur utilise, `Main` ou `ChooseBestMoveService` ?
5. **Lancez** tous les tests : faites un clic droit sur le dossier `src/test/java`, puis choisissez **Run 'All Tests'**. **Résultat attendu :** tous les tests réussissent, sauf deux tests de `CleanArchitectureTest`. **Sur la feuille :** les deux messages d’erreur désignent la même ligne de code : laquelle ? Traduisez en français la règle que donne le message du test `applicationKnowsNoAdapters`.

## Étape 2 — Le diagnostic (30 min)

Le changement demandé est le suivant : le moteur doit évaluer les positions avec `RetrogradeTablebase`, la table de finales, au lieu de `NaiveOracle`.

**Actions**

1. **Dessinez** le graphe de dépendances du code fourni, limité à ces six types : `Main`, `ConsoleGame`, `ChooseBestMoveService`, `EndgameOracle`, `NaiveOracle` et `RetrogradeTablebase`. **Justifiez** chaque flèche par une ligne de code. `ConsoleGame` ne pointe vers aucun des cinq autres types : c’est normal. **Tracez** en rouge la dépendance nuisible. **Sur la feuille :** le graphe, et la ligne de code qui justifie la flèche rouge.
2. **Estimez** l’impact du changement demandé : cherchez dans le graphe les flèches qui pointent vers `NaiveOracle`, la classe à remplacer. **Sur la feuille :** les fichiers à modifier.
3. **Répondez** à la question suivante. On veut écrire un test de `ChooseBestMoveService` qui fixe lui-même l’évaluation de chaque position. Par exemple : « ce coup mène à un mat en 2 demi-coups, cet autre coup mène à un mat en 6 demi-coups ; `ChooseBestMoveService` doit choisir le premier ». Un demi-coup est le coup d’un seul camp. Est-ce possible avec le constructeur actuel de `ChooseBestMoveService` ? **Sur la feuille :** votre réponse et sa justification.
4. **Décrivez** le défaut en une phrase : quelle classe dépend d’une classe dont elle ne devrait pas dépendre ? Employez les expressions « responsabilité de choisir un coup » et « responsabilité d’évaluer une position ». **Sur la feuille :** cette phrase.

**Faites le point avec l’enseignant** avant de continuer.

## Étape 3 — Le principe de la solution (15 min)

`ChooseBestMoveService` ne doit dépendre que de l’**interface** `EndgameOracle`, et jamais d’une classe qui implémente cette interface. C’est `Main` qui crée l’évaluateur, puis qui le **passe en paramètre au constructeur** de `ChooseBestMoveService` : c’est l’**injection par le constructeur**. Une classe qui crée un objet avec `new` dépend de la classe de cet objet. Une classe qui reçoit un objet dans un paramètre dont le type est une interface ne dépend que de cette interface.

**Actions**

1. **Dessinez**, à côté du graphe actuel, le graphe à obtenir. Dans ce graphe, `ChooseBestMoveService` ne pointe plus que vers `EndgameOracle`. Quelle classe pointe alors vers `NaiveOracle` ?
2. **Listez** les fichiers qu’il faudra modifier pour obtenir ce graphe, et indiquez pour chacun ce qu’il faudra y changer. **Sur la feuille :** cette liste, avec votre estimation du nombre de lignes à modifier dans chaque fichier.
3. **Vérifiez**, sur le graphe à obtenir, que le changement demandé (remplacer `NaiveOracle` par `RetrogradeTablebase`) ne modifie plus que le fichier `Main.java`.

## Étape 4 — Le refactoring (25 min)

Un refactoring modifie l’organisation du code sans modifier ce que fait le programme : le moteur doit jouer de la même façon avant et après.

**Actions**

1. **Remplacez**, dans `ChooseBestMoveService.java`, le constructeur par celui-ci, qui reçoit l’évaluateur en paramètre :

   ```java
   public ChooseBestMoveService(EndgameOracle oracle) {
       this.oracle = oracle;
   }
   ```

2. **Supprimez** l’import de `NaiveOracle`, devenu inutile. **Résultat attendu :** `ChooseBestMoveService.java` ne contient plus le mot `adapter`.
3. **Corrigez** la méthode `Main.buildUseCase`, qui ne compile plus : c’est maintenant à elle de créer l’évaluateur et de le passer au constructeur de `ChooseBestMoveService`. **Importez** `NaiveOracle` avec `Alt+Entrée`.
4. **Relancez** `Main` et **rejouez** deux coups. **Résultat attendu :** le moteur joue comme avant.
5. **Relancez** tous les tests. **Résultat attendu :** tous les tests réussissent, y compris `CleanArchitectureTest`.
6. **Validez** vos modifications (commit) avec le message « TD2 : injection de l’évaluateur par le constructeur ». **Poussez**-les ensuite (push) avec **Git > Push**, comme en TP : la destination doit être `origin/main`, sur votre dépôt privé, et jamais le dépôt du cours. Votre groupe peut aussi choisir le dépôt de l’un d’entre vous comme dépôt commun. Chacun reste alors libre de pousser ses propres essais sur son dépôt personnel.

**Sur la feuille :** les fichiers modifiés et le nombre de lignes modifiées dans chacun ; comparez-les à votre liste de l’étape 3.

## Étape 5 — La vérification (60 min)

Grâce au refactoring, le test envisagé à l’étape 2 devient possible : on peut tester `ChooseBestMoveService` avec un **évaluateur factice**. Un évaluateur factice est une petite classe écrite dans le fichier de test. Elle implémente `EndgameOracle` et renvoie les évaluations que l’auteur du test a choisies. Le test n’utilise donc ni `RetrogradeTablebase` ni `NaiveOracle`.

**Actions**

1. **Créez** le package `org.chesskrk.application` sous `src/test/java` (clic droit, puis **New > Package**). **Créez**-y la classe de test `ChooseBestMoveServiceTest`. **Recopiez** l’évaluateur factice et le premier test ci-dessous à l’intérieur de cette classe, entre ses accolades. Avec `Alt+Entrée`, **importez** chaque nom affiché en rouge : choisissez toujours la classe du package `org.chesskrk`, et `java.util.Optional` pour `Optional`.

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

   @Test
   void choisitLeMatEnUnCoup() {
       ChooseBestMoveService service = new ChooseBestMoveService(new EvaluateurFactice());
       Position position = Position.of(Square.parse("b6"), Square.parse("h7"), Square.parse("a8"), Color.WHITE);
       Move coupAttendu = new Move(Square.parse("h7"), Square.parse("h8"));
       assertEquals(Optional.of(coupAttendu), service.chooseMove(position));
   }
   ```

2. **Lisez** ce code. L’évaluateur factice distingue deux cas : une position de mat est une défaite immédiate pour le camp au trait ; toute autre position est nulle. Le test part de cette position : roi blanc en b6, tour en h7, roi noir en a8, trait aux blancs. Il vérifie que `ChooseBestMoveService` choisit le coup h7-h8, qui met le roi noir mat. La méthode `chooseMove` renvoie un `Optional<Move>` : c’est pourquoi le coup attendu est placé dans `Optional.of(...)`.
3. **Lancez** le test. **Résultat attendu :** le test réussit (il s’affiche en vert), et il ne nomme aucune classe du dossier `adapter`.
4. **Écrivez** les quatre tests du tableau ci-dessous, sur le modèle du premier. Les trois premiers utilisent chacun leur propre évaluateur factice : écrivez pour chacun une petite classe, sur le modèle de `EvaluateurFactice`. Attention : un évaluateur répond toujours du point de vue du camp au trait dans la position qu’il reçoit. Ce camp est l’adversaire du camp qui vient de jouer.

   | Ce que le test vérifie : `ChooseBestMoveService`… | Ce que l’évaluateur factice répond | Position de départ | Résultat attendu |
   |---|---|---|---|
   | … choisit le plus rapide de deux mats. | La position est perdue pour le camp au trait : en 2 demi-coups si la tour est en a1, en 6 demi-coups sinon. | roi blanc en c3, tour en a5, roi noir en e7, trait aux blancs | le coup a5-a1 |
   | … préfère la nulle à la défaite. | La position est gagnée pour le camp au trait, en 5 demi-coups, si la tour est sur l’échiquier ; sinon elle est nulle. | roi blanc en h1, tour en d5, roi noir en d4, trait aux noirs | le coup d4-d5 : le roi noir prend la tour |
   | … choisit la défaite la plus lente quand tous les coups perdent. | La position est gagnée pour le camp au trait : en 1 demi-coup si le roi noir est en a8, en 9 demi-coups sinon. | roi blanc en c6, tour en h1, roi noir en b8, trait aux noirs | un coup qui n’amène pas le roi noir en a8 |
   | … ne choisit aucun coup quand il n’y a aucun coup légal. | L’évaluateur factice de l’action 1. | roi blanc en b6, tour en h8, roi noir en a8, trait aux noirs : le roi noir est mat | `chooseMove` renvoie un `Optional` vide |

   Ces indications vous serviront :

   - `position.rook()` renvoie la case de la tour, et `position.blackKing()` celle du roi noir ;
   - `position.hasRook()` indique si la tour est sur l’échiquier ;
   - deux cases se comparent avec `equals`, et non avec `==` : `position.rook().equals(Square.parse("a1"))` ;
   - `Evaluation.loss(n)` crée l’évaluation « position perdue en `n` demi-coups », `Evaluation.win(n)` l’évaluation « position gagnée en `n` demi-coups », et `Evaluation.DRAW` est l’évaluation « position nulle » ;
   - le coup contenu dans un `Optional` s’obtient avec `get()`, et la case d’arrivée de ce coup avec `to()` ;
   - `isEmpty()` indique si un `Optional` est vide.
5. **Lancez** tous les tests. **Résultat attendu :** tous les tests réussissent.
6. **Validez** (commit) et **poussez** (push), comme à l’étape 4.

**Sur la feuille :** votre premier test prouve-t-il que `NaiveOracle` évalue correctement les positions ? Prouve-t-il que le moteur sait mater ? Que prouve-t-il ? Pourquoi était-il impossible d’écrire ces tests avant le refactoring ?

## Prolongement 1 — Un second changement (25 min)

On demande maintenant un second changement : le moteur doit utiliser `RetrogradeTablebase` quand le programme est lancé avec l’argument `table`, et `NaiveOracle` dans tous les autres cas.

**Actions**

1. **Estimez** l’impact de ce changement avant d’écrire du code. **Sur la feuille :** les fichiers à modifier ; dites si `ChooseBestMoveService` en fait partie, et pourquoi.
2. **Modifiez** `Main`. Ajoutez à la méthode `buildUseCase` un paramètre `String[] args`. Cette méthode doit créer l’évaluateur avec `RetrogradeTablebase.build()` si `args` contient au moins un élément et si le premier élément est égal à `"table"`. Sinon, elle doit le créer avec `new NaiveOracle()`. Dans la méthode `main`, remplacez ensuite `buildUseCase()` par `buildUseCase(args)`, et `Main::buildUseCase` par `() -> buildUseCase(args)`.
3. **Lancez** le programme avec l’argument `table` : ouvrez **Run > Edit Configurations…**, écrivez `table` dans le champ **Program arguments**, puis lancez `Main`. **Résultat attendu :** le moteur vous mate en quatorze coups au plus.
4. **Effacez** le contenu du champ **Program arguments**, puis **relancez** le programme. **Résultat attendu :** le moteur donne de nouveau des échecs inutiles.
5. **Relancez** tous les tests, puis **validez** (commit) et **poussez** (push).

**Sur la feuille :** les fichiers modifiés, comparés à votre estimation. Dans le code fourni au début du TD, quelle classe aurait-il fallu modifier pour lire l’argument et créer les deux évaluateurs ?

## Prolongement 2 — L’évaluateur de votre groupe (35 min)

Si le temps manque, ce prolongement se fera au TD suivant.

Vous allez écrire votre propre évaluateur. Voici une idée de départ : moins le roi noir a de coups légaux, plus il est près d’être mat.

**Actions**

1. **Créez** la classe `GroupOracle` dans le package `org.chesskrk.adapter.naive`, à côté de `NaiveOracle`. Les tests d’architecture tiennent déjà compte de ce package : vous n’avez pas à les modifier. **Recopiez**-y ce code, qui réalise l’idée de départ :

   ```java
   public final class GroupOracle implements EndgameOracle {
       @Override
       public Evaluation evaluate(Position position) {
           GameStatus status = Rules.status(position);
           if (status == GameStatus.CHECKMATE) {
               return Evaluation.loss(0);       // le roi noir est mat
           }
           if (status != GameStatus.ONGOING) {
               return Evaluation.DRAW;          // la partie est nulle : pat, ou tour prise
           }
           if (Rules.isAttackedByBlack(position, position.rook())
                   && !position.whiteKing().isAdjacentTo(position.rook())) {
               return Evaluation.win(50);       // la tour peut être prise
           }
           int coupsDuRoiNoir = Rules.legalMoves(position).size();
           return Evaluation.loss(10 + coupsDuRoiNoir);   // l'idée de départ
       }
   }
   ```

2. **Ajoutez** à `Main` un troisième cas : avec l’argument `groupe`, le moteur utilise votre évaluateur.
3. **Jouez** contre votre moteur. **Résultat attendu :** le moteur réduit peu à peu le nombre de cases où votre roi peut aller ; il ne réussit pas toujours à mater.
4. **Améliorez** la dernière ligne de la méthode `evaluate`. Par exemple, tenez compte de la distance entre les deux rois : `position.whiteKing().distanceTo(position.blackKing())`. **Défi :** obtenez que votre moteur vous mate plus souvent.
5. **Relancez** tous les tests, puis **validez** (commit) et **poussez** (push).

**Sur la feuille :** les fichiers créés et les fichiers modifiés pour ajouter votre évaluateur ; dites si `ChooseBestMoveService` a été modifié.

## Bilan (10 min)

**Sur la feuille**, écrivez une ligne pour chacun de ces cinq points :

1. le défaut : quelle classe dépendait de quelle autre classe, et pourquoi cette dépendance était nuisible ;
2. la solution, décrite avec les expressions « interface `EndgameOracle` » et « injection par le constructeur » ;
3. ce que les tests écrits avec des évaluateurs factices ont permis de vérifier sans utiliser aucune classe du dossier `adapter` ;
4. les fichiers qu’il a fallu modifier pour choisir l’évaluateur au lancement du programme ;
5. les fichiers qu’il faut créer ou modifier pour ajouter un nouvel évaluateur.

**Conservez** la feuille : au TD3, vous étudierez un autre défaut, dans le code des règles du jeu.
