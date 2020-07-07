package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;


//Create Question in the databse

public class QuestionDao {

   @PersistenceContext
   private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        entityManager.persist(questionEntity);
        return questionEntity;
    }
    //return the list of questionentity
    public List<QuestionEntity> getAllquestions(){
        return entityManager.createNamedQuery("getAllQuestions",QuestionEntity.class).getResultList();

    }
    //return the questionEntity if the question with given id found
    public QuestionEntity getQuestionById(final String questionId){
        try {
            return entityManager.
                    createNamedQuery("getQuestionById", QuestionEntity.class).
                    setParameter("uuid", questionId).getSingleResult();
        } catch (NoResultException nre){
            return null;

        }

    }

    //Update the question
public void updateQuestion(QuestionEntity questionEntity) {
    entityManager.merge(questionEntity);

}
//Delete the question
    public void deleteQuestion(QuestionEntity questionEntity){
        entityManager.remove(questionEntity);

    }
    public List<QuestionEntity> getAllQuestionByUser(final UserEntity userId){
        return entityManager
                .createNamedQuery("getQuestionByUser",QuestionEntity.class)
                .setParameter("user",userId)
                .getResultList();
    }
}