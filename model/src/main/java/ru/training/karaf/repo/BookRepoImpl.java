package ru.training.karaf.repo;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.apache.aries.jpa.template.EmConsumer;
import org.apache.aries.jpa.template.JpaTemplate;
import ru.training.karaf.model.Book;
import ru.training.karaf.model.BookDO;
import ru.training.karaf.model.Feedback;
import ru.training.karaf.model.FeedbackDO;
import ru.training.karaf.model.GenreDO;
import ru.training.karaf.model.UserDO;

public class BookRepoImpl implements BookRepo {
    private JpaTemplate template;
    
    public BookRepoImpl(JpaTemplate template) {
        this.template = template;
    }
    
    public void init() {
        GenreDO genre = new GenreDO();
        genre.setName("testgenre");
        BookDO book = new BookDO();
        book.setAuthor("testauthor");
        book.setGenre(genre);
        book.setTitle("testtitle");
        book.setYear(2000);
        
        template.tx(em -> em.persist(genre));
        template.tx(em -> em.persist(book));
        
        BookDO book1 = new BookDO();
        book1.setAuthor("testauthor1");
        book1.setGenre(genre);
        book1.setTitle("testtitle1");
        book1.setYear(1500);
        
        template.tx(em -> em.persist(book1));
        
        genre.setName("changed");
        template.tx(em -> em.merge(genre));
        
        // Remove all the books to remove genre 
//        template.tx(em -> em.remove(em.merge(book)));
//        template.tx(em -> em.remove(em.merge(book1)));
//        template.tx(em -> em.remove(em.merge(genre)));
    }
    
    
    @Override
    public List<? extends Book> getAllBooks() {
        return template.txExpr(em -> em.createNamedQuery
            (BookDO.GET_ALL_BOOKS, BookDO.class).getResultList());
    }

    // Cacade not persisting
    @Override
    public void createBook(Book book) {
        BookDO bookToCreate = new BookDO(book);
        template.tx(em -> em.persist(bookToCreate));
    }

    @Override
    public void updateBook(String title, Book book) {
        BookDO bookToUpdate = new BookDO(book);
        bookToUpdate.setGenre(book.getGenre());
        template.tx(em -> em.merge(bookToUpdate));
    }

    @Override
    public Optional<? extends Book> getBook(String title) {
        return template.txExpr(em -> getByTitle(title, em));
    }

    @Override
    public void deleteBook(String title) {
        try {
            BookDO book = template.txExpr(em -> getByTitle(title, em)).get();
            List<UserDO> users = template.txExpr(em ->
                    em.createNamedQuery(UserDO.GET_ALL_USERS)
                            .getResultList());
            if (users != null && !users.isEmpty()) {
                users.forEach(u -> {
                    if (u.getBooks().remove(book)) {
                        template.tx(em -> em.merge(u));
                    }
                });
            }
            template.tx(em -> em.remove(em.merge(book)));
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

//    @Override
//    public List<? extends Feedback> getBookFeedbacks(String title) {
//        Optional<BookDO> book = template.txExpr(em -> getByTitle(title, em));
//        if (book.isPresent()) {
//            return book.get().getFeedbacks();
//        }
//        return null;
//    }    
}
