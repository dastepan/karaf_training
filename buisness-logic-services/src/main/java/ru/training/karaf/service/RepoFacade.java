package ru.training.karaf.service;

import java.util.List;
import java.util.Optional;
import ru.training.karaf.repo.Repo;

public class RepoFacade {
    
    private List<Repo> repos;

    public void setRepos(List<Repo> repos) {
        this.repos = repos;
    }

    public void init() {}
    
    public Optional retrieveEntity(Class type, String criteria) {
        System.err.println("Inside facade");
        for (Repo r: repos) {
            Object entity = r.getByCriteria(criteria);
            System.err.println(entity);
            if (entity != null && entity.getClass().equals(type)) {
                return Optional.of(entity);
            }
        }
        return Optional.empty();
    }
}
