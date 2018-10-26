package compsci290.edu.duke.tutor.loginregistration;


import android.content.Intent;

public enum LoginType {
    TUTOR, TUTEE;
    private static final String name = LoginType.class.getName();
    public void attachTo(Intent intent) {
        intent.putExtra(name, ordinal());
    }
    public static LoginType detachFrom(Intent intent) {
        if(!intent.hasExtra(name)) throw new IllegalStateException();
        return values()[intent.getIntExtra(name, -1)];
    }
}
