% Want traces that satisfy the following
good :- 
   holdsat(olocation(key1,hallway1),I0),
   holdsat(alocation(agent2,hallway1),I1),I0<I1,
   holdsat(holds(agent2,key1),I2),I1<I2,
   holdsat(alocation(agent2,office1),I3),I2<I3,
   holdsat(holds(agent2,laptop1),I4),I3<I4.
:- not good.
#hide.
%#show holdsat(holds(agent2,laptop1),I).
#show holdsat/2.
#show observed/2.
%#show occurred/2.
% #show initiated/2.
% #show terminated/2.
