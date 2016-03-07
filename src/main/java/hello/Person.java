package hello;

public class Person {
  private int id;
  private String lastName;
  private String firstName;
  private Integer age;

  public Person() {

  }

  public Person(int id, String firstName, String lastName, Integer age) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.age = age;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append(id).append(",")
        .append(firstName).append(",")
        .append(lastName).append(",")
        .append(age)
        .toString();
  }

}
