:- occurred(enter(A,L),I,I1), occurred(enter(A,L),I,I2), next(I1,I2).
:- occurred(exit(A,L),I,I1), occurred(exit(A,L),I,I2), next(I1,I2).
:- occurred(leave(A,O),I,I1), occurred(leave(A,O),I,I2), next(I1,I2).
:- occurred(take(A,O),I,I1), occurred(take(A,O),I,I2), next(I1,I2).

