# rummikub

This package aims to provide a Rummikub implementation with a machine learning flavor

[![Build Status](https://travis-ci.org/rostskadat/rummikub.svg?branch=master)](https://travis-ci.org/rostskadat/rummikub) [![Coverage Status](https://coveralls.io/repos/github/rostskadat/rummikub/badge.svg?branch=master)](https://coveralls.io/github/rostskadat/rummikub?branch=master)

### Evaluation function

Example of search function for board game: [Expectiminimax](https://en.wikipedia.org/wiki/Expectiminimax_tree)

### Deep Reinforcement learning

cf: https://github.com/deeplearning4j/rl4j

### Domain

[MDP](https://en.wikipedia.org/wiki/Markov_decision_process) 5-tuple (S, A, T, γ, R.):

– S = {s1, s2,...} is the possibly infinite set of states the environment can be in. All the tiles on the tables in the rack?
– A = {a1, a2,...} is the possibly infinite set of actions the agent can take. Tile allowed at any given point in the game. Basically the tiles that can be played on the table? 
– T(s|s, a) defines the probability of ending up in environment state after taking action a in state s. Depends on other players decision and random picks from the pool?
– γ ∈ [0, 1] is the discount factor, which defines how important future rewards are. The value of the tile that I can put on the table ?
– R(s, a, s1) is the possibly stochastic reward given for a state transition from s to s1 through taking action a. It defines the goal of an agent interacting with the MDP, as it indicates the immediate quality of what the agent is doing.

### Reinforcement Learning Algorithms

Look at [5] $4  

### Q-Learning

In our case Q(s, a) expected value of taking action a in state s. 

### References

* [1] [Abstracting Reusable Cases from Reinforcement Learning](https://www.cc.gatech.edu/~isbell/reading/papers/VonHessling-ICCBR05.pdf)
* [2] [A brief tutorial on reinforcement learning: The game of Chung Toi](https://www.elen.ucl.ac.be/Proceedings/esann/esannpdf/es2011-110.pdf)
* [3] [Reinforcement Learning for Board Games: The Temporal Difference Algorithm](http://www.gm.fh-koeln.de/ciopwebpub/Kone15c.d/TR-TDgame_EN.pdf)
* [4] [Multi-Stage Temporal Difference Learning for 2048-like Games](https://pdfs.semanticscholar.org/e11f/23691ca8f6dabbf701c367d9c09882e1690f.pdf)
* [5] [A Gentle Introduction to Reinforcement Learning](http://www.springer.com/cda/content/document/cda_downloaddocument/9783319458557-c2.pdf?SGWID=0-0-45-1586969-p180225375)
* [6] [AI Mahjong](http://cs229.stanford.edu/proj2009/Loh.pdf)
* [7] [DominAI](https://drive.google.com/open?id=1Axc0kvnM7QGYk0X7vewBACbpawsK-zjM)
* [8] [Giraffe](https://drive.google.com/open?id=1Y-l5--nAR6YgXh91HzbSM6Y8HRXv12A9)





