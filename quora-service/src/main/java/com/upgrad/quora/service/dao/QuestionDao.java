package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    //Persists a new Question entity in the Database
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    //Fetches a list of Question entities from the Database
    public List<QuestionEntity> getAllQuestions() {

        List<QuestionEntity> questionsList = entityManager.createNamedQuery("getAllQuestions", QuestionEntity.class).getResultList();
        return questionsList;
    }

    //Fetch Question by question Uuid
    public QuestionEntity getQuestionByUuid(String questionUuid) {
        try{
            return entityManager.createNamedQuery("getQuestionByUuid", QuestionEntity.class)
                    .setParameter("questionId", questionUuid)
                    .getSingleResult();
        } catch(NoResultException nre) {
            nre.printStackTrace();
            return null;
        }
    }

    //persist the edited Question in Database
    public QuestionEntity editQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    /**
     * To get all questions from question entity for a given user
     * @param userId
     * @return
     */
    public List<QuestionEntity> getAllQuestionsByUser(final UserEntity userId) {
        return entityManager
                .createNamedQuery("getAllQuestionsByUser", QuestionEntity.class)
                .setParameter("user", userId)
                .getResultList();
    }
}