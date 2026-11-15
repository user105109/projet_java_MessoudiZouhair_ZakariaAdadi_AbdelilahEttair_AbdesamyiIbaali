# PaintApp — Java Swing

Application de dessin de bureau en Java Swing, construite avec les classes du cours Java II.

## Fonctionnalités
- Dessin à main levée
- Gomme
- Ligne, rectangle, ellipse
- Choix de couleur et épaisseur du trait
- Annuler / rétablir
- Effacer tout
- Ouvrir une image
- Enregistrer en PNG ou JPEG
- Interface française / anglaise
- Menus Fichier, Édition et Aide

## Prérequis
JDK 21 (ou une version compatible).

## Compiler et exécuter sous Ubuntu/Linux

Depuis le dossier `PaintApp` :

```bash
mkdir -p build
javac -d build src/*.java
cp resources/*.properties build/
java -cp build Main
```

## Créer un JAR exécutable

Depuis le dossier `PaintApp` :

```bash
jar --create --file PaintApp.jar --main-class Main -C build .
java -jar PaintApp.jar
```

Le JAR est autonome pour ce projet : il contient les classes compilées et les fichiers de traduction. Java doit néanmoins être installé sur l'ordinateur qui l'exécute.

## Remise
Fournir le code source (`src/`), les ressources de langue (`resources/`) et `PaintApp.jar`.
