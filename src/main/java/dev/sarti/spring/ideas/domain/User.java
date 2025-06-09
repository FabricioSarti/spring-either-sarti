package dev.sarti.spring.ideas.domain;

public class User {
    public final Long id;
    public final String name;
    public final String email;

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User withName(String newName) {
        return new User(this.id, newName, this.email);
    }

    public User withEmail(String newEmail) {
        return new User(this.id, this.name, newEmail);
    }

    @Override
    public String toString() {
        return "__ " + name + " (" + email + ")";
    }
}
