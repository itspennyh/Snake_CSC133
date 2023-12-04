# Snake_CSC133

Update #1: Created Lava, an extension of Apple class, as an obstacle that will end the game (kill the snake) if the player runs into it.
Need to: work on group spawning for the Lava objects so that the Lava spawns in clusters instead of only one at a time, 
         make sure Lava spawns do not overlap with the any of the apple spawns

Update #2: Added multiple lava spawns, lava blocks will spawn in groups of up to 5 (number currently set to 5 in newGame() method in SnakeGame.java, can be changed later),
           done through the Lava class extension in Apple.java in lavaSpawns(),
           added checkOverlapAndSpawn() to prevent lava blocks from spawnning over regular and rotten apples
Need to: work on error Mike noticed of apples spawning half on screen, half off screen (have not encountered this myself)
