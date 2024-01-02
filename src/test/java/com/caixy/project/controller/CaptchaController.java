package com.caixy.project.controller;

/**
 * 验证码控制器
 *
 * @name: com.caixy.project.controller.CaptchaController
 * @author: CAIXYPROMISE
 * @since: 2024-01-02 15:44
 **/

import cn.hutool.core.codec.Base64;
import com.caixy.project.common.BaseResponse;
import com.caixy.project.common.ErrorCode;
import com.caixy.project.common.ResultUtils;
import com.caixy.project.constant.RedisConstant;
import com.caixy.project.model.vo.CaptchaVO;
import com.caixy.project.utils.EncryptionUtils;
import com.caixy.project.utils.RedisOperatorService;
import com.google.code.kaptcha.Producer;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 验证码操作处理
 *
 * @author caixy
 */
@RestController
@RequestMapping("/captcha")
public class CaptchaController
{
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Resource
    private RedisOperatorService redisOperatorService;

    @Resource
    private EncryptionUtils encryptionUtils;

    /**
     * 生成验证码
     */
    @GetMapping("/code")
    public BaseResponse<?> getCode(HttpServletRequest request, HttpServletResponse response) throws IOException, NoSuchAlgorithmException
    {
        HashMap<String, Object> resultMap = new HashMap<>();
        // 保存验证码信息
        // 生成uuid
        String uuid = UUID.randomUUID().toString();

        String capStr = null, code = null;
        BufferedImage image = null;

        // 生成验证码 类型 char 或 math
        // 这里默认用 char
        // capStr和code是有区别的，capStr是用于存储生成的验证码图像上显示的文本。是验证码图片的直接表示。
        // code是存储用户需要输入的验证码的答案。
        // 例如:
        // 在数学型验证码中，code 是数学问题的答案（例如，如果 capStr 是 "3+4"，那么 code 是 "7"）。
        // 在字符型验证码中，code 和 capStr 是相同的，都是用户需要输入的验证码文本。
        capStr = code = captchaProducer.createText();
        image = captchaProducer.createImage(capStr);
//        String captchaType = SmartLaboratoryConfig.getCaptchaType();
//        if ("math".equals(captchaType))
//        {
//            String capText = captchaProducerMath.createText();
//            capStr = capText.substring(0, capText.lastIndexOf("@"));
//            code = capText.substring(capText.lastIndexOf("@") + 1);
//            image = captchaProducerMath.createImage(capStr);
//        }
//        else if ("char".equals(captchaType))
//        {
//            capStr = code = captchaProducer.createText();
//            image = captchaProducer.createImage(capStr);
//        }

//        redisCache.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        }
        catch (IOException e) {
            return ResultUtils.error(ErrorCode.OPERATION_ERROR);
        }

        resultMap.put("uuid", uuid);
        resultMap.put("code", code);
        // 生成前端加密密钥
        encryptionUtils.makeEncryption(resultMap);

        // 写入redis
        // 以uuid作为凭证，
        // 并设置过期时间: 5分钟
        redisOperatorService.setHashMap(RedisConstant.CAPTCHA_CODE_KEY + uuid,
                resultMap,
                RedisConstant.CAPTCHA_CODE_EXPIRE); // 过期时间5分钟
        // 返回Base64的验证码图片信息
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCodeImage(Base64.encode(os.toByteArray()));
        captchaVO.setUuid(uuid);
        return ResultUtils.success(captchaVO);
    }
}