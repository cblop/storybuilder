%
% -------------------------------PART 1-------------------------------
%
%
% Standard prelude for basic
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
% and for coordinated institutions
#external observed(E) : event(E).
compObserved(E,J) :- observed(E), start(J).

%
% Rules for Institution basic
%
  ifluent(live(basic), basic).
  fluent(live(basic), basic).
  inst(basic).
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
% Event: ex_red (type: ex)
  event(ex_red(Alpha0)) :- alpha(Alpha0).
  evtype(ex_red(Alpha0),basic,ex) :- alpha(Alpha0).
  evinst(ex_red(Alpha0),basic) :- alpha(Alpha0).
  ifluent(perm(ex_red(Alpha0)), basic) :- alpha(Alpha0).
  fluent(perm(ex_red(Alpha0)), basic) :- alpha(Alpha0).
  event(viol(ex_red(Alpha0))) :- alpha(Alpha0).
  evtype(viol(ex_red(Alpha0)), basic, viol) :- alpha(Alpha0).
  evinst(viol(ex_red(Alpha0)),basic) :- alpha(Alpha0).
% Event: ex_green (type: ex)
  event(ex_green(Alpha0)) :- alpha(Alpha0).
  evtype(ex_green(Alpha0),basic,ex) :- alpha(Alpha0).
  evinst(ex_green(Alpha0),basic) :- alpha(Alpha0).
  nifluent(perm(ex_green(Alpha0)), basic) :- alpha(Alpha0).
  fluent(perm(ex_green(Alpha0)), basic) :- alpha(Alpha0).
  event(viol(ex_green(Alpha0))) :- alpha(Alpha0).
  evtype(viol(ex_green(Alpha0)), basic, viol) :- alpha(Alpha0).
  evinst(viol(ex_green(Alpha0)),basic) :- alpha(Alpha0).
% Event: ex_blue (type: ex)
  event(ex_blue(Alpha0)) :- alpha(Alpha0).
  evtype(ex_blue(Alpha0),basic,ex) :- alpha(Alpha0).
  evinst(ex_blue(Alpha0),basic) :- alpha(Alpha0).
  ifluent(perm(ex_blue(Alpha0)), basic) :- alpha(Alpha0).
  fluent(perm(ex_blue(Alpha0)), basic) :- alpha(Alpha0).
  event(viol(ex_blue(Alpha0))) :- alpha(Alpha0).
  evtype(viol(ex_blue(Alpha0)), basic, viol) :- alpha(Alpha0).
  evinst(viol(ex_blue(Alpha0)),basic) :- alpha(Alpha0).
%
% null event for unknown events
% Event: null (type: ex)
  event(null).
  evtype(null,basic,ex).
  evinst(null,basic).
  ifluent(perm(null), basic).
  fluent(perm(null), basic).
  event(viol(null)).
  evtype(viol(null),basic,viol).
  evinst(viol(null),basic).
% Institutional events
% Event: in_green (type: in)
  event(in_green(Alpha0)) :- alpha(Alpha0).
  evtype(in_green(Alpha0),basic,inst) :- alpha(Alpha0).
  evinst(in_green(Alpha0),basic) :- alpha(Alpha0).
  ifluent(pow(basic,in_green(Alpha0)),basic) :- alpha(Alpha0).
  ifluent(perm(in_green(Alpha0)),basic) :- alpha(Alpha0).
  fluent(pow(basic,in_green(Alpha0)),basic) :- alpha(Alpha0).
  fluent(perm(in_green(Alpha0)),basic) :- alpha(Alpha0).
  event(viol(in_green(Alpha0))) :- alpha(Alpha0).
  evtype(viol(in_green(Alpha0)),basic,viol) :- alpha(Alpha0).
  evinst(viol(in_green(Alpha0)),basic) :- alpha(Alpha0).
% Event: in_blue (type: in)
  event(in_blue(Alpha0)) :- alpha(Alpha0).
  evtype(in_blue(Alpha0),basic,inst) :- alpha(Alpha0).
  evinst(in_blue(Alpha0),basic) :- alpha(Alpha0).
  ifluent(pow(basic,in_blue(Alpha0)),basic) :- alpha(Alpha0).
  ifluent(perm(in_blue(Alpha0)),basic) :- alpha(Alpha0).
  fluent(pow(basic,in_blue(Alpha0)),basic) :- alpha(Alpha0).
  fluent(perm(in_blue(Alpha0)),basic) :- alpha(Alpha0).
  event(viol(in_blue(Alpha0))) :- alpha(Alpha0).
  evtype(viol(in_blue(Alpha0)),basic,viol) :- alpha(Alpha0).
  evinst(viol(in_blue(Alpha0)),basic) :- alpha(Alpha0).
% Event: in_red (type: in)
  event(in_red(Alpha0)) :- alpha(Alpha0).
  evtype(in_red(Alpha0),basic,inst) :- alpha(Alpha0).
  evinst(in_red(Alpha0),basic) :- alpha(Alpha0).
  ifluent(pow(basic,in_red(Alpha0)),basic) :- alpha(Alpha0).
  ifluent(perm(in_red(Alpha0)),basic) :- alpha(Alpha0).
  fluent(pow(basic,in_red(Alpha0)),basic) :- alpha(Alpha0).
  fluent(perm(in_red(Alpha0)),basic) :- alpha(Alpha0).
  event(viol(in_red(Alpha0))) :- alpha(Alpha0).
  evtype(viol(in_red(Alpha0)),basic,viol) :- alpha(Alpha0).
  evinst(viol(in_red(Alpha0)),basic) :- alpha(Alpha0).
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
ifluent(in_fact_b(Alpha0),basic) :-
  alpha(Alpha0).
fluent(in_fact_b(Alpha0),basic) :-
  alpha(Alpha0).

ifluent(in_fact_a(Alpha0),basic) :-
  alpha(Alpha0).
fluent(in_fact_a(Alpha0),basic) :-
  alpha(Alpha0).

ifluent(in_fact(Alpha0),basic) :-
  alpha(Alpha0).
fluent(in_fact(Alpha0),basic) :-
  alpha(Alpha0).

%
% noninertial fluents
%
nifluent(ni_fact(Alpha0), basic) :-
  alpha(Alpha0).
fluent(ni_fact(Alpha0), basic) :-
  alpha(Alpha0).

%
% violation fluents (to be implemented)

%
% obligation fluents
%
oblfluent(obl(in_fact_a(Alpha0),in_fact_b(Alpha1),ex_green(Alpha2)), basic) :-
   fluent(in_fact_a(Alpha0),basic),
   fluent(in_fact_b(Alpha1),basic),
   event(ex_green(Alpha2)), alpha(Alpha0),alpha(Alpha1),alpha(Alpha2),inst(basic).
ifluent(obl(in_fact_a(Alpha0),in_fact_b(Alpha1),ex_green(Alpha2)), basic) :-
   fluent(in_fact_a(Alpha0),basic),
   fluent(in_fact_b(Alpha1),basic),
   event(ex_green(Alpha2)), alpha(Alpha0),alpha(Alpha1),alpha(Alpha2),inst(basic).
fluent(obl(in_fact_a(Alpha0),in_fact_b(Alpha1),ex_green(Alpha2)), basic) :-
   fluent(in_fact_a(Alpha0),basic),
   fluent(in_fact_b(Alpha1),basic),
   event(ex_green(Alpha2)), alpha(Alpha0),alpha(Alpha1),alpha(Alpha2),inst(basic).
terminated(obl(in_fact_a(Alpha0),in_fact_b(Alpha1),ex_green(Alpha2)),basic,I) :-
   fluent(in_fact_a(Alpha0),basic), holdsat(in_fact_a(Alpha0),basic,I),
   fluent(in_fact_b(Alpha1),basic),
   holdsat(obl(in_fact_a(Alpha0),in_fact_b(Alpha1),ex_green(Alpha2)),basic,I),
   event(ex_green(Alpha2)), alpha(Alpha0),alpha(Alpha1),alpha(Alpha2),inst(basic).
terminated(obl(in_fact_a(Alpha0),in_fact_b(Alpha1),ex_green(Alpha2)),basic,I) :-
   fluent(in_fact_a(Alpha0),basic),
   fluent(in_fact_b(Alpha1),basic),  holdsat(in_fact_b(Alpha1),basic,I),
   holdsat(obl(in_fact_a(Alpha0),in_fact_b(Alpha1),ex_green(Alpha2)),basic,I),
   event(ex_green(Alpha2)), alpha(Alpha0),alpha(Alpha1),alpha(Alpha2),inst(basic).
occurred(ex_green(Alpha2),basic,I) :-
   fluent(in_fact_a(Alpha0),basic), not holdsat(in_fact_a(Alpha0), basic, I),
   fluent(in_fact_b(Alpha1),basic),  holdsat(in_fact_b(Alpha1),basic,I),
   holdsat(obl(in_fact_a(Alpha0),in_fact_b(Alpha1),ex_green(Alpha2)),basic,I),
   event(ex_green(Alpha2)), alpha(Alpha0),alpha(Alpha1),alpha(Alpha2),inst(basic).
oblfluent(obl(ex_red(Alpha0),ex_blue(Alpha1),ex_green(Alpha2)), basic) :-
   event(ex_red(Alpha0)),
   event(ex_blue(Alpha1)),
   event(ex_green(Alpha2)), alpha(Alpha0),alpha(Alpha1),alpha(Alpha2),inst(basic).
ifluent(obl(ex_red(Alpha0),ex_blue(Alpha1),ex_green(Alpha2)), basic) :-
   event(ex_red(Alpha0)),
   event(ex_blue(Alpha1)),
   event(ex_green(Alpha2)), alpha(Alpha0),alpha(Alpha1),alpha(Alpha2),inst(basic).
fluent(obl(ex_red(Alpha0),ex_blue(Alpha1),ex_green(Alpha2)), basic) :-
   event(ex_red(Alpha0)),
   event(ex_blue(Alpha1)),
   event(ex_green(Alpha2)), alpha(Alpha0),alpha(Alpha1),alpha(Alpha2),inst(basic).
terminated(obl(ex_red(Alpha0),ex_blue(Alpha1),ex_green(Alpha2)),basic,I) :-
   event(ex_red(Alpha0)), occurred(ex_red(Alpha0),basic,I),
   event(ex_blue(Alpha1)),
   holdsat(obl(ex_red(Alpha0),ex_blue(Alpha1),ex_green(Alpha2)),basic,I),
   event(ex_green(Alpha2)), alpha(Alpha0),alpha(Alpha1),alpha(Alpha2),inst(basic).
terminated(obl(ex_red(Alpha0),ex_blue(Alpha1),ex_green(Alpha2)),basic,I) :-
   event(ex_red(Alpha0)), 
   event(ex_blue(Alpha1)), occurred(ex_blue(Alpha1),basic,I),
   holdsat(obl(ex_red(Alpha0),ex_blue(Alpha1),ex_green(Alpha2)),basic,I),
   event(ex_green(Alpha2)), alpha(Alpha0),alpha(Alpha1),alpha(Alpha2),inst(basic).
occurred(ex_green(Alpha2),basic,I) :-
   event(ex_red(Alpha0)), 
   event(ex_blue(Alpha1)), occurred(ex_blue(Alpha1),basic,I),
   holdsat(obl(ex_red(Alpha0),ex_blue(Alpha1),ex_green(Alpha2)),basic,I),
   event(ex_green(Alpha2)), alpha(Alpha0),alpha(Alpha1),alpha(Alpha2),inst(basic).
%
% -------------------------------PART 2-------------------------------
%
%
% generate rules
%
%
% Translation of ex_blue(A) generates in_blue(A) if [] in 
occurred(in_blue(A),basic,I) :- occurred(ex_blue(A),basic,I),
   holdsat(pow(basic,in_blue(A)),basic,I),
   alpha(A),
   inst(basic), instant(I).
%
% Translation of ex_red(A) generates in_red(A) if [] in 
occurred(in_red(A),basic,I) :- occurred(ex_red(A),basic,I),
   holdsat(pow(basic,in_red(A)),basic,I),
   alpha(A),
   inst(basic), instant(I).
%
% initiate rules
%
%
% Translation of in_blue(A) initiates ['perm', ['in_green', ['A']]] if []
%
initiated(perm(in_green(A)),basic,I) :-
   occurred(in_blue(A),basic,I),
   holdsat(live(basic),basic,I), inst(basic),
   alpha(A),
   inst(basic), instant(I).
%
% Translation of in_blue(A) initiates ['pow', ['in_green', ['A']]] if []
%
initiated(pow(basic,in_green(A)),basic,I) :-
   occurred(in_blue(A),basic,I),
   holdsat(live(basic),basic,I), inst(basic),
   alpha(A),
   inst(basic), instant(I).
%
% Translation of in_red(A) initiates ['in_fact', ['A']] if []
%
initiated(in_fact(A),basic,I) :-
   occurred(in_red(A),basic,I),
   holdsat(live(basic),basic,I), inst(basic),
   alpha(A),
   inst(basic), instant(I).
%
% Translation of in_red(A) initiates ['perm', ['in_blue', ['A']]] if []
%
initiated(perm(in_blue(A)),basic,I) :-
   occurred(in_red(A),basic,I),
   holdsat(live(basic),basic,I), inst(basic),
   alpha(A),
   inst(basic), instant(I).
%
% Translation of in_red(A) initiates ['pow', ['in_blue', ['A']]] if []
%
initiated(pow(basic,in_blue(A)),basic,I) :-
   occurred(in_red(A),basic,I),
   holdsat(live(basic),basic,I), inst(basic),
   alpha(A),
   inst(basic), instant(I).
%
% Translation of in_red(A) initiates ['obl', [['ex_red', ['A']], ['ex_blue', ['A']], ['ex_green', ['A']]]] if []
%
initiated(obl(ex_red(A),ex_blue(A),ex_green(A)),basic,I) :-
   occurred(in_red(A),basic,I),
   holdsat(live(basic),basic,I), inst(basic),
   alpha(A),
   inst(basic), instant(I).
%
% terminate rules
%
%
% Translation of in_blue(A) terminates ['in_fact', ['A']] if []
%
terminated(in_fact(A),basic,I) :-
   occurred(in_blue(A),basic,I),
   holdsat(live(basic),basic,I),inst(basic),
   alpha(A),
   inst(basic), instant(I).
%
% noninertial rules
%
%
% Translation of perm(ex_green(Alpha)) when ['in_fact', ['Alpha']]
holdsat(perm(ex_green(Alpha)),basic,I) :-
   holdsat(in_fact(Alpha),basic,I),
   alpha(Alpha),
   inst(basic), instant(I).
%
% Translation of ni_fact(Alpha) when ['in_fact', ['Alpha']]
holdsat(ni_fact(Alpha),basic,I) :-
   holdsat(in_fact(Alpha),basic,I),
   alpha(Alpha),
   inst(basic), instant(I).
%
% -------------------------------PART 3-------------------------------
%
%
% initially
%
% no creation event
holdsat(live(basic),basic,I) :- start(I), inst(basic).
holdsat(perm(null),basic,I) :- start(I), inst(basic).
%
% -------------------------------GROUNDING-------------------------------
%
% Alpha
#program alpha(l).
alpha(l).

%
% End of file
%
