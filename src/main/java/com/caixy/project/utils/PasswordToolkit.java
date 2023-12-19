package com.caixy.project.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordToolkit
{
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String encodePassword(String rawPassword)
    {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(String originPassword, String hashPassword)
    {
        return passwordEncoder.matches(originPassword, hashPassword);
    }
}
