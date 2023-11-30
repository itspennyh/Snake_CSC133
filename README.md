# Snake_CSC133

### Update 1

In this update, I've encountered some issues with the comments in the code. Therefore, I'm providing additional explanations here:

1. **Control Zones:** It appears that there are now two control zones in the game, which could use clearer marking. For instance, clicking on the left side of the screen should make the snake turn left based on its current heading, with similar logic for the right side.


2. **Rendering Improvement:** I've made changes to the `draw` method in `SnakeGame.java` to enhance graphics rendering. This is part of the process of building a gamestate manager. Instead of rendering the current game graphics every time, it will now render either the pause screen or the playing screen, but not both simultaneously.


3. **Game States:** The `mpause` state removes the game thread and cannot coexist with other active game states. On the other hand, the `mplaying` state creates a new thread. We need to determine where the check for these states should occur and provide the gamer with access to close the game, making `mPause` more like a load-in splash screen. It's also worth considering if a game thread that doesn't utilize a tick rate needs to run.


5. **SoundPool Deprecation:** Note that the `SoundPool` class is marked as deprecated, so we should explore alternatives for handling sound in the game.

### Update 2, v0.3

This update added notes on the location of the bug that is now causing runtime issues. Here are some highlights:

1. **Game State manager:** Implemented game state manager in SnakeGame.Java, utilizing a singleton design


2. **Game State bug:** logic error; mPlaying == !mPaused assumption, not true! they are not strictly correlated.
    Around line 166 in SnakeGame.Java, in regards to draw() function.

2a. **Game State Bug Now Functional** Works for only one loop, then gets stuck on intro load screen.
