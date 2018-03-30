# Euchre Game

This java app allows to play a simple game of Euchre.  This was designed for practice and as such the AI opponents are omnicient.  There is an alternate means of play that uses win percentages calculated by repeated AI plays.

Current features and limitations are:

+ Omnicient AI opponents
+ Simulated AI opponents
- no ability to call a loner


#### Build

```
ant
```


#### Running
For normal play against the omnicient AI opponents:

```
java -jar dist/Euchre.jar
```

For playing with simulated AI (ability varies):

```
java -cp dist/Euchre.jar Simulate.SimEuchreGUIFrame
```

