package com.mobanker.tkj.cs.service;

import com.mobanker.framework.constant.enums.PermissionResult;
import com.mobanker.framework.dto.LoginResponse;
import com.mobanker.framework.dto.LoginUserDto;
import com.mobanker.framework.exception.DataCommitException;
import com.mobanker.framework.service.LoginService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Copyright @ 2013QIANLONG.
 * All right reserved.
 * Class Name : com.mobanker.tkj.cs.service
 * Description :
 * Author : cailinfeng
 * Date : 2016/2/25
 */
@Service("loginService")
public class LoginServiceImpl implements LoginService{

    @Override
    public LoginUserDto userLoginSuccess(LoginUserDto loginUserDto, String s, String s1, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws DataCommitException {
        return null;
    }

    @Override
    public boolean userLoginSuccess(LoginUserDto loginUserDto, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return true;
    }

    @Override
    public PermissionResult checkPermission(HttpServletRequest httpServletRequest) {
        return PermissionResult.PERMISSION_SUCCESS;
    }

    @Override
    public LoginResponse login(String s, String s1, String s2, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws DataCommitException {
        return null;
    }

    @Override
    public void logout(String s, String s1) throws DataCommitException {

    }
}
