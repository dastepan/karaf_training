package ru.training.karaf.repo;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.apache.aries.jpa.template.JpaTemplate;
import ru.training.karaf.model.Book;
import ru.training.karaf.model.BookDO;
import ru.training.karaf.model.FeedbackDO;
import ru.training.karaf.model.GenreDO;

public class BookRepoImpl implements BookRepo {
    private JpaTemplate template;
    
    public BookRepoImpl(JpaTemplate template) {
        this.template = template;
    }
    
    public void init() {
        GenreDO genre = new GenreDO();
        genre.setName("g1");
        
        BookDO book = new BookDO();
        book.setAuthor("a1");
        book.setGenre(genre);
        book.setTitle("t1");
        book.setYear(2000);
        
        BookDO book1 = new BookDO();
        book1.setAuthor("a2");
        book1.setGenre(genre);
        book1.setTitle("t2");
        book1.setYear(1500);
        
        FeedbackDO f = new FeedbackDO();
        f.setMessage("f1");
        book1.addFeedback(f);
        
        template.tx(em -> em.persist(genre));
        template.tx(em -> em.persist(book));
        template.tx(em -> em.persist(book1));
    }
    
    
    @Override
    public List<? extends Book> getAllBooks() {
        return template.txExpr(em -> em.createNamedQuery
            (BookDO.GET_ALL_BOOKS, BookDO.class).getResultList());
    }

    @Override
    public void createBook(Book book) {
        BookDO bookToCreate = new BookDO();
        template.tx(em -> {
            bookToCreate.setAuthor(book.getAuthor());
            bookToCreate.setTitle(book.getTitle());
            bookToCreate.setYear(book.getYear());
            bookToCreate.setGenre(em
                    .createNamedQuery(GenreDO.GET_GENRE_BY_NAME, GenreDO.class)
                    .setParameter("name", book.getGenre().getName())
                    .getSingleResult());
            em.persist(bookToCreate);
        });
    }

    @Override
    public void updateBook(String title, Book book) {
        template.tx(em -> {
            getByTitle(title, em).ifPresent(bookToUpdate -> {
            bookToUpdate.setAuthor(book.getAuthor());
            bookToUpdate.setTitle(book.getTitle());
            bookToUpdate.setYear(book.getYear());
            bookToUpdate.setGenre(em
                    .createNamedQuery(GenreDO.GET_GENRE_BY_NAME, GenreDO.class)
                    .setParameter("name", book.getGenre().getName())
                    .getSingleResult());
            //em.merge(bookToUpdate);
            });
        });
    }

    @Override
    public Optional<? extends Book> getBook(String title) {
        return template.txExpr(em -> getByTitle(title, em));
    }

    @Override
    public void deleteBook(String title) {
        try {
            BookDO book = template.txExpr(em -> getByTitle(title, em)).get();
//            List<UserDO> users = template.txExpr(em -> em
//                    .createNamedQuery(UserDO.GET_ALL_USERS, UserDO.class)
//                    .getResultList());
//            
//            if (users != null && !users.isEmpty()) {
//                users.forEach(u -> {
//                    if (u.getBooks().remove(book)) {
//                        template.tx(em -> em.merge(u));
//                    }
//                });
//            }
            template.tx(em -> em
                    .createNamedQuery(BookDO.RESET_OWNERSHIP)
                    .setParameter(1, book.getId())
                    .executeUpdate());

            template.tx(em -> {
                em.remove(em.merge(book));
                em.getEntityManagerFactory().getCache().evictAll();
            });
            
        } catch(NoSuchElementException ex) {
            System.err.println("Book not found: " + ex);
        }
    }
    
    private Optional<BookDO> getByTitle(String title, EntityManager em) {
        try {
            return Optional.of(em.createNamedQuery(BookDO.GET_BOOK_BY_TITLE,
                    BookDO.class).setParameter("title", title)
                    .getSingleResult());
        } catch (NoResultException ex) {
            System.err.println("Book not found: " + ex);
            return Optional.empty();
        }
    }
}
