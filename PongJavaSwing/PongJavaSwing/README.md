# Pong - Java Swing

Jeu à deux joueurs avec deux raquettes et une balle.

- Joueur gauche : Z (monter), S (descendre)
- Joueur droit : flèche haut / flèche bas
- Espace : pause / reprendre
- R : recommencer
- Le premier à 7 points gagne

Compiler :
```bash
mkdir -p build
javac -d build src/PongGame.java
java -cp build PongGame
```

Créer et lancer le JAR :
```bash
jar --create --file PongGame.jar --main-class PongGame -C build .
java -jar PongGame.jar
```
