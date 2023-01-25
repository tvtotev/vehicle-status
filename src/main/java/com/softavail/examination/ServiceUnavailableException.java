package com.softavail.examination;

public class ServiceUnavailableException extends Exception {

    /**
     * Vehicle Status External service unavailability exception
     */
    private static final long serialVersionUID = 663673918712656051L;

    public ServiceUnavailableException(RuntimeException e) {
        super(e);
    }

}
