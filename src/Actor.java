public class Actor implements Comparable<Actor> {


  private String id;
  private String name;
  private int cout;

  public Actor(String id, String name) {
    super();
    this.id = id;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getCout() {
    return cout;
  }

  public void setCout(int cout) {
    this.cout = cout;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Actor other = (Actor) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

  public int compareTo(Actor a2) {
    // TODO Auto-generated method stub
    if ((this.getCout() - a2.getCout()) == 0) {
      return this.getId().compareTo(a2.getId());
    }
    return this.getCout() - a2.getCout();
  }
}