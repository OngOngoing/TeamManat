package util;

import models.User;

public abstract class Authenticator {
    public static Authenticator getInstance() {
        String authName = play.Play.application().configuration().getString("exceed.authenticator");
        Authenticator authenticator = null;
        try {
            authenticator = (Authenticator)Class.forName(authName).newInstance();
        } catch (Exception e) {
            return null;
        }
        return authenticator;

    }
    public abstract User authenticate(String username, String password);
}
