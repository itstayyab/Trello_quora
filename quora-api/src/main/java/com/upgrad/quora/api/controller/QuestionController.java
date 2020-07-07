package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserAuthorizationService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    private UserAuthorizationService userAuthorizationService;

    @Autowired
    private QuestionService questionService;

    /**
     * Controller method to handle createQuestion POST endpoint
     * @param questionRequest
     * @param authorization
     * @return QuestionResponse
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(QuestionRequest questionRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        //Authorize the user who is trying to create question
        UserAuthEntity userAuthEntity = questionService.createQuestionAuthorization(authorization);

        //Create Question Entity and store it in DB
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setDate(ZonedDateTime.now());
        questionEntity.setUserEntity(userAuthEntity.getUserEntity());

        final QuestionEntity createdQuestion = questionService.createNewQuestion(questionEntity);
        //Create QuestionResponse and return it to user
        QuestionResponse questionResponse = new QuestionResponse().id(questionEntity.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.OK);
    }

    /**
     * Method handles GET request to fetch all questions
     * @param authorization
     * @return List
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        //Authorize the user by passing in access-token of the user
        UserAuthEntity userAuthEntity = questionService.getAllQuestionsAuthorization(authorization);
        //Fetch list of all questions
        List<QuestionEntity> allQuestions = questionService.getAllQuestions();
        //List to add QuestionResponse entities
        final List<QuestionDetailsResponse> questionResponse = new ArrayList<>();
        //Extract Uuid and content from each QuestionResponse entity
        for(QuestionEntity question:allQuestions) {
            String uuid = question.getUuid();
            String content = question.getContent();
            questionResponse.add(new QuestionDetailsResponse().id(uuid).content(content));
        }
        return new ResponseEntity<List>(questionResponse,HttpStatus.OK);
    }

    /**
     *Method handles PUT request to update question whose Uuid is passed by user
     * @param questionEditRequest
     * @param questionUuid
     * @param authorization
     * @return QuestionEditResponse
     * @throws AuthorizationFailedException
     * @throws InvalidQuestionException
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestionContent(QuestionEditRequest questionEditRequest, @PathVariable("questionId") final String questionUuid, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = questionService.editQuestionAuthorization(authorization);

        //Get Id of user who wants to edit question
        Integer questionEditorId = userAuthEntity.getUserEntity().getId();

        QuestionEntity question = questionService.getQuestionByUuid(questionUuid);

        //Get Id of user who is the owner of the question
        Integer questionOwnerId = question.getUserEntity().getId();

        //Verifying whether the user trying to edit the question is same as the question owner
        questionService.authorizeQuestionOwner(questionEditorId, questionOwnerId);

        //Set the user typed content as the new content of the Question entity
        question.setContent(questionEditRequest.getContent());

        //Edit question date?

        //Update the question in the database
        QuestionEntity editedQuestion = questionService.editQuestion(question);

        //Set the Uuid and status of edited question in response
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(editedQuestion.getUuid()).status("QUESTION EDITED");

        return new ResponseEntity<QuestionEditResponse>(questionEditResponse,HttpStatus.OK);
    }

    /**
     * Controller method to get all questions posted by a user
     * @param accessToken
     * @param userId
     * @return
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    @RequestMapping(method = RequestMethod.GET,path = "question/all/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getQuestionByUserId(@RequestHeader("authorization") final String accessToken,@PathVariable("userId") String userId)
            throws AuthorizationFailedException, UserNotFoundException {
        List<QuestionEntity> questions = questionService.getAllQuestionsByUser(userId, accessToken);
        List<QuestionDetailsResponse> questionDetailResponses = new ArrayList<>();
        for (QuestionEntity questionEntity : questions) {
            QuestionDetailsResponse questionDetailResponse = new QuestionDetailsResponse();
            questionDetailResponse.setId(questionEntity.getUuid());
            questionDetailResponse.setContent(questionEntity.getContent());
            questionDetailResponses.add(questionDetailResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailResponses, HttpStatus.OK);
    }
}
