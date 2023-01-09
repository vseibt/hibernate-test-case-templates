package org.hibernate.bugs.hhh16003;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.hibernate.bugs.hhh16003.MasterDataImportStatus.SUCCESS;
import static org.hibernate.bugs.hhh16003.TransportMode.DOMESTIC;
import static org.hibernate.bugs.hhh16003.TransportMode.INTERNATIONAL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Hhh16003JPAUnitTest {

    private static final MasterDataMetaData AT_INTERNATIONAL_SYSTEM = new MasterDataMetaData(
            "SYSTEM",
            "AT",
            INTERNATIONAL,
            "EUR"
    );
    private static final MasterDataMetaData DE_DOMESTIC_PREMIUM = new MasterDataMetaData(
            "PREMIUM",
            "DE",
            DOMESTIC,
            "EUR"
    );

    private static final String JPQL =
            "select mdf.id from MasterDataFileEntity mdf " +
            "where mdf.dataImportStatus = 'SUCCESS' " +
            "  and mdf.metaData.country = :countryCode " +
            "  and mdf.metaData.transportMode = :transportMode " +
            "  and mdf.metaData.product = :product " +
            "  and mdf.importFinishedAt = " +
            "   (select max(mdf.importFinishedAt) from MasterDataFileEntity mdf " +
            "     where mdf.dataImportStatus = 'SUCCESS' " +
            "       and mdf.metaData.country = :countryCode " +
            "       and mdf.metaData.transportMode = :transportMode " +
            "       and mdf.metaData.product = :product)";

    private static final String JPQL_WORKAROUND =
            "select mdf.id from MasterDataFileEntity mdf " +
            "where mdf.dataImportStatus = 'SUCCESS' " +
            "  and mdf.metaData.country = :countryCode " +
            "  and mdf.metaData.transportMode = :transportMode " +
            "  and mdf.metaData.product = :product " +
            "  and mdf.importFinishedAt = " +
            "   (select max(mdf2.importFinishedAt) from MasterDataFileEntity mdf2 " +
            "     where mdf2.dataImportStatus = 'SUCCESS' " +
            "       and mdf2.metaData.country = :countryCode " +
            "       and mdf2.metaData.transportMode = :transportMode " +
            "       and mdf2.metaData.product = :product)";

    private EntityManagerFactory entityManagerFactory;

    private MasterDataFileEntity entity;

	@Before
	public void init() {
		entityManagerFactory = Persistence.createEntityManagerFactory( "templatePU" );

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        entity = new MasterDataFileEntity(PrimaryKey.newPrimaryKey(), DE_DOMESTIC_PREMIUM, LocalDateTime.now(), SUCCESS);
        entityManager.persist(entity);

        var entity2 = new MasterDataFileEntity(PrimaryKey.newPrimaryKey(), AT_INTERNATIONAL_SYSTEM, LocalDateTime.now(), SUCCESS);
        entityManager.persist(entity2);

        entityManager.getTransaction().commit();
        entityManager.close();
	}

	@After
	public void destroy() {
		entityManagerFactory.close();
	}

    @Test
    public void hhh16003ShouldGenerateProperSql() throws Exception {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        var result = createAndExecuteQuery(entityManager, JPQL);
        assertEquals(entity.getId(), result);

        entityManager.getTransaction().commit();
        entityManager.close();
    }

	@Test
	public void hhh16003GeneratesProperSqlByWorkaround() throws Exception {
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		var result = createAndExecuteQuery(entityManager, JPQL_WORKAROUND);
        assertEquals(entity.getId(), result);

		entityManager.getTransaction().commit();
		entityManager.close();
	}

    private PrimaryKey createAndExecuteQuery(EntityManager entityManager, String jpql) {
        var query = entityManager.createQuery(jpql, PrimaryKey.class);
		query.setParameter("countryCode", "DE");
        query.setParameter("transportMode", DOMESTIC);
        query.setParameter("product", "PREMIUM");

        var result = query.getSingleResult();
        return result;
    }
}
