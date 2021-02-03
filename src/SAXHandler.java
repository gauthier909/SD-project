import java.util.HashSet;
import java.util.Set;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXHandler extends DefaultHandler {
  private String idActor;
  private String nameActor;
  private String actorsMovie;
  private String yearMovie;
  private String nameMovie;
  private String[] tabActors;
  private boolean bNameMovie;
  private Graph g = new Graph();

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    // TODO Auto-generated method stub
    super.characters(ch, start, length);

    if (bNameMovie) {
      nameMovie = new String(ch, start, length);
      bNameMovie = false;
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    // TODO Auto-generated method stub
    super.endElement(uri, localName, qName);
    if (qName.equalsIgnoreCase("actor")) {
      Actor a = new Actor(idActor, nameActor);

      // Ajout dans map Actor -> Movie
      if (!g.getActorMovieList().containsKey(idActor)) {
        g.getActorMovieList().put(idActor, new HashSet<Movie>());
      }

      // Ajout dans map id -> Actor
      if (!g.getActorList().containsKey(idActor)) {
        g.getActorList().put(idActor, a);
      }
    } else if (qName.equalsIgnoreCase("movie")) {
      // Split des actors
      tabActors = actorsMovie.split(" ");

      // Ajout de chaque actors dans un set
      Set<Actor> setActors = new HashSet<Actor>();
      for (int i = 0; i < tabActors.length; i++) {
        Actor a = g.getActorList().get(tabActors[i]);
        setActors.add(a);
      }

      // Création d'un film
      Movie m = new Movie(yearMovie, nameMovie);

      // Ajout d'un film dans map Actor -> Movie
      for (int i = 0; i < tabActors.length; i++) {
        g.getActorMovieList().get(tabActors[i]).add(m);
      }

      // Ajout d'un film dans map Movie -> Actor
      if (!g.getMovieActorList().containsKey(m)) {
        g.getMovieActorList().put(m, setActors);
      }
    }
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    // TODO Auto-generated method stub
    super.startElement(uri, localName, qName, attributes);

    if (qName.equalsIgnoreCase("actor")) {
      idActor = attributes.getValue("id");
      nameActor = attributes.getValue("name");
    } else if (qName.equalsIgnoreCase("movie")) {
      actorsMovie = attributes.getValue("actors");
      yearMovie = attributes.getValue("year");
      bNameMovie = true;
    }
  }

  public Graph getGraph() {
    // TODO Auto-generated method stub
    return g;
  }
}