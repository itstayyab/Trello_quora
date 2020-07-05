package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    //Create a new question entity
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createNewQuestion(QuestionEntity questionEntity) {
        return questionDao.createQuestion(questionEntity);
    }

    //Get a list of all question entities
    public List<QuestionEntity> getAllQuestions() {
        return questionDao.getAllQuestions();
    }

}
