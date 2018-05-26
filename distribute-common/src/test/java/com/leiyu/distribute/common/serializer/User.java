package com.leiyu.distribute.common.serializer;

import java.io.Serializable;

public class User implements Serializable {

        private static final long serialVersionUID = 1l;

        private Integer id;

        private String name;

        private String username;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }