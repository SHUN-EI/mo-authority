package com.mo.authority.service.auth;

import com.mo.authority.dto.auth.LoginDTO;
import com.mo.base.R;

/**
 * Created by mo on 2023/12/3
 */
public interface LoginService {
    R<LoginDTO> login(String account, String password);
}
