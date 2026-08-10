package org.apache.cxf.spring.boot.jaxws.exception;
/** Exception thrown when a A P I Build error occurs.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public class APIBuildException extends RuntimeException {

    public APIBuildException() {
        super();
    }

    public APIBuildException(String message) {
        super(message);
    }

    public APIBuildException(String message, Throwable cause) {
        super(message, cause);
    }

    public APIBuildException(Throwable cause) {
        super(cause);
    }

    protected APIBuildException(String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
	
}
