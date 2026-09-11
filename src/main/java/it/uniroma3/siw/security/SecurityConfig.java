package it.uniroma3.siw.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disabilita CSRF per gli endpoint REST usati dal client React
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))

            .authorizeHttpRequests(auth -> auth
                // 1. Risorse statiche pubbliche (CSS, JavaScript, immagini)
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico").permitAll()

                // 2. Pagine pubbliche di autenticazione e home
                .requestMatchers("/", "/index", "/login", "/register", "/default").permitAll()

                // 3. Consultazione pubblica (GET) di festival, film, registi, proiezioni
                .requestMatchers(HttpMethod.GET, "/festivals", "/festivals/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/film", "/film/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/registi", "/registi/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/proiezioni", "/proiezioni/**", "/programma", "/programma-interattivo").permitAll()

                // 4. API REST per il frontend React (lettura pubblica)
                .requestMatchers(HttpMethod.GET, "/api/**").permitAll()

                .requestMatchers("/analisi-prestazioni", "/analisi-prestazioni/**").permitAll()

                // 6. Funzionalità riservate all'AMMINISTRATORE (ROLE_ADMIN)
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/festivals/nuovo", "/festivals/salva", "/festivals/modifica/**", "/festivals/elimina/**", "/festivals/associa-film/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/film/nuovo", "/film/salva", "/film/modifica/**", "/film/elimina/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/registi/nuovo", "/registi/salva", "/registi/modifica/**", "/registi/elimina/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/sale/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/proiezioni/nuova", "/proiezioni/salva", "/proiezioni/modifica/**", "/proiezioni/elimina/**").hasAuthority("ROLE_ADMIN")

                // 7. Funzionalità per utenti autenticati (recensioni)
                .requestMatchers("/recensioni/**").authenticated()

                // Qualsiasi altra richiesta richiede autenticazione
                .anyRequest().authenticated()
            )

            // Configurazione del form di login
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/default", true) // /default reindirizza ad /admin o alla home a seconda del ruolo
                .failureUrl("/login?error=true")
                .permitAll()
            )

            // Configurazione del logout
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
