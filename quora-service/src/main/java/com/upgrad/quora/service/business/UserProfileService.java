package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
public class UserProfileService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthDao userAuthDao;

    //Retrieving the user by Uuid from database whose profile is to be returned
    public UserEntity getUser(final String userUuid) throws UserNotFoundException{

        UserEntity userEntity =  userDao.getUserById(userUuid);

        if(userEntity==null) {
            //handle exception when null object is returned from userDao
            throw new UserNotFoundException("USR-001","User with entered uuid does not exist");

        } else {
            return userEntity;
        }
    }


    public UserAuthEntity authorizeUser(final String authorization) throws AuthorizationFailedException {
        UserAuthEntity userByAuthToken = userAuthDao.getUserAuthByToken(authorization);
        if (userByAuthToken==null) {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else {
            ZonedDateTime logoutAt = userByAuthToken.getLogoutAt();
            if (logoutAt!=null) {
                throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
            } else {
                return userByAuthToken;
            }
        }
    }
}
