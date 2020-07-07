package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserAuthDao userAuthDao;

    @Autowired
    private UserDao userDao;

    //Authorize user who wants to post a question
    public UserAuthEntity createQuestionAuthorization(final String authorization) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(authorization);
        if (userAuthEntity==null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else {
            //Retrieve logout_at attribute value of UserAuthEntity to check if user has already signed out
            ZonedDateTime logoutAt = userAuthEntity.getLogoutAt();
            if (logoutAt!=null) {
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a question");
            } else {
                return userAuthEntity;
            }
        }
    }

    //Create a new Question entity
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createNewQuestion(QuestionEntity questionEntity) {
        // Assign a UUID to the question that is being created.
        questionEntity.setUuid(UUID.randomUUID().toString());
        return questionDao.createQuestion(questionEntity);
    }

    //Authorize user who wants to get a list of all questions
    public UserAuthEntity getAllQuestionsAuthorization(final String authorization) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(authorization);
        if (userAuthEntity==null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else {
            //Retrieve logout_at attribute value of UserAuthEntity to check if user has already signed out
            ZonedDateTime logoutAt = userAuthEntity.getLogoutAt();
            if (logoutAt!=null) {
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions");
            } else {
                return userAuthEntity;
            }
        }
    }

    //Get a list of all Question entities
    public List<QuestionEntity> getAllQuestions() {
        return questionDao.getAllQuestions();
    }

    //Get Question entity by question Uuid
    public QuestionEntity getQuestionByUuid(String questionUuid) throws InvalidQuestionException {
        QuestionEntity questionByUuid = questionDao.getQuestionByUuid(questionUuid);
        if (questionByUuid==null) {
           throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        }
        return questionByUuid;
    }

    //Validate whether the question owner and editor are same users
    public boolean authorizeQuestionOwner(Integer questionEditorId, Integer questionOwnerId) throws AuthorizationFailedException {
        if (!questionEditorId.equals(questionOwnerId)) {
            throw new AuthorizationFailedException("ATHR-003","Only the question owner can edit the question");
        } else {
            return true;
        }
    }

    //Edit question content
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(QuestionEntity questionEntity) {
        return questionDao.editQuestion(questionEntity);
    }

    //Check whether user is authorized to edit question
    public UserAuthEntity editQuestionAuthorization(final String authorization) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(authorization);
        if (userAuthEntity==null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else {
            //Retrieve logout_at attribute value of UserAuthEntity to check if user has already signed out
            ZonedDateTime logoutAt = userAuthEntity.getLogoutAt();
            if (logoutAt!=null) {
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to edit the question");
            } else {
                return userAuthEntity;
            }
        }
    }

    /**
     * Service method to get all question posted by an user.
     * @param userId
     * @param accessToken
     * @return list of all questions
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    public List<QuestionEntity> getAllQuestionsByUser(final String userId, final String accessToken)
            throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity userAuthEntity = userAuthDao.getUserAuthByToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions posted by a specific user");
        }
        UserEntity user = userDao.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }
        return questionDao.getAllQuestionsByUser(user);
    }
}
