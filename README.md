# Melodicorn

Some kind of music typesetting / editing thing. Not sure yet. Written in ClojureScript for use in the browser.

## Why?

Why anything?

## Running

Two tabs open:

    $ lein cljsbuild auto

and

    $ lein ring server-headless 3000

## Roadmap

### Done

- bar lines

### To do

- key signatures
- time signatures
  - initial
  - change of key signature
- notes
- rests
- once level 0 is more settled, a path representation
- include paths for all entities
- validation rules for time signature and beats-in-bar
- validation exception / disabling

## Screenshots

### First commit

![Screenshot 1](https://raw.githubusercontent.com/afandian/melodicorn/develop/docs/screenshots/26-05-2014/first.png)

### With clefs

![Screenshot 2](https://raw.githubusercontent.com/afandian/melodicorn/develop/docs/screenshots/29-06-2014/second.png)
