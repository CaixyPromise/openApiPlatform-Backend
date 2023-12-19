package com.caixy.openApiPlatformEncryptionAlgorithm;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * 加密算法实现类
 *
 * @name: com.caixy.openApiPlatformEncryptionAlgorithm.SignUtils
 * @author: CAIXYPROMISE
 * @since: 2023-12-19 22:33
 **/
public class SignUtils
{
    private static final String SALT = "openapi";

    /**
     * 生成加密后的SecretKey
     *
     * @param body      请求体
     * @param timestamp 时间戳
     * @param secretKey 密钥
     * @return 加密后的字符串
     * @author CAIXYPROMISE
     * @CreateDate 2023/12/18 20:22
     * @update 2023/12/18 20:22
     * @version 1.0
     */
    public static String encodeSecretKey(String secretKey, String body, Long timestamp)
    {
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        String content = body + "." + secretKey + "." + timestamp.toString() + "." + SALT;
        return md5.digestHex(content);
    }

    /**
     * 校验生成的SecretKey与原始的Key的区别
     *
     * @param secretKey 原始密钥
     * @param body      请求体
     * @param timestamp 时间戳
     * @param sign      已经加密后的签名
     * @return 是否相等
     * @author CAIXYPROMISE
     * @CreatedDate 2023/12/18 20:22
     * @updatedDate 2023/12/18 20:22
     * @version 1.0
     */
    public static boolean validateSecretKey(String secretKey, String body, Long timestamp, String sign)
    {
        return encodeSecretKey(secretKey, body, timestamp).equals(sign);
    }

}
