package eu.eventstorm.cqrs.ex001.gen.impl;

import eu.eventstorm.cqrs.ex001.command.CreateUserCommand;

public final class CreateUserCommandImpl implements CreateUserCommand {

    private String name;
    private int age;
    private String email;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
