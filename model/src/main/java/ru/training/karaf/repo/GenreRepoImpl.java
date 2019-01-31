package ru.training.karaf.repo;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.apache.aries.jpa.template.JpaTemplate;
import ru.training.karaf.model.Genre;
import ru.training.karaf.model.GenreDO;

public class GenreRepoImpl implements GenreRepo {
    private JpaTemplate template;

    public GenreRepoImpl(JpaTemplate template) {
        this.template = template;
    }

    @Override
    public List<? extends Genre> getAllGenres() {
        return template.txExpr(em -> em.createNamedQuery
            (GenreDO.GET_ALL_GENRES, GenreDO.class).getResultList());
    }

    @Override
    public void createGenre(Genre genre) {
        try {
            template.txExpr(em ->
                    getGenreByName(genre.getName(), em)).get();
            System.err.println("Genre already exists");
        } catch(NoSuchElementException ex) {
            GenreDO genreToCreate = new GenreDO(genre);
            template.tx(em -> em.persist(genreToCreate));
        }
    }

    @Override
    public void updateGenre(String name, Genre genre) {
        try {
            template.txExpr(em -> getGenreByName(genre.getName(), em)).get();
            System.err.println("Genre with specified name already exists");
        } catch(NoSuchElementException ex) {
            template.tx(em -> {
                getGenreByName(name, em).ifPresent(genreToUpdate -> {
                    genreToUpdate.setName(genre.getName());

                    em.merge(genreToUpdate);
                });
            });            
        }
    }

    @Override
    public Optional<? extends Genre> getGenre(String name) {
        return template.txExpr(em -> getGenreByName(name, em));
    }

    @Override
    public void deleteGenre(String name) {
        template.tx(em -> getGenreByName(name, em).ifPresent(em::remove));
    }
    
    private Optional<GenreDO> getGenreByName(String name, EntityManager em) {
        try {
            return Optional.of(em.createNamedQuery(GenreDO.GET_GENRE_BY_NAME,
                    GenreDO.class).setParameter("name", name)
                    .getSingleResult());
        } catch (NoResultException e) {
            System.err.println("Genre not found: " + e);
            return Optional.empty();
        }
    }
}
