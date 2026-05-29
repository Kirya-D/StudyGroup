package kirya.viewmodel;

class LoggedInAccount {
    private static String username = null;

    /**
     * {@return the username of the currently logged-in account}
     */
    public static String Username() {
        return LoggedInAccount.username;
    }

    /**
     * Sets the currently logged in {@code username}.
     * 
     * <p>
     * Postcondition: {@link LoggedInAccount#Username} == {@code username}
     * </p>
     * 
     * @param username the non-null username of the account to log in.
     * @throws IllegalArgumentException If {@code username} is null
     */
    public static final void LogInAs(String username) {
        if (username == null) {
            throw new IllegalArgumentException("username can not be null");
        }
        LoggedInAccount.username = username;
    }

    /**
     * Clears the logged in user
     * 
     * <p>
     * Postcondition: {@link LoggedInAccount#Username} == null
     * </p>
     */
    public static final void LogOut() {
        LoggedInAccount.username = null;
    }
}
