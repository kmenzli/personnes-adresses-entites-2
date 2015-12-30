package org.academy.hibernate;

import org.academy.hibernate.entities.Adresse;
import org.academy.hibernate.entities.Personne;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;


@SuppressWarnings( { "unused", "unchecked" })
public class Main {

	// constantes
	private final static String TABLE_PERSONNE = "jpa04_hb_personne";

	private final static String TABLE_ADRESSE = "jpa04_hb_adresse";

	// Contexte de persistance
	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");

	private static EntityManager em = null;

	// objets partag�s
	private static Personne p1, p2, newp1;

	private static Adresse a1, a2, a3, a4, newa1, newa4;

	public static void main(String[] args) throws Exception {
		// nettoyage base
		log("clean");
		clean();

		// dump
		dumpPersonne();

		// test1
		log("test1");
		test1();

		// test2
		log("test2");
		test2();

		// test4
		log("test4");
		test4();

		// test5
		log("test5");
		test5();

		// test6
		log("test6");
		test6();

		// test7
		log("test7");
		test7();

		// test8
		log("test8");
		test8();

		// fin contexte de persistance
		if (em != null && em.isOpen())
			em.close();

		// fermeture EntityManagerFactory
		emf.close();
	}

	// r�cup�rer l'EntityManager courant
	private static EntityManager getEntityManager() {
		if (em == null || !em.isOpen()) {
			em = emf.createEntityManager();
		}
		return em;
	}

	// r�cup�rer un EntityManager neuf
	private static EntityManager getNewEntityManager() {
		if (em != null && em.isOpen()) {
			em.close();
		}
		em = emf.createEntityManager();
		return em;
	}

	// affichage contenu table Personne
	private static void dumpPersonne() {
		// contexte de persistance
		EntityManager em = getEntityManager();
		// d�but transaction
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		// affichage personnes
		System.out.println("[personnes]");
		for (Object p : em.createQuery("select p from Personne p order by p.nom asc").getResultList()) {
			System.out.println(p);
		}
		// fin transaction
		tx.commit();
	}

	// affichage contenu table Personne
	private static void dumpAdresse() {
		// contexte de persistance
		EntityManager em = getEntityManager();
		// d�but transaction
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		// affichage adresses
		System.out.println("[adresses]");
		for (Object a : em.createQuery("select a from Adresse a").getResultList()) {
			System.out.println(a);
		}
		// fin transaction
		tx.commit();
	}

	// raz BD
	private static void clean() {
		// contexte de persistance
		EntityManager em = getEntityManager();
		// d�but transaction
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		// requ�te
		Query sql1;
		// supprimer les �l�ments de la table PERSONNE
		sql1 = em.createNativeQuery("delete from " + TABLE_PERSONNE);
		sql1.executeUpdate();
		// supprimer les �l�ments de la table ADRESSE
		sql1 = em.createNativeQuery("delete from " + TABLE_ADRESSE);
		sql1.executeUpdate();
		// fin transaction
		tx.commit();
	}

	// logs
	private static void log(String message) {
		System.out.println("main : ----------- " + message);
	}

	// cr�ation d'objets
	public static void test1() throws ParseException {
		// contexte de persistance
		EntityManager em = getEntityManager();
		// cr�ation personnes
		p1 = new Personne("Martin", "Paul", new SimpleDateFormat("dd/MM/yy").parse("31/01/2000"), true, 2);
		p2 = new Personne("Durant", "Sylvie", new SimpleDateFormat("dd/MM/yy").parse("05/07/2001"), false, 0);
		// cr�ation adresses
		a1 = new Adresse("8 rue Boileau", null, null, "49000", "Angers", null, "France");
		a2 = new Adresse("Apt 100", "Les Mimosas", "15 av Foch", "49002", "Angers", "03", "France");
		a3 = new Adresse("x", "x", "x", "x", "x", "x", "x");
		a4 = new Adresse("y", "y", "y", "y", "y", "y", "y");
		// associations personne <--> adresse
		p1.setAdresse(a1);
		p2.setAdresse(a2);
		// d�but transaction
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		// persistance des personnes
		em.persist(p1);
		em.persist(p2);
		// et des adresses a3 et a4 non li�es � des personnes
		em.persist(a3);
		em.persist(a4);
		// fin transaction
		tx.commit();
		// on affiche les tables
		dumpPersonne();
		dumpAdresse();
	}

	// modifier un objet du contexte
	public static void test2() {
		// contexte de persistance
		EntityManager em = getEntityManager();
		// d�but transaction
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		// on incr�mente le nbre d'enfants de p1
		p1.setNbenfants(p1.getNbenfants() + 1);
		// on modifie son �tat marital
		p1.setMarie(false);
		// l'objet p1 est automatiquement sauvegard� (dirty checking)
		// lors de la prochaine synchronisation (commit ou select)
		// fin transaction
		tx.commit();
		// on affiche la nouvelle table
		dumpPersonne();
	}

	// supprimer un objet appartenant au contexte de persistance
	public static void test4() {
		// contexte de persistance
		EntityManager em = getEntityManager();
		// d�but transaction
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		// on supprime l'objet attach� p2
		em.remove(p2);
		// fin transaction
		tx.commit();
		// on affiche les nouvelles tables
		dumpPersonne();
		dumpAdresse();
	}

	// d�tacher, r�attacher et modifier
	public static void test5() {
		// nouveau contexte de persistance
		EntityManager em = getNewEntityManager();
		// d�but transaction
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		// on r�attache p1 au nouveau contexte
		p1 = em.find(Personne.class, p1.getId());
		System.out.println("2-------------------------------------------------------");
		// on change l'adresse de p1
		p1.getAdresse().setVille("Paris");
		// fin transaction
		tx.commit();
		// on affiche les nouvelles tables
		dumpPersonne();
		dumpAdresse();
	}

	// supprimer un objet Adresse
	public static void test6() {
		EntityTransaction tx = null;
		// nouveau contexte de persistance
		EntityManager em = getNewEntityManager();
		// d�but transaction
		tx = em.getTransaction();
		tx.begin();
		// on r�attache l'adresse a3 au nouveau contexte
		a3 = em.find(Adresse.class, a3.getId());
		System.out.println(a3);
		// on la supprime
		em.remove(a3);
		// fin transaction
		tx.commit();
		// dump table Adresse
		dumpAdresse();
	}

	// rollback
	public static void test7() {
		EntityTransaction tx = null;
		try {
			// nouveau contexte de persistance
			EntityManager em = getNewEntityManager();
			// d�but transaction
			tx = em.getTransaction();
			tx.begin();
			// on r�attache l'adresse a1 au nouveau contexte
			newa1 = em.find(Adresse.class, a1.getId());
			// on r�attache l'adresse a4 au nouveau contexte
			newa4 = em.find(Adresse.class, a4.getId());
			// on essaie de les supprimer - devrait lancer une exception car on ne peut supprimer une adresse li�e � une
			// personne, ce qui est le cas de newa1
			em.remove(newa4);
			em.remove(newa1);
			// fin transaction
			tx.commit();
		} catch (RuntimeException e1) {
			// on a eu un pb
			System.out.format("Erreur dans transaction [%s%n%s%n%s%n%s]%n", e1.getClass().getName(), e1.getMessage(), e1.getCause(), e1.getCause()
					.getCause());
			try {
				if (tx.isActive()) {
					System.out.println("Rollback en cours...");
					tx.rollback();
				}
			} catch (RuntimeException e2) {
				System.out.format("Erreur au rollback [%s]%n", e2.getMessage());
			}
			// on abandonne le contexte courant
			em.clear();
		}
		// dump - la table Adresse n'a pas du changer � cause du rollback
		dumpAdresse();
	}

	// relation inverse un-�-un
	// r�alis�e par une requ�te JPQL
	public static void test8() {
		EntityTransaction tx = null;
		// nouveau contexte de persistance
		EntityManager em = getNewEntityManager();
		// d�but transaction
		tx = em.getTransaction();
		tx.begin();
		// on r�attache l'adresse a1 au nouveau contexte
		newa1 = em.find(Adresse.class, a1.getId());
		// on r�cup�re la personne propri�taire de cette adresse
		Personne p1 = (Personne) em.createQuery("select p from Personne p join p.adresse a where a.id=:adresseId").setParameter("adresseId", newa1.getId())
				.getSingleResult();
		// on les affiche
		System.out.println("adresse=" + newa1);
		System.out.println("personne=" + p1);
		// fin transaction
		tx.commit();
	}

}