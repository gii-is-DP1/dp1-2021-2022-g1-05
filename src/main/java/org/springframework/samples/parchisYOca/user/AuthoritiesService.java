/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.parchisYOca.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Service
@Slf4j
public class AuthoritiesService {

    private org.springframework.samples.parchisYOca.user.AuthoritiesRepository authoritiesRepository;
    private org.springframework.samples.parchisYOca.user.UserService userService;

    @Autowired
    public AuthoritiesService(AuthoritiesRepository authoritiesRepository, UserService userService) {
        this.authoritiesRepository = authoritiesRepository;
        this.userService = userService;
    }

    @Transactional
    public Optional<Authorities> findById(Integer id) throws DataAccessException{
        return authoritiesRepository.findById(id);
    }

    @Transactional
    public Authorities saveAuthorities(String username, String role) throws DataAccessException {
        Authorities authority = new Authorities();
        log.debug("Saving authority {} for user {}", role, username);
        Optional<User> user = userService.findUserByUsername(username);
        if(user.isPresent()) {
        	log.debug("User {} found, saving authority as {}", username, role);
            authority.setUser(user.get());
            authority.setAuthority(role);
            authoritiesRepository.save(authority);
        }else {
        	log.debug("User {} not found", username);
            throw new DataAccessException("User '"+username+"' not found!") {};
        }
        return authority;
    }


}
