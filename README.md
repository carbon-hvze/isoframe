# Isomophic TodoMVC done with re-frame

A [re-frame](https://github.com/Day8/re-frame) implementation of [TodoMVC](http://todomvc.com/).

This todo example is based on official re-frame example. 
The rationale is the following:
1. To show how we can create isomorphic applications i.e. write code that can be reused across different platforms.
2. To give new perspective on how ui testing could be performed.
Application consists of three parts - server, web and mobile ui.
Corresponding talk was given on IT Global Meetup in Saint Petersburg, Russia.

## Setup And Run

1. Install [Leiningen](http://leiningen.org/)  (plus Java).

2. Launch server
   ```
   lein repl
   ```

3. Launch web ui
   ```
   lein with-profile web figwheel
   ```

4. Or (and) mobile ui
   ```
   lein with-profile mobile figwheel
   ```
