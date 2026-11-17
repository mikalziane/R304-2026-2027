# TD1 — Un moteur d’échecs KRK qui joue mal, puis bien

R304 FI — 90 minutes — groupes de 3 à 5. Cette séance précède le premier cours. Vous n’avez besoin de connaître ni la Clean Architecture ni les échecs. Un ordinateur portable par groupe suffit. N’utilisez pas d’IA pendant le TD.

**Comment lire ce sujet.** La partie « Contexte » se lit une fois. Ensuite, chaque tranche horaire donne une liste numérotée d’**Actions** : une action par ligne, le verbe en gras. « **Sur la feuille :** » dit ce que vous écrivez sur la feuille de groupe (papier ou fichier). « **Résultat attendu :** » dit ce que vous devez voir. Rien d’autre n’est à rendre.

## Contexte, à lire une fois

### Le projet des TD

Pendant les TD, votre groupe va écrire et surtout concevoir un petit **moteur d’échecs** pour la finale **KRK**, *King, Rook, King* : un roi et une tour blancs contre un roi noir. Un moteur d’échecs part d’une position. Il énumère les coups légaux, évalue la position obtenue après chacun d’eux et joue le meilleur. Il communique aussi avec l’extérieur : il reçoit le coup de l’adversaire et annonce son propre coup, dans une console ou dans un logiciel qui affiche l’échiquier.

Aujourd’hui, vous recevez un moteur complet, qui fonctionne mais qui joue mal. Vous jouerez contre lui, puis vous étudierez son organisation. Enfin, vous le rendrez imbattable en changeant **une ligne** (et la ligne d’import correspondante). Vous n’aurez aucune classe ni aucun test à écrire.

Au TD suivant, le code fourni aura un défaut, et ce sera à vous de le trouver, de le corriger et de prouver qu’il a disparu. Au cours des séances suivantes, vous construirez le moteur étape par étape. Ce plan peut encore changer.

### La finale roi et tour contre roi

Les lettres désignent les colonnes et les chiffres désignent les lignes : b6 est la case située dans la colonne b, sur la ligne 6. ♔ est le roi blanc, ♖ la tour blanche et ♚ le roi noir ; dans la console du programme, ces pièces s’écrivent `K`, `R` et `k`. Un camp est **au trait** quand c’est à lui de jouer.

Trois règles suffisent aujourd’hui :

- la **tour** attaque toutes les cases de sa ligne et de sa colonne, mais elle ne peut pas passer par-dessus une pièce ;
- le **roi** se déplace d’une case, dans n’importe quelle direction ; il attaque donc toutes les cases voisines de la sienne. Il ne peut pas aller sur une case attaquée par l’adversaire (on dit aussi que cette case est « contrôlée ») ;
- un roi attaqué est en **échec**. Pour un roi, une case sûre est une case voisine, libre et non attaquée. Un roi en échec est **mat** s’il n’a aucune case sûre et s’il ne peut pas prendre la tour (elle n’est pas sur une case voisine, ou elle est défendue par le roi blanc). Son camp a alors perdu la partie.

**Position de départ de vos parties, les blancs au trait** : roi blanc en e1, tour en a1, roi noir en e5.

| | a | b | c | d | e | f | g | h |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 8 | | | | | | | | |
| 7 | | | | | | | | |
| 6 | | | | | | | | |
| 5 | | | | | ♚ | | | |
| 4 | | | | | | | | |
| 3 | | | | | | | | |
| 2 | | | | | | | | |
| 1 | ♖ | | | | ♔ | | | |

**Un exemple de mat, les noirs au trait.** La tour attaque le roi noir le long de la ligne 8. Les cases a7 et b7 sont attaquées par le roi blanc, et la case b8 par la tour. Le roi noir n’a donc aucune case sûre : il est mat.

| | a | b | c | d | e | f | g | h |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 8 | ♚ | | | | | | | ♖ |
| 7 | | | | | | | | |
| 6 | | ♔ | | | | | | |
| 5 | | | | | | | | |
| 4 | | | | | | | | |
| 3 | | | | | | | | |
| 2 | | | | | | | | |
| 1 | | | | | | | | |

Avec un roi et une tour contre un roi seul, les blancs peuvent toujours forcer le mat, en seize coups au plus s’ils jouent parfaitement. S’ils n’y parviennent pas, la partie est **nulle**. Cela arrive dans trois cas : la tour est prise ; le roi noir n’est pas en échec, mais il n’a plus aucun coup légal (c’est le **pat**) ; chaque camp a joué cinquante coups sans qu’il y ait mat.

### Les responsabilités du moteur, et ses couches

Une **responsabilité** est une tâche dont une partie du programme se charge. Chaque responsabilité peut devoir changer pour une raison qui lui est propre. On la désigne par un verbe.

| Responsabilité | Ce que ce code doit savoir | Où il se trouve |
|---|---|---|
| **Appliquer les règles du jeu** : cases, positions, coups légaux, échec, mat, pat. | Quels coups sont légaux. | `org.chesskrk.domain` : `Square`, `Position`, `Move`, `Rules`, `GameStatus`, `Color` |
| **Choisir un coup** : parmi les coups légaux, retenir celui qui mène à la meilleure position. | Comment comparer les coups, à condition qu’un évaluateur lui indique ce que vaut chaque position. | `org.chesskrk.application` : `ChooseBestMoveService`, la classe qui choisit le coup ; `ChooseMoveUseCase`, l’interface que cette classe implémente ; `EndgameOracle`, l’interface que tout évaluateur implémente ; `Evaluation`, la réponse d’un évaluateur |
| **Évaluer une position** : dire si elle est gagnée, nulle ou perdue, et en combien de coups. | Ce que vaut chaque position. La méthode de calcul peut varier. | `org.chesskrk.adapter.naive` : `NaiveOracle` ; `org.chesskrk.adapter.tablebase` : `RetrogradeTablebase` |
| **Recevoir et annoncer les coups** : recevoir le coup de l’adversaire, annoncer le coup du moteur, afficher l’échiquier. | Par quel moyen les coups sont reçus et annoncés : une console, ou le protocole UCI qu’utilisent les logiciels d’échecs. | `org.chesskrk.adapter.console` : `ConsoleGame` ; `org.chesskrk.adapter.uci` : `UciSession`, `FenCodec`, `UciMoveCodec` |
| **Assembler le programme** : créer les objets au démarrage et les relier entre eux. | Quelles classes fournissent les objets du programme. | `org.chesskrk.infrastructure` : `Main` |

Ces responsabilités sont réparties en quatre **couches**, du centre vers l’extérieur :

- le **domaine** contient les règles du jeu ;
- l’**application** contient la classe qui choisit le coup, `ChooseBestMoveService`, et deux interfaces : `ChooseMoveUseCase`, que cette classe implémente, et `EndgameOracle`, dont elle a besoin pour obtenir la valeur des positions ;
- les **adaptateurs** contiennent les évaluateurs, ainsi que les classes qui reçoivent et annoncent les coups ;
- l’**infrastructure** contient `Main`, qui assemble le programme.

Ce découpage s’appelle la **Clean Architecture** ; vous le retrouverez en cours. Il obéit à une seule règle : **une couche ne dépend que des couches situées plus près du centre**. Le domaine ne dépend d’aucune autre couche. L’application dépend du domaine, mais pas des adaptateurs : `ChooseBestMoveService` ne dépend ni de `ConsoleGame` ni de `RetrogradeTablebase`. Seule l’infrastructure dépend de toutes les autres couches.

Deux responsabilités voisines sont séparées par une **frontière**. Aujourd’hui, nous n’en étudions qu’une : celle qui sépare le choix d’un coup de l’évaluation d’une position. Le reste du moteur, y compris la partie UCI, servira dans les séances suivantes. Nous supposons que les pièces (un roi et une tour) ne changeront jamais : c’est pourquoi elles sont définies dans le domaine.

## 0–20 min — Ouvrir le projet et jouer un premier coup

Pendant la présentation de l’enseignant, une personne du groupe ouvre le projet sur son ordinateur portable ; les autres écoutent. Ensuite, changez régulièrement de rôle : une personne lit le sujet, une autre tient le clavier (c’est le pilote), une troisième écrit sur la feuille.

**Actions**

1. **Clonez** le dépôt dans IntelliJ IDEA, sauf si vous l’avez déjà fait au TP1. Sur l’écran d’accueil, cliquez sur **Get from VCS** (ou **Clone Repository**, selon la version) et saisissez l’URL `https://github.com/mikalziane/R304-2026-2027.git`. Choisissez comme destination votre dossier personnel ou, sur un poste de l’IUT, votre espace `Z:`.
2. **Mettez à jour** le dépôt si vous l’aviez déjà cloné : ouvrez son dossier avec **File > Open**, puis choisissez **Git > Update Project** (`Ctrl+T`). **Résultat attendu :** le dossier `TD1/src/main/java/org/chesskrk` contient les dossiers `domain`, `application`, `adapter` et `infrastructure`. Si ce n’est pas le cas, appelez l’enseignant.
3. **Ouvrez** le projet de la séance : choisissez **File > Open**, sélectionnez le fichier `pom.xml` du dossier `TD1`, puis cliquez sur **Open as Project** et sur **This Window**. Maven, l’outil qui compile le projet et télécharge ses bibliothèques, démarre alors. La première fois, il a besoin d’une connexion à Internet. **Résultat attendu :** après un moment, la barre de progression située en bas de la fenêtre disparaît.
4. **Choisissez** le JDK : dans **File > Project Structure > Project**, sélectionnez dans la liste **SDK** un **JDK 21** ou plus récent. S’il n’y en a pas, choisissez **Add SDK > Download JDK**.
5. **Lancez** `src/main/java/org/chesskrk/infrastructure/Main.java` en cliquant sur le triangle vert situé à côté de `main`. **Résultat attendu :** la fenêtre **Run** affiche deux lignes d’instructions, un échiquier dessiné avec des lettres, le coup du moteur, puis le message `Votre roi (e5) va en :`.
6. **Jouez** un coup : tapez la case d’arrivée de votre roi, par exemple `d4`, puis appuyez sur **Entrée**. **Résultat attendu :** le moteur répond aussitôt par son coup. Le programme refuse les coups illégaux. Pour abandonner, tapez `q`. Pour commencer une nouvelle partie, relancez `Main`.

**Solution de secours : le terminal.** N’utilisez le terminal que si les actions précédentes échouent. Un terminal est une fenêtre dans laquelle on tape des commandes.

1. **Ouvrez** le terminal d’IntelliJ IDEA : **View > Tool Windows > Terminal** (`Alt+F12` sous Windows).
2. **Vérifiez** que l’invite de commande (le texte affiché à gauche du curseur) se termine par `TD1` : c’est le dossier qui contient `pom.xml` et `mvnw.cmd`. Si elle se termine par `R304-2026-2027`, tapez `cd TD1`, puis appuyez sur **Entrée**. Dans tout autre cas, appelez l’enseignant.
3. **Tapez** les trois commandes ci-dessous, en appuyant sur **Entrée** après chacune. La première évite que les accents s’affichent mal.

   ```powershell
   chcp 65001
   .\mvnw.cmd -q compile
   java -cp target/classes org.chesskrk.infrastructure.Main
   ```

Si vous n’arrivez pas à lancer le programme, signalez-le et passez à la lecture du code. Les fichiers se lisent aussi dans un navigateur, à l’adresse `https://github.com/mikalziane/R304-2026-2027/tree/main/TD1/src`. L’enseignant montrera une partie d’échecs au vidéoprojecteur.

## 20–35 min — Jouer et observer

Le moteur part toujours de la même position : roi blanc en e1, tour en a1, roi noir en e5, les blancs au trait.

**Actions**

1. **Jouez** deux ou trois parties, en changeant de pilote. **Défi : tenez cinquante coups sans vous faire mater.**
2. **Observez** le moteur et répondez à quatre questions. Que fait sa tour ? Met-il votre roi en échec ? Laisse-t-il sa tour en prise, c’est-à-dire sur une case où votre roi peut la prendre ? Vous menace-t-il sérieusement de mat à un moment de la partie ?

**Sur la feuille :** vos quatre observations, puis votre réponse à la question suivante. Que manque-t-il au moteur : la connaissance des règles (lui arrive-t-il de jouer un coup illégal ?) ou la capacité à évaluer les positions ? Justifiez votre réponse par ce que vous avez observé.

## 35–55 min — Les classes de chaque couche et le chemin d’un coup dans le code

Tous les chemins de fichiers sont relatifs à `TD1/src/main/java/org/chesskrk`. Dans chaque dossier, le fichier `package-info.java` décrit le contenu du dossier en une phrase.

**Repères de lecture.** Le **type déclaré** d’un champ est le type écrit devant son nom dans sa déclaration. Une **interface** se reconnaît au mot-clé `interface`. Elle déclare des méthodes sans donner leur code. `Optional<Move>` se lit « un coup, ou rien s’il n’y a aucun coup légal ». L’expression `stream()…min(...)` parcourt une liste et retient l’élément dont le score est le plus petit ; vous n’avez pas besoin d’en comprendre le détail. Un **demi-coup** est le coup d’un seul camp. L’évaluateur donne toujours sa réponse du point de vue du camp au trait. Après un coup des blancs, le camp au trait est celui des noirs : une position perdue pour les noirs est donc bonne pour le moteur. Dans `NaiveOracle`, les nombres 2 et 50 sont des ordres de grandeur, et non un compte exact de demi-coups.

**Actions**

1. **Dépliez**, dans le panneau **Project** d’IntelliJ, les quatre dossiers `domain`, `application`, `adapter` et `infrastructure`. **Sur la feuille :** quatre cercles concentriques (le domaine au centre, puis l’application, les adaptateurs et l’infrastructure), avec le nom de chaque classe dans le cercle de sa couche. Aidez-vous du tableau des responsabilités.
2. **Ouvrez** `adapter/console/ConsoleGame.java` et **repérez**, dans la méthode `whiteMoves`, la ligne qui contient `engine.chooseMove(position)` : c’est là que la console demande un coup au moteur. **Sur la feuille :** le type déclaré du champ `engine`, en précisant si c’est une classe ou une interface.
3. **Ouvrez** `application/usecase/ChooseBestMoveService.java` et **lisez** la méthode `chooseMove`, étape par étape. `Rules.legalMoves` donne les coups légaux. `Rules.apply` calcule la position obtenue après chaque coup. `oracle.evaluate(...)` demande la valeur de cette position. Enfin, `min` retient le meilleur coup. **Sur la feuille :** la responsabilité dont cette classe se charge ; celle qu’elle confie à un autre objet, en précisant lequel.
4. **Ouvrez** `application/port/out/EndgameOracle.java` et **lisez**-le en entier. C’est une interface Java. Elle déclare une seule méthode, `evaluate`, qui reçoit une position et renvoie une `Evaluation`. Une `Evaluation` indique si la position est gagnée, nulle ou perdue pour le camp au trait, et en combien de demi-coups le mat arrive. Une interface joue le rôle d’un contrat : toute classe qui l’implémente s’engage à fournir ses méthodes. **Sur la feuille :** quelle classe a besoin de cette interface ; quelles classes l’implémentent.
5. **Ouvrez** `adapter/naive/NaiveOracle.java` et **lisez** la méthode `evaluate`. Le mot-clé `implements` signifie que cette classe implémente l’interface `EndgameOracle`. **Sur la feuille :** les trois cas simples que `evaluate` reconnaît ; ce que la méthode renvoie pour toute autre position ; le lien avec ce que vous avez observé en jouant.
6. **Écrivez** en quatre lignes, un fichier par ligne, le chemin d’un coup dans le code, depuis la console jusqu’à `NaiveOracle`.
7. **Faites expliquer** ce chemin au groupe par une personne autre que le pilote.

**Indice de lecture facultatif :** une variable dont le type déclaré est une interface peut désigner un objet de n’importe quelle classe qui implémente cette interface, comme dans l’exemple `Tarif` et `TarifFixe` de la présentation de l’enseignant. Il faut donc distinguer deux choses : le type déclaré, qui est écrit dans le fichier, et la classe de l’objet, dont le code s’exécute.

## 55–70 min — Le graphe de dépendances, et l’impact d’un changement

Une flèche de dépendance va de la classe ou de l’interface qui **utilise un type** vers ce **type utilisé**. Une classe utilise un type quand ce type est celui d’un de ses champs, d’un paramètre, d’une variable ou d’un résultat, quand elle crée un objet de ce type avec `new`, ou quand ce type est une interface qu’elle implémente. On regarde donc les noms écrits dans le code, et non les appels effectués à l’exécution.

**Actions**

1. **Recopiez** les cinq noms ci-dessous et **tracez** les flèches de dépendance entre eux. Chaque flèche doit correspondre à une déclaration du code Java. Vous pouvez ignorer les flèches vers le domaine, car toutes les classes l’utilisent. **Sur la feuille :** pour chaque flèche, la déclaration qui la justifie.

   ```text
   [ ConsoleGame ]   [ ChooseBestMoveService ]   [ EndgameOracle ]

                     [ NaiveOracle ]   [ RetrogradeTablebase ]
   ```

2. **Examinez** `ChooseBestMoveService` et `NaiveOracle` : y a-t-il une flèche de l’un vers l’autre, dans un sens ou dans l’autre ? À l’exécution, l’un appelle-t-il l’autre ? **Sur la feuille :** votre explication, appuyée sur l’indice de lecture.
3. **Estimez**, d’après votre graphe, l’impact du changement suivant. On veut que le moteur évalue les positions avec `RetrogradeTablebase` au lieu de `NaiveOracle`. `RetrogradeTablebase` est une table de finales : elle contient la valeur exacte de toutes les positions KRK. **Sur la feuille, avant de continuer :** les fichiers qu’il faudra modifier ; ceux qui resteront intacts ; votre réponse à la question « `ChooseBestMoveService` doit-il changer ? ».
4. **Ouvrez** `infrastructure/Main.java`, seulement après avoir écrit votre estimation, et **ajoutez** `Main` au graphe, avec ses flèches. **Sur la feuille :** ce que vous remarquez, et si votre estimation reste valable.

> **Indice facultatif : comment estimer l’impact d’un changement.** Cette estimation est moins simple qu’il n’y paraît. C’est la compétence que vous travaillerez pendant tout le parcours.
>
> 1. Nommez précisément ce qui change, et ce qui le remplace.
> 2. Dans le graphe, cherchez toutes les flèches qui **arrivent** sur ce qui change. Chaque fichier d’où part une de ces flèches contient le nom de ce qui change : il faudra le relire, et sans doute le modifier.
> 3. Si ce qui change est une interface, continuez : toutes les classes qui ont une flèche vers cette interface sont touchées à leur tour. Si l’interface ne change pas, l’impact ne va pas plus loin.
> 4. Ajoutez le fichier dans lequel il faudra écrire le nom du remplaçant : une classe doit bien créer le nouvel objet.
> 5. Une classe qui n’a aucune flèche vers ce qui change reste intacte, même si elle l’appelle à l’exécution.

**Faites le point avec l’enseignant** avant de continuer, et changez de pilote. La section suivante est repliée : ne l’ouvrez qu’après cet échange.

## 70–85 min — Faire le changement, vérifier l’impact

<details>
<summary>À ouvrir après avoir fait le point avec l’enseignant</summary>

**Actions**

1. **Remplacez**, dans la méthode `buildUseCase` de `infrastructure/Main.java`, la ligne

   ```java
   EndgameOracle oracle = new NaiveOracle();
   ```

   par

   ```java
   EndgameOracle oracle = RetrogradeTablebase.build();
   ```

2. **Importez** la classe. IntelliJ signale `RetrogradeTablebase` en rouge : placez le curseur sur ce nom, appuyez sur `Alt+Entrée`, puis choisissez **Import class**. L’import de `NaiveOracle` devient inutile et s’affiche en gris : vous pouvez le laisser ou le supprimer.
3. **Relancez** `Main` et **rejouez**. Le calcul de la table de finales prend une seconde au démarrage.

**Résultat attendu :** depuis la position de départ, le moteur mate en quatorze coups au plus.

**Sur la feuille :**

1. combien de coups vous avez tenu cette fois, et si le moteur donne encore des échecs inutiles ;
2. le nombre de fichiers et le nombre de lignes modifiés ; comparez ces nombres à votre estimation, et dites si votre graphe a changé ;
3. la classe de l’objet qui exécute maintenant l’appel `oracle.evaluate(...)` ; dites si le nom de cette classe apparaît dans `ChooseBestMoveService.java`, et recopiez la ligne de code qui le montre.

**Si vous avez le temps.** **Lancez** les tests : faites un clic droit sur le dossier `src/test/java`, puis choisissez **Run 'All Tests'**. **Résultat attendu :** les 41 tests réussissent. Parmi eux, `CleanArchitectureTest` vérifie automatiquement la règle des couches : si une classe de la couche application dépendait de `ConsoleGame` ou de `RetrogradeTablebase`, ce test échouerait.

</details>

## 85–90 min — Bilan et préparation de la suite

**Sur la feuille**, répondez en une ligne à chacune de ces trois questions :

1. ce qui a rendu le moteur imbattable : qu’est-ce qui a changé, la façon d’appliquer les règles, de choisir un coup, d’évaluer une position ou d’assembler le programme ?
2. pourquoi une seule ligne a suffi ; employez dans votre réponse les mots « interface `EndgameOracle` » ;
3. quels fichiers changeraient si l’on ajoutait demain un troisième évaluateur.

**Conservez** la feuille de groupe, avec le chemin d’un coup dans le code et le graphe de dépendances : vous en aurez besoin au TD2. Il n’y aura pas d’oral individuel, mais chacun de vous doit savoir expliquer une flèche du graphe. Au TD suivant, le code fourni aura un défaut : une classe qui dépend d’une classe dont elle ne devrait pas dépendre. Vous corrigerez ce défaut.

**Si le temps manque :** ne jouez qu’une seule partie ; traitez le chemin d’un coup dans le code et l’estimation de l’impact avec l’enseignant ; modifiez ensuite la ligne de `Main`. Le bilan peut se faire à l’oral, avec tous les groupes. Vous n’avez aucun travail à faire chez vous.
