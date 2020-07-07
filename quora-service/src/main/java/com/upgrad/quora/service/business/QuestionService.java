package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


public class QuestionService<questionEntity> {
    @Autowired
    private UserAuthDao userAuthDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private QuestionDao questionDao;

    //Delete the the Question -Delete Request

    @Transactional(propagation = Propagation.REQUIRED)

    public QuestionEntity deleteQuestion(final String accessToken, final String questionId)
            throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);

        //Access token is not in the database- throw Authorizatopn FailedException

        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "USer has not signed in");
            //user has signed out
        } else if (userAuthEntity.getLogoutAt() != null){
            throw new AuthorizationFailedException("ATHR-002",
                    "User is signed out.Sign in first to delete the question");
        }
        QuestionEntity questionEntity = questionDao.getQuestionById(questionId);
        if (questionEntity == null) {
            new InvalidQuestionException("QUES-001", "entered question uuid does not exist");
        }
        //Only the question owner can delete the question

        if (!questionEntity.getUserEntity().getUuid().equals(userAuthEntity.getUserEntity().getUuid())
                && !userAuthEntity.getUserEntity().getRole().equals("admin"))
        {
            throw new AuthorizationFailedException("ATHR-003",
                    "Only the question owner or admin can delete the question");
        }
        questionDao.deleteQuestion(questionEntity);
        return questionEntity;
    }
}




















