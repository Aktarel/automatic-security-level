package fr.nlebec.jira.plugins.customseclvl.exceptions;

public class CustomSecurityLevelException extends Exception {
    private static final long serialVersionUID = 1L;

    public CustomSecurityLevelException(final String msg, final Throwable t) {
        super(msg, t);
    }

    public CustomSecurityLevelException(final String msg) {
        super(msg);
    }

    public CustomSecurityLevelException(final Throwable t) {
        super(t);
    }
}
