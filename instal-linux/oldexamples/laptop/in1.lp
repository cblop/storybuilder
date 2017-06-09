% Want traces with failed in the final state
:- not holdsat(failed,I),final(I).
% Don't want traces that satisfy any of the following
%--ENTER rules
   % A cannot enter hallway
:- observed(enter(A,hallway1),I).
   % A cannot enter same place twice in successive instants
:- observed(enter(A,L),I),observed(enter(t,L),J),J=I-1.
   % A cannot enter O if already in O
:- observed(enter(A,O),I),holdsat(alocation(A,O),I).
%--EXIT rules
   % A cannot exit hallway
:- observed(exit(A,hallway1),I).
   % A can only exit X if in X
:- observed(exit(A,L1),I),holdsat(alocation(A,L2),I),L1!=L2.
   % A can only exit X if unlocked(X)
:- observed(exit(A,L),I),holdsat(locked(L),I).
%--LEAVE rules
   % A cannot leave O unless A holds O at I-1
:- observed(leave(A,O),I),not holdsat(holds(A,O),I).
%--TAKE rules
   %  A cannot take O if A already holds O
:- observed(take(A,O),I),holdsat(holds(A,O),I).
   % A1 cannot take O if A2 already holds O (A1 may = A2)
:- observed(take(A1,O),I),holdsat(holds(A2,O),J),J=I-1.
   % A1 can't take O if A2 holds O
:- observed(take(A1,O),I), holdsat(holds(A2,O),I), A1!=A2.
   % A (in L) cannot take O if O is not in L
:- observed(take(A,O),I),
   holdsat(alocation(A,L1),I),holdsat(olocation(O,L2),I),L1!=L2.
%    % A cannot take O in an office if an employee is there
% :- observed(take(A,O),I),
%    holdsat(alocation(A,L),I),holdsat(employeeIn(L),I),
%    holdsat(attacker(A),I),holdsat(office(L),I).
%--HOLDS rules
% #hide.
% #show holdsat(holds(agent2,laptop1),I).
% #show holdsat/2.
% #show observed/2.
% #show occurred/2.
% #show initiated/2.
% #show terminated/2.

