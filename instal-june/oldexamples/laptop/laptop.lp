%
% -------------------------------PART 1-------------------------------
%
%
% Standard prelude for insecureObject
%
% suppress clingo warnings in absence of inertials, non-inertials or obligations
ifluent(0,0).
nifluent(0,0).
oblfluent(0,0).
% fluent rules
holdsat(P,In,J):- holdsat(P,In,I),not terminated(P,In,I),
    next(I,J),ifluent(P, In),instant(I),instant(J), inst(In).
holdsat(P,In,J):- initiated(P,In,I),next(I,J),
    ifluent(P, In),instant(I),instant(J), inst(In).
holdsat(P,In,J):- initiated(P,In,I),next(I,J),
    oblfluent(P, In),instant(I),instant(J), inst(In).
% all observed events occur
occurred(E,In,I):- evtype(E,In,ex),observed(E,In,I),instant(I), inst(In).
% produces null for unknown events
occurred(null,In,I) :- not evtype(E,In,ex), observed(E,In,I),
    instant(I), inst(In).
% produces gap warning for unknown events
unknown(E, In, I) :- not evtype(E,In,ex), observed(E,In,I),
    instant(I), inst(In).
warninggap(In, I) :- unknown(E,In,I), inst(In), instant(I).
% a violation occurs for each non-permitted action
occurred(viol(E),In,I):-
    occurred(E,In,I),
    evtype(E,In,ex),
    not holdsat(perm(E),In,I),
    holdsat(live(In),In,I),evinst(E,In),
    event(E),instant(I),event(viol(E)),inst(In).
occurred(viol(E),In,I):-
    occurred(E,In,I),
    evtype(E,In,inst),
    not holdsat(perm(E),In,I),
    event(E),instant(I),event(viol(E)), inst(In).
% needed until I tidy up some of the constraint generation
% true.
start(0).
instant(0..T) :- final(T).
next(T,T+1) :- instant(T).
final(horizon).
% externals for individual institutions
% #external observed(E,I) : event(E), inst(I). removed 20160415 JAP
% observed(E,I,J) :- observed(E,I), start(J). removed 20160415 JAP
#external holdsat(F,I) : fluent(F,I), inst(I).
holdsat(F,I,J) :- holdsat(F,I), start(J).
#external holdsat(perm(E),I) : event(E), inst(I).
holdsat(perm(E),I,J) :- holdsat(perm(E),I), start(J).
#external holdsat(pow(E),I) : event(E), inst(I).
holdsat(pow(I,E),I,J) :- holdsat(pow(E),I), start(J).
% for incremental observations
#external observed(E) : event(E).
compObserved(E,J) :- observed(E), start(J).
% for observation sequences
#external observed(E,I) : event(E), instant(I).
compObserved(E,I) :- observed(E,I).

%
% Rules for Institution insecureObject
%
  ifluent(live(insecureObject), insecureObject).
  fluent(live(insecureObject), insecureObject).
  inst(insecureObject).
%
% Constraints for observable events
%
%%  mode COMPOSITE is chosen:
{compObserved(E, J)}:- evtype(E,In,ex),instant(J), not final(J), inst(In).
:- compObserved(E,J),compObserved(F,J),instant(J),evtype(E,InX,ex),
   evtype(F,InY,ex), E!=F,inst(InX;InY). 
obs(I):- compObserved(E,I),evtype(E,In,ex),instant(I),inst(In).
      :- not obs(I), not final(I), instant(I), inst(In).
observed(E,In,I) :- compObserved(E,I), inst(In), instant(I).
%
% Exogenous events
% Event: leave (type: ex)
  event(leave(Agent0,Object1)) :- agent(Agent0),object(Object1).
  evtype(leave(Agent0,Object1),insecureObject,ex) :- agent(Agent0),object(Object1).
  evinst(leave(Agent0,Object1),insecureObject) :- agent(Agent0),object(Object1).
  ifluent(perm(leave(Agent0,Object1)), insecureObject) :- agent(Agent0),object(Object1).
  fluent(perm(leave(Agent0,Object1)), insecureObject) :- agent(Agent0),object(Object1).
  event(viol(leave(Agent0,Object1))) :- agent(Agent0),object(Object1).
  evtype(viol(leave(Agent0,Object1)), insecureObject, viol) :- agent(Agent0),object(Object1).
  evinst(viol(leave(Agent0,Object1)),insecureObject) :- agent(Agent0),object(Object1).
% Event: exit (type: ex)
  event(exit(Agent0,Location1)) :- agent(Agent0),location(Location1).
  evtype(exit(Agent0,Location1),insecureObject,ex) :- agent(Agent0),location(Location1).
  evinst(exit(Agent0,Location1),insecureObject) :- agent(Agent0),location(Location1).
  ifluent(perm(exit(Agent0,Location1)), insecureObject) :- agent(Agent0),location(Location1).
  fluent(perm(exit(Agent0,Location1)), insecureObject) :- agent(Agent0),location(Location1).
  event(viol(exit(Agent0,Location1))) :- agent(Agent0),location(Location1).
  evtype(viol(exit(Agent0,Location1)), insecureObject, viol) :- agent(Agent0),location(Location1).
  evinst(viol(exit(Agent0,Location1)),insecureObject) :- agent(Agent0),location(Location1).
% Event: take (type: ex)
  event(take(Agent0,Object1)) :- agent(Agent0),object(Object1).
  evtype(take(Agent0,Object1),insecureObject,ex) :- agent(Agent0),object(Object1).
  evinst(take(Agent0,Object1),insecureObject) :- agent(Agent0),object(Object1).
  ifluent(perm(take(Agent0,Object1)), insecureObject) :- agent(Agent0),object(Object1).
  fluent(perm(take(Agent0,Object1)), insecureObject) :- agent(Agent0),object(Object1).
  event(viol(take(Agent0,Object1))) :- agent(Agent0),object(Object1).
  evtype(viol(take(Agent0,Object1)), insecureObject, viol) :- agent(Agent0),object(Object1).
  evinst(viol(take(Agent0,Object1)),insecureObject) :- agent(Agent0),object(Object1).
% Event: enter (type: ex)
  event(enter(Agent0,Location1)) :- agent(Agent0),location(Location1).
  evtype(enter(Agent0,Location1),insecureObject,ex) :- agent(Agent0),location(Location1).
  evinst(enter(Agent0,Location1),insecureObject) :- agent(Agent0),location(Location1).
  ifluent(perm(enter(Agent0,Location1)), insecureObject) :- agent(Agent0),location(Location1).
  fluent(perm(enter(Agent0,Location1)), insecureObject) :- agent(Agent0),location(Location1).
  event(viol(enter(Agent0,Location1))) :- agent(Agent0),location(Location1).
  evtype(viol(enter(Agent0,Location1)), insecureObject, viol) :- agent(Agent0),location(Location1).
  evinst(viol(enter(Agent0,Location1)),insecureObject) :- agent(Agent0),location(Location1).
%
% null event for unknown events
% Event: null (type: ex)
  event(null).
  evtype(null,insecureObject,ex).
  evinst(null,insecureObject).
  ifluent(perm(null), insecureObject).
  fluent(perm(null), insecureObject).
  event(viol(null)).
  evtype(viol(null),insecureObject,viol).
  evinst(viol(null),insecureObject).
% Institutional events
%
% Violation events
%
%
% Creation events
%
%
% Dissolution events
%
%
% dissolve events
%
%
% inertial fluents
%
ifluent(hallway(Location0),insecureObject) :-
  location(Location0).
fluent(hallway(Location0),insecureObject) :-
  location(Location0).

ifluent(locked(Location0),insecureObject) :-
  location(Location0).
fluent(locked(Location0),insecureObject) :-
  location(Location0).

ifluent(alocation(Agent0,Location1),insecureObject) :-
  agent(Agent0),location(Location1).
fluent(alocation(Agent0,Location1),insecureObject) :-
  agent(Agent0),location(Location1).

ifluent(office(Location0),insecureObject) :-
  location(Location0).
fluent(office(Location0),insecureObject) :-
  location(Location0).

ifluent(unlocked(Location0),insecureObject) :-
  location(Location0).
fluent(unlocked(Location0),insecureObject) :-
  location(Location0).

ifluent(laptop(Object0),insecureObject) :-
  object(Object0).
fluent(laptop(Object0),insecureObject) :-
  object(Object0).

ifluent(holds(Agent0,Object1),insecureObject) :-
  agent(Agent0),object(Object1).
fluent(holds(Agent0,Object1),insecureObject) :-
  agent(Agent0),object(Object1).

ifluent(attacker(Agent0),insecureObject) :-
  agent(Agent0).
fluent(attacker(Agent0),insecureObject) :-
  agent(Agent0).

ifluent(olocation(Object0,Location1),insecureObject) :-
  object(Object0),location(Location1).
fluent(olocation(Object0,Location1),insecureObject) :-
  object(Object0),location(Location1).

ifluent(key(Object0),insecureObject) :-
  object(Object0).
fluent(key(Object0),insecureObject) :-
  object(Object0).

ifluent(employee(Agent0),insecureObject) :-
  agent(Agent0).
fluent(employee(Agent0),insecureObject) :-
  agent(Agent0).

%
% noninertial fluents
%
nifluent(situation2(Object0), insecureObject) :-
  object(Object0).
fluent(situation2(Object0), insecureObject) :-
  object(Object0).

nifluent(situation3(Object0), insecureObject) :-
  object(Object0).
fluent(situation3(Object0), insecureObject) :-
  object(Object0).

nifluent(situation1(Object0), insecureObject) :-
  object(Object0).
fluent(situation1(Object0), insecureObject) :-
  object(Object0).

nifluent(situation4(Object0), insecureObject) :-
  object(Object0).
fluent(situation4(Object0), insecureObject) :-
  object(Object0).

nifluent(employeeIn(Location0), insecureObject) :-
  location(Location0).
fluent(employeeIn(Location0), insecureObject) :-
  location(Location0).

nifluent(failed(Object0), insecureObject) :-
  object(Object0).
fluent(failed(Object0), insecureObject) :-
  object(Object0).

nifluent(vulnerable(Object0), insecureObject) :-
  object(Object0).
fluent(vulnerable(Object0), insecureObject) :-
  object(Object0).

%
% violation fluents (to be implemented)

%
% obligation fluents
%
%
% -------------------------------PART 2-------------------------------
%
%
% generate rules
%
%
% initiate rules
%
%
% Translation of leave(A,O) initiates ['olocation', ['O', 'L']] if ['and', ['holds', ['A', 'O']], ['alocation', ['A', 'L']]]
%
initiated(olocation(O,L),insecureObject,I) :-
   occurred(leave(A,O),insecureObject,I),
   holdsat(live(insecureObject),insecureObject,I), inst(insecureObject),
   holdsat(holds(A,O),insecureObject,I),
   holdsat(alocation(A,L),insecureObject,I),
   agent(A),
   location(L),
   object(O),
   inst(insecureObject), instant(I).
%
% Translation of take(A,O) initiates ['holds', ['A', 'O']] if ['and', ['and', ['and', ['alocation', ['A', 'L']], ['olocation', ['O', 'L']]], ['not', ['holds', ['A', 'O']]]], ['employee', ['A']]]
%
initiated(holds(A,O),insecureObject,I) :-
   occurred(take(A,O),insecureObject,I),
   holdsat(live(insecureObject),insecureObject,I), inst(insecureObject),
   holdsat(alocation(A,L),insecureObject,I),
   holdsat(olocation(O,L),insecureObject,I),
   not
   holdsat(holds(A,O),insecureObject,I),
   holdsat(employee(A),insecureObject,I),
   agent(A),
   location(L),
   object(O),
   inst(insecureObject), instant(I).
%
% Translation of take(A,O) initiates ['holds', ['A', 'O']] if ['and', ['and', ['and', ['alocation', ['A', 'L']], ['olocation', ['O', 'L']]], ['vulnerable', ['O']]], ['attacker', ['A']]]
%
initiated(holds(A,O),insecureObject,I) :-
   occurred(take(A,O),insecureObject,I),
   holdsat(live(insecureObject),insecureObject,I), inst(insecureObject),
   holdsat(alocation(A,L),insecureObject,I),
   holdsat(olocation(O,L),insecureObject,I),
   holdsat(vulnerable(O),insecureObject,I),
   holdsat(attacker(A),insecureObject,I),
   agent(A),
   location(L),
   object(O),
   inst(insecureObject), instant(I).
%
% Translation of exit(A,L1) initiates ['alocation', ['A', 'L2']] if ['and', ['and', ['and', ['office', ['L1']], ['hallway', ['L2']]], ['holds', ['A', 'K']]], ['key', ['K']]]
%
initiated(alocation(A,L2),insecureObject,I) :-
   occurred(exit(A,L1),insecureObject,I),
   holdsat(live(insecureObject),insecureObject,I), inst(insecureObject),
   holdsat(office(L1),insecureObject,I),
   holdsat(hallway(L2),insecureObject,I),
   holdsat(holds(A,K),insecureObject,I),
   holdsat(key(K),insecureObject,I),
   agent(A),
   object(K),
   location(L2),
   location(L1),
   inst(insecureObject), instant(I).
%
% Translation of exit(A,L1) initiates ['locked', ['L1']] if ['and', ['and', ['and', ['office', ['L1']], ['hallway', ['L2']]], ['holds', ['A', 'K']]], ['key', ['K']]]
%
initiated(locked(L1),insecureObject,I) :-
   occurred(exit(A,L1),insecureObject,I),
   holdsat(live(insecureObject),insecureObject,I), inst(insecureObject),
   holdsat(office(L1),insecureObject,I),
   holdsat(hallway(L2),insecureObject,I),
   holdsat(holds(A,K),insecureObject,I),
   holdsat(key(K),insecureObject,I),
   agent(A),
   object(K),
   location(L2),
   location(L1),
   inst(insecureObject), instant(I).
%
% Translation of exit(A,L1) initiates ['alocation', ['A', 'L2']] if ['and', ['and', ['unlocked', ['L1']], ['office', ['L1']]], ['hallway', ['L2']]]
%
initiated(alocation(A,L2),insecureObject,I) :-
   occurred(exit(A,L1),insecureObject,I),
   holdsat(live(insecureObject),insecureObject,I), inst(insecureObject),
   holdsat(unlocked(L1),insecureObject,I),
   holdsat(office(L1),insecureObject,I),
   holdsat(hallway(L2),insecureObject,I),
   agent(A),
   location(L2),
   location(L1),
   inst(insecureObject), instant(I).
%
% Translation of enter(A,L) initiates ['alocation', ['A', 'L']] if ['and', ['unlocked', ['L']], ['office', ['L']]]
%
initiated(alocation(A,L),insecureObject,I) :-
   occurred(enter(A,L),insecureObject,I),
   holdsat(live(insecureObject),insecureObject,I), inst(insecureObject),
   holdsat(unlocked(L),insecureObject,I),
   holdsat(office(L),insecureObject,I),
   agent(A),
   location(L),
   inst(insecureObject), instant(I).
%
% Translation of enter(A,L) initiates ['unlocked', ['L']] if ['and', ['and', ['office', ['L']], ['holds', ['A', 'K']]], ['key', ['K']]]
%
initiated(unlocked(L),insecureObject,I) :-
   occurred(enter(A,L),insecureObject,I),
   holdsat(live(insecureObject),insecureObject,I), inst(insecureObject),
   holdsat(office(L),insecureObject,I),
   holdsat(holds(A,K),insecureObject,I),
   holdsat(key(K),insecureObject,I),
   agent(A),
   object(K),
   location(L),
   inst(insecureObject), instant(I).
%
% Translation of enter(A,L) initiates ['alocation', ['A', 'L']] if ['and', ['and', ['office', ['L']], ['holds', ['A', 'K']]], ['key', ['K']]]
%
initiated(alocation(A,L),insecureObject,I) :-
   occurred(enter(A,L),insecureObject,I),
   holdsat(live(insecureObject),insecureObject,I), inst(insecureObject),
   holdsat(office(L),insecureObject,I),
   holdsat(holds(A,K),insecureObject,I),
   holdsat(key(K),insecureObject,I),
   agent(A),
   object(K),
   location(L),
   inst(insecureObject), instant(I).
%
% terminate rules
%
%
% Translation of leave(A,O) terminates ['holds', ['A', 'O']] if ['holds', ['A', 'O']]
%
terminated(holds(A,O),insecureObject,I) :-
   occurred(leave(A,O),insecureObject,I),
   holdsat(live(insecureObject),insecureObject,I),inst(insecureObject),
   holdsat(holds(A,O),insecureObject,I),
   agent(A),
   object(O),
   inst(insecureObject), instant(I).
%
% Translation of take(A,O) terminates ['olocation', ['O', 'L']] if ['and', ['alocation', ['A', 'L']], ['olocation', ['O', 'L']]]
%
terminated(olocation(O,L),insecureObject,I) :-
   occurred(take(A,O),insecureObject,I),
   holdsat(live(insecureObject),insecureObject,I),inst(insecureObject),
   holdsat(alocation(A,L),insecureObject,I),
   holdsat(olocation(O,L),insecureObject,I),
   agent(A),
   location(L),
   object(O),
   inst(insecureObject), instant(I).
%
% Translation of exit(A,L) terminates ['alocation', ['A', 'L']] if ['and', ['and', ['and', ['and', ['unlocked', ['L']], ['office', ['L']]], ['holds', ['A', 'K']]], ['key', ['K']]], ['alocation', ['A', 'L']]]
%
terminated(alocation(A,L),insecureObject,I) :-
   occurred(exit(A,L),insecureObject,I),
   holdsat(live(insecureObject),insecureObject,I),inst(insecureObject),
   holdsat(unlocked(L),insecureObject,I),
   holdsat(office(L),insecureObject,I),
   holdsat(holds(A,K),insecureObject,I),
   holdsat(key(K),insecureObject,I),
   holdsat(alocation(A,L),insecureObject,I),
   agent(A),
   object(K),
   location(L),
   inst(insecureObject), instant(I).
%
% Translation of exit(A,L) terminates ['unlocked', ['L']] if ['and', ['and', ['and', ['and', ['unlocked', ['L']], ['office', ['L']]], ['holds', ['A', 'K']]], ['key', ['K']]], ['alocation', ['A', 'L']]]
%
terminated(unlocked(L),insecureObject,I) :-
   occurred(exit(A,L),insecureObject,I),
   holdsat(live(insecureObject),insecureObject,I),inst(insecureObject),
   holdsat(unlocked(L),insecureObject,I),
   holdsat(office(L),insecureObject,I),
   holdsat(holds(A,K),insecureObject,I),
   holdsat(key(K),insecureObject,I),
   holdsat(alocation(A,L),insecureObject,I),
   agent(A),
   object(K),
   location(L),
   inst(insecureObject), instant(I).
%
% Translation of exit(A,L) terminates ['alocation', ['A', 'L']] if ['and', ['and', ['unlocked', ['L']], ['office', ['L']]], ['alocation', ['A', 'L']]]
%
terminated(alocation(A,L),insecureObject,I) :-
   occurred(exit(A,L),insecureObject,I),
   holdsat(live(insecureObject),insecureObject,I),inst(insecureObject),
   holdsat(unlocked(L),insecureObject,I),
   holdsat(office(L),insecureObject,I),
   holdsat(alocation(A,L),insecureObject,I),
   agent(A),
   location(L),
   inst(insecureObject), instant(I).
%
% Translation of enter(A,L1) terminates ['alocation', ['A', 'L2']] if ['and', ['and', ['unlocked', ['L1']], ['office', ['L1']]], ['hallway', ['L2']]]
%
terminated(alocation(A,L2),insecureObject,I) :-
   occurred(enter(A,L1),insecureObject,I),
   holdsat(live(insecureObject),insecureObject,I),inst(insecureObject),
   holdsat(unlocked(L1),insecureObject,I),
   holdsat(office(L1),insecureObject,I),
   holdsat(hallway(L2),insecureObject,I),
   agent(A),
   location(L2),
   location(L1),
   inst(insecureObject), instant(I).
%
% Translation of enter(A,L1) terminates ['locked', ['L1']] if ['and', ['and', ['and', ['and', ['locked', ['L1']], ['office', ['L1']]], ['holds', ['A', 'K']]], ['key', ['K']]], ['hallway', ['L2']]]
%
terminated(locked(L1),insecureObject,I) :-
   occurred(enter(A,L1),insecureObject,I),
   holdsat(live(insecureObject),insecureObject,I),inst(insecureObject),
   holdsat(locked(L1),insecureObject,I),
   holdsat(office(L1),insecureObject,I),
   holdsat(holds(A,K),insecureObject,I),
   holdsat(key(K),insecureObject,I),
   holdsat(hallway(L2),insecureObject,I),
   agent(A),
   object(K),
   location(L2),
   location(L1),
   inst(insecureObject), instant(I).
%
% Translation of enter(A,L1) terminates ['alocation', ['A', 'L2']] if ['and', ['and', ['and', ['and', ['locked', ['L1']], ['office', ['L1']]], ['holds', ['A', 'K']]], ['key', ['K']]], ['hallway', ['L2']]]
%
terminated(alocation(A,L2),insecureObject,I) :-
   occurred(enter(A,L1),insecureObject,I),
   holdsat(live(insecureObject),insecureObject,I),inst(insecureObject),
   holdsat(locked(L1),insecureObject,I),
   holdsat(office(L1),insecureObject,I),
   holdsat(holds(A,K),insecureObject,I),
   holdsat(key(K),insecureObject,I),
   holdsat(hallway(L2),insecureObject,I),
   agent(A),
   object(K),
   location(L2),
   location(L1),
   inst(insecureObject), instant(I).
%
% noninertial rules
%
%
% Translation of situation4(O) when ['and', ['and', ['olocation', ['O', 'L']], ['key', ['O']]], ['hallway', ['L']]]
holdsat(situation4(O),insecureObject,I) :-
   holdsat(olocation(O,L),insecureObject,I),
   holdsat(key(O),insecureObject,I),
   holdsat(hallway(L),insecureObject,I),
   location(L),
   object(O),
   inst(insecureObject), instant(I).
%
% Translation of situation1(O) when ['and', ['and', ['and', ['olocation', ['O', 'L']], ['not', ['employeeIn', ['L']]]], ['alocation', ['A', 'L']]], ['unlocked', ['L']]]
holdsat(situation1(O),insecureObject,I) :-
   holdsat(olocation(O,L),insecureObject,I),
   not
   holdsat(employeeIn(L),insecureObject,I),
   holdsat(alocation(A,L),insecureObject,I),
   holdsat(unlocked(L),insecureObject,I),
   agent(A),
   location(L),
   object(O),
   inst(insecureObject), instant(I).
%
% Translation of situation2(O) when ['and', ['and', ['and', ['olocation', ['O', 'L']], ['not', ['employeeIn', ['L']]]], ['alocation', ['A', 'L']]], ['locked', ['L']]]
holdsat(situation2(O),insecureObject,I) :-
   holdsat(olocation(O,L),insecureObject,I),
   not
   holdsat(employeeIn(L),insecureObject,I),
   holdsat(alocation(A,L),insecureObject,I),
   holdsat(locked(L),insecureObject,I),
   agent(A),
   location(L),
   object(O),
   inst(insecureObject), instant(I).
%
% Translation of situation3(O) when ['and', ['and', ['olocation', ['O', 'L']], ['laptop', ['O']]], ['hallway', ['L']]]
holdsat(situation3(O),insecureObject,I) :-
   holdsat(olocation(O,L),insecureObject,I),
   holdsat(laptop(O),insecureObject,I),
   holdsat(hallway(L),insecureObject,I),
   location(L),
   object(O),
   inst(insecureObject), instant(I).
%
% Translation of vulnerable(O) when ['situation4', ['O']]
holdsat(vulnerable(O),insecureObject,I) :-
   holdsat(situation4(O),insecureObject,I),
   object(O),
   inst(insecureObject), instant(I).
%
% Translation of vulnerable(O) when ['situation3', ['O']]
holdsat(vulnerable(O),insecureObject,I) :-
   holdsat(situation3(O),insecureObject,I),
   object(O),
   inst(insecureObject), instant(I).
%
% Translation of vulnerable(O) when ['situation2', ['O']]
holdsat(vulnerable(O),insecureObject,I) :-
   holdsat(situation2(O),insecureObject,I),
   object(O),
   inst(insecureObject), instant(I).
%
% Translation of vulnerable(O) when ['situation1', ['O']]
holdsat(vulnerable(O),insecureObject,I) :-
   holdsat(situation1(O),insecureObject,I),
   object(O),
   inst(insecureObject), instant(I).
%
% Translation of employeeIn(L) when ['and', ['alocation', ['A', 'L']], ['employee', ['A']]]
holdsat(employeeIn(L),insecureObject,I) :-
   holdsat(alocation(A,L),insecureObject,I),
   holdsat(employee(A),insecureObject,I),
   agent(A),
   location(L),
   inst(insecureObject), instant(I).
%
% Translation of failed(O) when ['and', ['and', ['holds', ['A', 'O']], ['attacker', ['A']]], ['laptop', ['O']]]
holdsat(failed(O),insecureObject,I) :-
   holdsat(holds(A,O),insecureObject,I),
   holdsat(attacker(A),insecureObject,I),
   holdsat(laptop(O),insecureObject,I),
   agent(A),
   object(O),
   inst(insecureObject), instant(I).
%
% -------------------------------PART 3-------------------------------
%
%
% initially
%
% no creation event
holdsat(live(insecureObject),insecureObject,I) :- start(I), inst(insecureObject).
holdsat(perm(null),insecureObject,I) :- start(I), inst(insecureObject).
% initially: olocation(laptop1,office1)
holdsat(olocation(laptop1,office1),insecureObject,I) :-
   inst(insecureObject), start(I).
% initially: alocation(agent1,office1)
holdsat(alocation(agent1,office1),insecureObject,I) :-
   inst(insecureObject), start(I).
% initially: alocation(agent2,hallway1)
holdsat(alocation(agent2,hallway1),insecureObject,I) :-
   inst(insecureObject), start(I).
% initially: hallway(hallway1)
holdsat(hallway(hallway1),insecureObject,I) :-
   inst(insecureObject), start(I).
% initially: office(office1)
holdsat(office(office1),insecureObject,I) :-
   inst(insecureObject), start(I).
% initially: unlocked(office1)
holdsat(unlocked(office1),insecureObject,I) :-
   inst(insecureObject), start(I).
% initially: holds(agent1,key1)
holdsat(holds(agent1,key1),insecureObject,I) :-
   inst(insecureObject), start(I).
% initially: key(key1)
holdsat(key(key1),insecureObject,I) :-
   inst(insecureObject), start(I).
% initially: laptop(laptop1)
holdsat(laptop(laptop1),insecureObject,I) :-
   inst(insecureObject), start(I).
% initially: employee(agent1)
holdsat(employee(agent1),insecureObject,I) :-
   inst(insecureObject), start(I).
% initially: attacker(agent2)
holdsat(attacker(agent2),insecureObject,I) :-
   inst(insecureObject), start(I).
% initially: perm(take(X,Y))
holdsat(perm(take(X,Y)),insecureObject,I) :-
   object(Y),
   agent(X),
   inst(insecureObject), start(I).
% initially: perm(leave(X,Y))
holdsat(perm(leave(X,Y)),insecureObject,I) :-
   object(Y),
   agent(X),
   inst(insecureObject), start(I).
% initially: perm(exit(X,Y))
holdsat(perm(exit(X,Y)),insecureObject,I) :-
   location(Y),
   agent(X),
   inst(insecureObject), start(I).
% initially: perm(enter(X,Y))
holdsat(perm(enter(X,Y)),insecureObject,I) :-
   location(Y),
   agent(X),
   inst(insecureObject), start(I).
%
% -------------------------------GROUNDING-------------------------------
%
% Object
#program object(l).
object(l).

% Location
#program location(l).
location(l).

% Agent
#program agent(l).
agent(l).

%
% End of file
%
