# Fat Beats

A simple music app.

**Fat Beats** is an extended version of the *BeatBox* program from the great [Head First Java](https://en.wikipedia.org/wiki/Head_First_(book_series) "Head First Java") book. It uses MIDI to give user 16 drum instruments to play with 16-measure looped beats. A beat pattern can be saved, loaded, randomized (that gives surprisingly good results) or sent to a playlist. There's even a simple server that can receive patterns and broadcast them to other clients on LAN. A chat was in the plans, but in the end got only a GUI implementation. The undo/redo mechanism was probably the neatest thing I did at the time.

![Fat Beats](https://github.com/ThreefoldBurly/fat-beats/blob/master/fatbeats.png "Fat Beats")

#### HOW TO RUN

After cloning the repo add a `bin` directory to it and (on Linux) run commands:

`javac -d bin -sourcepath src src/fatbeats/*/*.java`

`java -cp bin fatbeats.server.Server`

`java -cp bin fatbeats.main.Launcher`

in a terminal.