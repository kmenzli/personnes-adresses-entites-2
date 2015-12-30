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



public class InitDB {

	// constantes
	private final static String TABLE_PERSONNE = "jpa04_hb_personne";

	private final static String TABLE_ADRESSE = "jpa04_hb_adresse";

	public static void main(String[] args) throws ParseException {
		// Contexte de persistance
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa");
		EntityManager em = null;
		// on r�cup�re un EntityManager � partir de l'EntityManagerFactory pr�c�dent
		em = emf.createEntityManager();
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
		// cr�ation personnes
		Personne p1 = new Personne("Martin", "Paul", new SimpleDateFormat("dd/MM/yy").parse("31/01/2000"), true, 2);
		Personne p2 = new Personne("Durant", "Sylvie", new SimpleDateFormat("dd/MM/yy").parse("05/07/2001"), false, 0);
		// cr�ation adresses
		Adresse a1 = new Adresse("8 rue Boileau", null, null, "49000", "Angers", null, "France");
		Adresse a2 = new Adresse("Apt 100", "Les Mimosas", "15 av Foch", "49002", "Angers", "03", "France");
		Adresse a3 = new Adresse("x", "x", "x", "x", "x", "x", "x");
		Adresse a4 = new Adresse("y", "y", "y", "y", "y", "y", "y");
		// associations personne <--> adresse
		p1.setAdresse(a1);
		p2.setAdresse(a2);
		// persistance des personnes et par cascade de leurs adresses
		em.persist(p1);
		em.persist(p2);
		// et des adresses a3 et a4 non li�es � des personnes
		em.persist(a3);
		em.persist(a4);
		// affichage personnes
		System.out.println("[personnes]");
		for (Object p : em.createQuery("select p from Personne p order by p.nom asc").getResultList()) {
			System.out.println(p);
		}
		// affichage adresses
		System.out.println("[adresses]");
		for (Object a : em.createQuery("select a from Adresse a").getResultList()) {
			System.out.println(a);
		}

		// fin transaction
		tx.commit();
		// fin EntityManager
		em.close();
		// fin EntityManagerFactory
		emf.close();
		// log
		System.out.println("termin�...");

	}
}
