Game of Life
------------------
An implementation of Conway's Game of Life, with animation in the terminal.

To run:

1. Install [leiningen](https://github.com/technomancy/leiningen).
2. Clone this repository.
3. In the terminal, in the repo directory: `lein repl`
4. In the repl: `(play <width> <height> <milliseconds-per-round>)`
5. Adjust the height and width of your terminal window.
6. Repeat steps 4 and 5 until the dotted line in the window is just below the top of the window, and does not wrap around to more than one line.
7. Boom, animation!

![game of life gif](readme_images/game_of_life.gif)
