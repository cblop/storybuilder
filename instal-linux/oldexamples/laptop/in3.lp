% Want traces that satisfy the following
observed(exit(agent1,office1),insecureObject,0).
:- observed(exit(agent1,office1),insecureObject,I),I!=0.
observed(leave(agent1,key1),insecureObject,1).
:- observed(leave(agent1,key1),insecureObject,I),I!=1.
observed(take(agent2,key1),insecureObject,2).
:- observed(take(agent2,key1),insecureObject,I),I!=2.
observed(enter(agent2,office1),insecureObject,3).
:- observed(enter(agent2,office1),insecureObject,I),I!=3.
observed(take(agent2,laptop1),insecureObject,4).
:- observed(take(agent2,laptop1),insecureObject,I),I!=4.
#hide.
#show observed/3.
