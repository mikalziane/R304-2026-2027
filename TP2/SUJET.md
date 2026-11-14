# R304 TP2 Impact des changements et dépendances nuisibles

Mikal Ziane — BUT Informatique — 2026–2027

Durée : 3 heures. Thèmes : impact des changements, spécification et implémentation d’un type, polymorphisme, diagramme d’architecture, dépendance nuisible.

## Démarrage 15 minutes

Mettez à jour votre clone du dépôt R304, puis ouvrez `TP2/pom.xml` dans IntelliJ IDEA comme au TP1. Vous devez voir les deux exercices `robot` et `matrices`. Lancez `RobotTest`, `CalculTest` et `MatricesTest` : cinq tests doivent réussir. Validez (commit) votre travail sur votre dépôt personnel à la fin de chaque exercice.

## Exercice 1 : le robot et son monde (1 h 15)

Le code Java fourni simule un robot explorateur dans un espace à une seule dimension. Le robot démarre juste devant sa base, en position 1, et part explorer le monde.

Le robot consomme un minerai pour chaque déplacement d’une case, en avant ou en arrière. Il part donc avec une certaine quantité de minerai et il peut en récolter s’il en trouve sur son chemin.

Toutefois, le robot veille à ne pas trop s’éloigner de sa base sous peine de tomber en panne sèche : s’il ne lui reste pas assez de minerai pour aller sur la case suivante, il abandonne sa mission et rentre à sa base.

Il a été annoncé que ce qui concerne l’environnement du robot allait changer. L’environnement ne sera plus forcément représenté de la même façon (par un `StringBuilder`), même si dans les tests des chaînes de caractères seront toujours utilisées.

On supposera que le monde restera linéaire (une seule dimension) et que le robot continuera de connaître sa position, c’est-à-dire la distance qui le sépare de sa base.

Étant donné l’annonce de ces changements, la classe `Robot` ne respecte pas le principe SRP : elle ne doit pas s’occuper des détails de la représentation du monde mais uniquement de ce qui concerne les robots.

Il faut que la classe `Robot` adopte une vue plus abstraite du monde, qui suppose seulement ceci :

1. Le monde peut toujours être créé à partir d’une chaîne de caractères, comme dans les tests actuels, qui doivent rester inchangés.
2. On peut regarder ce qui se trouve à une certaine position (position par rapport à la base du robot). La réponse est un caractère, comme actuellement.
3. On peut toujours tenter de ramasser du minerai à une certaine position, comme actuellement.

**Action 1.** **Modifiez** la classe `Robot` pour l’immuniser contre un changement de la représentation du monde. Il ne s’agit pas de changer la représentation du monde, qui restera pour le moment un `StringBuilder`, mais de l’encapsuler dans une entité plus abstraite correspondant aux hypothèses énoncées ci-dessus.

**Action 2.** **Vérifiez** que les tests continuent de passer correctement.

## Exercice 2 : matrices et dépendances nuisibles (1 h 30)

Le projet `matrices` est un embryon d’un programme de calcul matriciel. Rappelons qu’une matrice H×L est un objet mathématique similaire à un tableau à deux dimensions : sa hauteur H et sa largeur L. Si M est une matrice 2×3, alors ses postes sont

```text
M11 M12 M13
M21 M22 M23
```

Un moyen simple d’implémenter une matrice est donc tout simplement à l’aide d’un tableau à deux dimensions (voir la classe `MatricePleine`). Toutefois, si de nombreux postes de la matrice sont nuls, alors il est possible de gagner de la place en ne stockant que les coordonnées et la valeur des postes non nuls (voir la classe `MatriceCreuse`). Par exemple, si seul le poste d’indices (1,3) est non nul, alors une matrice creuse associera simplement à la clé (1,3) la valeur de ce poste (mettons 5), ce qui est plus compact que de stocker le tableau complet :

```text
0 0 5
0 0 0
```

La classe `Calcul` doit permettre de manipuler des matrices (une seule dans cette version), mais sans avoir à se préoccuper de leur implémentation (`MatriceCreuse`, `MatricePleine`, voire d’autres classes à l’avenir).

**Action 3.** Quelles dépendances sont nuisibles dans la classe `Calcul` ? **Indiquez-les** en rouge sur un diagramme d’architecture. Si vous n’avez pas de stylo rouge, biffez d’une croix les dépendances d’utilisation et le nom des entités mal placées (dépendance de contenance).

**Action 4.** **Dessinez** le diagramme d’une architecture qui corrige ce problème, puis **mettez-la en œuvre** dans le code. Les tests doivent continuer de passer.
