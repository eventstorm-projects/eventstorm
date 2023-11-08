package eu.eventstorm.sql.model.ex001;

public final class StudentAdapter {

    private final int id;
    private final int age;

    public StudentAdapter(int id, int age) {
        this.id = id;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

}
