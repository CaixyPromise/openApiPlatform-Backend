package com.caixy.project.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class PasswordToolkit
{
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final String SALT = "CAIXYPROMISE";

    public String encodePassword(String rawPassword)
    {
        return passwordEncoder.encode(rawPassword);
    }

    public boolean matches(String originPassword, String hashPassword)
    {
        return passwordEncoder.matches(originPassword, hashPassword);
    }

    public String makeUserKey(String content)
    {
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(SALT)
                .append(".")
                .append(content)
                .append(".")
                .append(System.currentTimeMillis())
                .append(".")
                .append(Arrays.toString(RandomUtil.randomInts(5)));
        return  md5.digestHex(stringBuffer.toString());
    }



}
