package com.xiaoy.hashid.demo.utils;

import org.hashids.Hashids;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @Author: yuqiang
 * @Description: HashId工具类
 * @Date: Created in 2020/7/10 20:20
 * @Modified By:
 */
public class HashIdsUtil {
    private static Hashids hashids = new Hashids("miyao");

    /**
     * 单个 id 加密
     * @param id : 需要加密的 id
     * @return id 加密后的 hash 值
     */
    public static String encode(Long id) {
        if (Objects.isNull(id)) {
            return null;
        }
        return hashids.encode(id);
    }

    /**
     * 单个 id 解密
     * @param hash : 加密的 hash 值
     * @return 解密后的 id
     */
    public static Long decode(String hash) {
        if (StringUtils.isEmpty(hash)) {
            return null;
        }
        long[] decode = hashids.decode(hash);
        if (decode.length == 0) {
            return null;
        }
        return decode[0];
    }

    /**
     * 多个 id 加密
     * @param ids : 需要加密的 id 集合
     * @return 加密后的 hash 值
     */
    public static String encodeBatch(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        return hashids.encode(ids.stream().mapToLong(t -> t).toArray());
    }

    public static void main(String[] args) {
        String encode = HashIdsUtil.encode(54L);
        System.out.println("单个加密 id 结果为: " + encode);
        Long decodeId = HashIdsUtil.decode(encode);
        System.out.println("单个解密 id 结果为: " + decodeId);

    }
}
