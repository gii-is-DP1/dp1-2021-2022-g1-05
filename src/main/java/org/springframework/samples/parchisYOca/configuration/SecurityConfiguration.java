package org.springframework.samples.parchisYOca.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author japarejo
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	DataSource dataSource;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/resources/**","/webjars/**","/h2-console/**").permitAll()
				.antMatchers(HttpMethod.GET, "/","/oups").permitAll()
				.antMatchers("/users/new").permitAll()
                .antMatchers("/stats").hasAnyAuthority("player", "admin")
                .antMatchers("/session/**").hasAnyAuthority("player", "admin")
                .antMatchers("/gooseInGame/**").hasAnyAuthority("player", "admin")
                .antMatchers("/gooseMatches").hasAnyAuthority("admin")
                .antMatchers("/gooseMatches/close/**").hasAnyAuthority("admin")
                .antMatchers("/gooseMatches/**").hasAnyAuthority("player", "admin")
                .antMatchers("/ludoInGame/**").hasAnyAuthority("player", "admin")
                .antMatchers("/ludoMatches").hasAnyAuthority("admin")
                .antMatchers("/ludoMatches/close/**").hasAnyAuthority("admin")
                .antMatchers("/ludoMatches/**").hasAnyAuthority("player", "admin")
                .antMatchers("/users").hasAnyAuthority("admin")
                .antMatchers("/users/**").hasAnyAuthority("player","admin")
                .antMatchers("/achievements").hasAnyAuthority("admin", "player")
                .antMatchers("/achievements/**").hasAnyAuthority("admin")
                .antMatchers("/players").hasAnyAuthority("admin")
                .antMatchers("/players/**").hasAnyAuthority("player","admin")
				.antMatchers("/admin/**").hasAnyAuthority("admin")
				.anyRequest().denyAll()
				.and()
				 	.formLogin()
				 	/*.loginPage("/login")*/
				 	.failureUrl("/login-error")
				.and()
					.logout()
						.logoutSuccessUrl("/");
                // Configuración para que funcione la consola de administración
                // de la BD H2 (deshabilitar las cabeceras de protección contra
                // ataques de tipo csrf y habilitar los framesets si su contenido
                // se sirve desde esta misma página.
                http.csrf().ignoringAntMatchers("/h2-console/**");
                http.headers().frameOptions().sameOrigin();
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication()
	      .dataSource(dataSource)
	      .usersByUsernameQuery(
	       "select username,password,enabled "
	        + "from users "
	        + "where username = ?")
	      .authoritiesByUsernameQuery(
              "select username, authority "
                  + "from authorities "
                  + "where username = ?")
	      .passwordEncoder(passwordEncoder());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		PasswordEncoder encoder =  NoOpPasswordEncoder.getInstance();
	    return encoder;
	}

}


