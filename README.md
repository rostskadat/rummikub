# rummikub

This package aims to provide a Rummikub implementation with a machine learning flavor

[![Build Status](https://travis-ci.org/rostskadat/rummikub.svg?branch=master)](https://travis-ci.org/rostskadat/rummikub) [![Coverage Status](https://coveralls.io/repos/github/rostskadat/rummikub/badge.svg?branch=master)](https://coveralls.io/github/rostskadat/rummikub?branch=master)

### Evaluation function

Example of search function for board game: [Expectiminimax](https://en.wikipedia.org/wiki/Expectiminimax_tree)

### Deep Reinforcement learning

cf: https://github.com/deeplearning4j/rl4j

### Domain

[MDP](https://en.wikipedia.org/wiki/Markov_decision_process) 5-tuple:

* State: All the tiles on the tables in the rack
* Action: Tile allowed at any given point in the game. Basically the tiles that can be played on the table.
* Probability: depends on other players decision and random picks from the pool.
* Reward: the tile that I can put on the table. 
* gamma: the discount factor (Should that be learned as well?)

### Q-Learning

In our case Q(s, a) expected value of taking action a in state s. 

### References

* [Abstracting Reusable Cases from Reinforcement Learning](https://www.cc.gatech.edu/~isbell/reading/papers/VonHessling-ICCBR05.pdf)
* [A brief tutorial on reinforcement learning: The game of Chung Toi](https://www.elen.ucl.ac.be/Proceedings/esann/esannpdf/es2011-110.pdf)
* [Reinforcement Learning for Board Games: The Temporal Difference Algorithm](http://www.gm.fh-koeln.de/ciopwebpub/Kone15c.d/TR-TDgame_EN.pdf)
* [Multi-Stage Temporal Difference Learning for 2048-like Games](https://pdfs.semanticscholar.org/e11f/23691ca8f6dabbf701c367d9c09882e1690f.pdf)
