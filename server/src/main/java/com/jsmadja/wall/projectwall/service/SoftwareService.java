package com.jsmadja.wall.projectwall.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;
import com.jsmadja.wall.projectwall.domain.Software;

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
    public void persist(Software software) throws SoftwareNotCreatedException {
        Preconditions.checkNotNull(software, "software");
        try {
            entityManager.persist(software);
            entityManager.flush();
        } catch(Throwable e) {
            String message = "Can't create software "+software+" in database";
            LOG.error(message, e);
            throw new SoftwareNotCreatedException(message, e);
        }
    }
}
