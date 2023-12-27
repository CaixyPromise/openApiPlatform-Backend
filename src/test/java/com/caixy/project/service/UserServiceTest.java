package com.caixy.project.service;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.caixy.openApiPlatformEncryptionAlgorithm.SignUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

/**
 * 用户服务测试
 *
 * @author caixy
 */
@ExtendWith(SpringExtension.class)
class UserServiceTest
{

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final String SALT = "CAIXYPROMISE";

    @Test
    public void encodePassword()
    {
        String rawPassword = "as123456789";
        System.out.println(passwordEncoder.encode(rawPassword));
    }

    public boolean matches(String originPassword, String hashPassword)
    {
        return passwordEncoder.matches(originPassword, hashPassword);
    }

    @Test
    public void makeUserKey()
    {
        String content = "caixypromise";
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(SALT)
                .append(".")
                .append(content)
                .append(".")
                .append(System.currentTimeMillis())
                .append(".")
                .append(Arrays.toString(RandomUtil.randomInts(5)));
//        return  md5.digestHex(stringBuffer.toString());
        System.out.println(md5.digestHex(stringBuffer.toString()));
    }


    @Test
    public void test()
    {
        Long time = Long.valueOf("1703676640");
        String nonce = "42013";
        String content = "7912d7887dd5caaf5b06d03538981876b1216c67b1eb75edd89cebc0f055cdab";
        String input_content = "17a0396a90e0a122cea50032b06c3d0ee06d0f9961a6a2450ee89760ad7f23c2";

        System.out.println(SignUtils.encodeSecretKey(content, nonce, time));
        String newContent = SignUtils.encodeSecretKey(input_content, nonce, time);
        System.out.println(SignUtils.encodeSecretKey(newContent, nonce, time));
    }
}