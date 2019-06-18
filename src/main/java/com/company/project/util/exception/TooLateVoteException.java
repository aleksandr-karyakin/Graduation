package com.company.project.util.exception;

import org.springframework.http.HttpStatus;

public class TooLateVoteException extends ApplicationException {

    public static final String VOTE_TOO_LATE = "exception.user.voteTooLate";

    public TooLateVoteException() {
        super(ErrorType.VOTE_FORBIDDEN, VOTE_TOO_LATE, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
