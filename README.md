# Snake_CSC133

The notes in the comment did not come out as expected so I'll add them here as well:

Looks like there are two zones of control now in the game, it would help if they were marked.
IE clicking on the left side of the screen has snake take a left turn based on its current heading. Same logic for right.

I altered the draw method in SnakeGame.java to further separate the rendering of graphics, as part of building a gamestate manager.
Instead of rendering current game graphics every time, it will now render the pause screen or the playing screen, rather than both at the same time.

mpause removes game thread, cannot co-exist with other active game states, mplaying creates thread.
So where does the check need to happen? Where can we sit a pause screen, give gamer access to close game, so that mPause is really more like a load in splash.
I suppose a game thread doesn't need to run, or rather one that doesn't utilize tick rate.

might need to do something about how actions with the snake are handled too.
Control passes thru a switch operation with a single event, action up.
Should probably refactor as that control I believe got shifted to Snake.java

SoundPool is noted as being deprecated