% Want traces that satisfy the following
observed(exit(agent1,office1),insecureObject,0).
observed(leave(agent1,key1),insecureObject,1).
observed(take(agent2,key1),insecureObject,2).
observed(enter(agent2,office1),insecureObject,3).
observed(take(agent2,laptop1),insecureObject,4).
#show observed/3.
#show initiated/3.
#show terminated/3.
