package com.xiaoy.hashid.demo.controller;

import com.xiaoy.hashid.demo.annotation.HashIdAnnotated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Author: yuqiang
 * @Description:
 * @Date: Created in 2020/9/7 22:32
 * @Modified By:
 */
@RestController
public class HashIdController {

    @GetMapping("/test1")
    public String test1(@RequestParam("id") @HashIdAnnotated String id){
        System.out.println(id);
        return id;
    }

    @GetMapping("/test2/{id}")
    public String test2(@PathVariable("id") @HashIdAnnotated String id){
        System.out.println(id);
        return id;
    }

    /**
     * 复杂类型入参 以及出参的 字段hashId处理
     * @param user
     * @return
     */
    @PostMapping("/test3")
    public @HashIdAnnotated User test3(@RequestBody User user){
        System.out.println("userId -> " + user.getUserId());
        System.out.println("userList -> " + (Objects.isNull(user.getUserList()) ? null : user.getUserList().toString()));
        System.out.println("userArray -> " + (Objects.isNull(user.getUserArray()) ? null : user.getUserArray().toString()));
        if(Objects.nonNull(user.getAddress())){
            System.out.println("user.address.addrId -> " + user.getAddress().getAddr());
        }
        return user;
    }
    public static class User {

        @HashIdAnnotated
        private String userId;

        private String name;

        @HashIdAnnotated
        private List<User> userList;

        @HashIdAnnotated
        private User[] userArray;

        private Address address;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<User> getUserList() {
            return userList;
        }

        public void setUserList(List<User> userList) {
            this.userList = userList;
        }

        public User[] getUserArray() {
            return userArray;
        }

        public void setUserArray(User[] userArray) {
            this.userArray = userArray;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        @Override
        public String toString() {
            return "User{" +
                    "userId='" + userId + '\'' +
                    ", name='" + name + '\'' +
                    ", userList=" + userList +
                    ", userArray=" + Arrays.toString(userArray) +
                    ", address=" + address +
                    '}';
        }
    }

    public static class Address {

        @HashIdAnnotated
        private String addrId;

        private String addr;

        public String getAddrId() {
            return addrId;
        }

        public void setAddrId(String addrId) {
            this.addrId = addrId;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "addrId='" + addrId + '\'' +
                    ", addr='" + addr + '\'' +
                    '}';
        }
    }
}
