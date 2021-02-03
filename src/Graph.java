import java.io.File;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Graph {
	private Map<String, Actor> actorList = new HashMap<String, Actor>();
	private Map<String, Set<Movie>> actorMovieList = new HashMap<String, Set<Movie>>();
	private Map<Movie, Set<Actor>> movieActorList = new HashMap<Movie, Set<Actor>>();

	public void calculerCheminLePlusCourt(String string2, String string, String string3) {
		// Récupération des acteurs
		Actor a1 = this.findActorByName(string);
		Actor a2 = this.findActorByName(string2);

		Map<Actor, Actor> actorPrecedent = new HashMap<Actor, Actor>();
		Map<Actor, Movie> moviePrecedent = new HashMap<Actor, Movie>();

		this.algoBFS(a1, a2, actorPrecedent, moviePrecedent);

		this.extractDOM(a1, a2, actorPrecedent, moviePrecedent, string3);

	}

	private void afficherPrecedents(Actor a1, Actor a2, Map<Actor, Actor> actorPrecedent,
			Map<Actor, Movie> moviePrecedent) {
		System.out.println(a2.getName());
		Actor temp = a2;
		while (!temp.equals(a1)) {
			System.out.println("\nA joué dans : " + moviePrecedent.get(temp).getName());
			System.out.println("\nAvec : " + actorPrecedent.get(temp).getName());
			temp = actorPrecedent.get(temp);
		}
	}

	private void algoBFS(Actor a1, Actor a2, Map<Actor, Actor> actorPrecedent, Map<Actor, Movie> moviePrecedent) {

		// Collections
		Set<Actor> visites = new HashSet<Actor>();
		ArrayDeque<Actor> file = new ArrayDeque<Actor>();

		// Ajout du premier sommet
		file.add(a1);
		visites.add(a1);

		// Tant que la file n'est pas vide
		while (!file.isEmpty()) {
			// Prendre le premier de la file
			Actor current = file.poll();
			// Parcours des films de l'acteur courant
			for (Movie m : this.actorMovieList.get(current.getId())) {
				int sizeActorsM = this.movieActorList.get(m).size();
				// Parcours des acteurs du film courant
				for (Actor a : this.movieActorList.get(m)) {
					a.setCout(sizeActorsM);
					// Si acteur == acteur2
					if (a.equals(a2)) {
						actorPrecedent.put(a, current);
						moviePrecedent.put(a, m);
						return;
					}
					// Si les acteurs visités ne contiennent pas acteur
					if (!visites.contains(a)) {
						file.add(a);
						visites.add(a);
						actorPrecedent.put(a, current);
						moviePrecedent.put(a, m);
					}
				}
			}
		}
	}

	private Actor findActorByName(String name) {
		for (Actor a : actorList.values()) {
			if (a.getName().equals(name)) {
				return a;
			}
		}
		return null;
	}

	public void calculerCheminCoutMinimum(String string2, String string, String string3) {
		// Récupération des acteurs
		Actor a1 = this.findActorByName(string);
		Actor a2 = this.findActorByName(string2);

		Map<Actor, Actor> actorPrecedent = new HashMap<Actor, Actor>();
		Map<Actor, Movie> moviePrecedent = new HashMap<Actor, Movie>();

		this.algoDijkstra(a1, a2, actorPrecedent, moviePrecedent);
		this.afficherPrecedents(a1, a2, actorPrecedent, moviePrecedent);
		this.extractDOM(a1, a2, actorPrecedent, moviePrecedent, string3);

	}

	private void algoDijkstra(Actor a1, Actor a2, Map<Actor, Actor> actorPrecedent, Map<Actor, Movie> moviePrecedent) {
		SortedSet<Actor> etiquettesProvisoires = new TreeSet<Actor>();
		Set<Actor> etiquettesDefinitives = new HashSet<Actor>();

		a1.setCout(0);
		etiquettesProvisoires.add(a1);

		while (!etiquettesDefinitives.contains(a2)) {
			Actor current = etiquettesProvisoires.first();
			etiquettesProvisoires.remove(current);
			etiquettesDefinitives.add(current);

			for (Movie m : actorMovieList.get(current.getId())) {
				int sizeActorsM = this.movieActorList.get(m).size();
				for (Actor a : this.movieActorList.get(m)) {
					int sizeModif = current.getCout() + sizeActorsM;
					// Si definitives ne contient pas a
					if (!etiquettesDefinitives.contains(a)) {
						// Si provisoires contient a
						if (etiquettesProvisoires.contains(a)) {
							// Si size modif plus petit que courant
							if (sizeModif < a.getCout()) {
								etiquettesProvisoires.remove(a);
								a.setCout(sizeModif);
								etiquettesProvisoires.add(a);
								actorPrecedent.put(a, current);
								moviePrecedent.put(a, m);
							}
						} else {
							a.setCout(sizeModif);
							etiquettesProvisoires.add(a);
							actorPrecedent.put(a, current);
							moviePrecedent.put(a, m);
						}
					}
				}
			}
		}
	}

	private void extractDOM(Actor a1, Actor a2, Map<Actor, Actor> actorPrecedent, Map<Actor, Movie> moviePrecedent,
			String nomFichier) {
		int coutCount = 0;
		int nbMovieCount = 0;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			Element rootElement = doc.createElement("path");
			doc.appendChild(rootElement);

			Element actor = doc.createElement("actor");
			actor.appendChild(doc.createTextNode(a2.getName()));
			rootElement.appendChild(actor);

			Actor temp = a2;

			while (!temp.equals(a1)) {
				// ADD MOVIE

				Element movie = doc.createElement("movie");
				rootElement.appendChild(movie);

				Attr nameMovie = doc.createAttribute("name");
				nameMovie.setValue(moviePrecedent.get(temp).getName());

				Attr yearMovie = doc.createAttribute("year");
				yearMovie.setValue(moviePrecedent.get(temp).getYear());

				movie.setAttributeNode(nameMovie);
				movie.setAttributeNode(yearMovie);

				nbMovieCount++;
				coutCount += this.movieActorList.get(moviePrecedent.get(temp)).size();

				// ADD ACTOR
				Element actor2 = doc.createElement("actor");
				actor2.appendChild(doc.createTextNode(actorPrecedent.get(temp).getName()));
				rootElement.appendChild(actor2);

				temp = actorPrecedent.get(temp);
			}

			Attr cout = doc.createAttribute("cout");
			cout.setValue("" + coutCount);

			Attr nbMovie = doc.createAttribute("nbMovie");
			nbMovie.setValue("" + nbMovieCount);

			rootElement.setAttributeNode(cout);
			rootElement.setAttributeNode(nbMovie);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(nomFichier));
			transformer.transform(source, result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Map<String, Set<Movie>> getActorMovieList() {
		return actorMovieList;
	}

	public Map<Movie, Set<Actor>> getMovieActorList() {
		return movieActorList;
	}

	public Map<String, Actor> getActorList() {
		return actorList;
	}

}