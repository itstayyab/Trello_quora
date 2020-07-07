package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDeleteResponse;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class QuestionController {
    @Autowired private QuestionService questionService;
    //DeleteQuestionController
@RequestMapping(method= RequestMethod.DELETE, path="/question/delete/{questionId}")
    public ResponseEntity<QuestionDeleteResponse> deletequestion(
      @RequestHeader("authorization") final String accessToken,
      @PathVariable("questionId") final String questionId)
    throws AuthorizationFailedException, InvalidQuestionException{
    QuestionEntity questionEntity=questionService.deleteQuestion(accessToken,questionId);
    QuestionDeleteResponse questionDeleteResponse=new QuestionDeleteResponse();
    questionDeleteResponse.setId(questionEntity.getUuid());
    questionDeleteResponse.setStatus("Question Deleted");
    return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);


    }
}
