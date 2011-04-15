package net.awired.visuwall.server.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import net.awired.visuwall.server.domain.Software;
import net.awired.visuwall.server.exception.NotCreatedException;
import net.awired.visuwall.server.exception.SoftwareNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

@Service
public class SoftwareService {

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger LOG = LoggerFactory.getLogger(SoftwareService.class);

    public Software find(String name) throws SoftwareNotFoundException {
        Preconditions.checkNotNull(name, "name");

        Software software = entityManager.find(Software.class, name);
        if (software == null) {
            throw new SoftwareNotFoundException("Software with name:"+name+" not found in database");
        }
        return software;
    }

    public List<Software> findAll() {
        Query query = entityManager.createNamedQuery("findAll");
        return query.getResultList();
    }

    @Transactional
    public void persist(Software software) throws NotCreatedException {
        Preconditions.checkNotNull(software, "software");
        try {
            entityManager.persist(software);
            entityManager.flush();
        } catch(Throwable e) {
            String message = "Can't create software "+software+" in database";
            LOG.error(message, e);
            throw new NotCreatedException(message, e);
        }
    }
}
