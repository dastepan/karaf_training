package ru.training.karaf.rest.dto;

import java.util.List;
import java.util.Objects;
import ru.training.karaf.model.Book;
import ru.training.karaf.model.Feedback;
import ru.training.karaf.model.Genre;

public class BookDTO implements Book {
    private String title;
    private String author;
    private Integer year;
    private Genre genre;
    private List<? extends Feedback> feedbacks;

    public BookDTO() {}
    
    public BookDTO(Book book) {
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.year = book.getYear();
        this.genre = book.getGenre();
        this.feedbacks = book.getFeedbacks();
    }
    
    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    @Override
    public List<? extends Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<? extends Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, year, genre, feedbacks);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BookDTO other = (BookDTO) obj;
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.author, other.author)) {
            return false;
        }
        if (!Objects.equals(this.year, other.year)) {
            return false;
        }
        if (!Objects.equals(this.genre, other.genre)) {
            return false;
        }
        if (!Objects.equals(this.feedbacks, other.feedbacks)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BookDTO{" + "title=" + title + ", author=" + author + ", year="
                + year + ", genre=" + genre + '}';
    }
}
