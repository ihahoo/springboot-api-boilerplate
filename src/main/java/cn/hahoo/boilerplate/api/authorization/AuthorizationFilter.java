package cn.hahoo.boilerplate.api.authorization;

import cn.hahoo.boilerplate.utils.auth.AuthUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    @Resource
    private AuthorizationService authorizationService;

    private AuthUtil authUtil;

    @Autowired
    public void setAuthUtil(AuthUtil authUtil) {
        this.authUtil = authUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer")) {
            filterChain.doFilter(request,response);
            return;
        }

        String jwtToken = token.replace("Bearer","");
        if (jwtToken.isEmpty()) {
            filterChain.doFilter(request,response);
            return;
        }

        Jws<Claims> jws;
        try {
            jws = authUtil.parseToken(jwtToken);
        } catch (JwtException ex) {
            filterChain.doFilter(request,response);
            return;
        }

        // 检查黑名单
        if (jws.getBody().getId().isEmpty() || authorizationService.isInBlacklist(jws.getBody().getId())) {
            filterChain.doFilter(request,response);
            return;
        }

        //权限
        List<String> roles = authUtil.getScopes(jws.getBody().get("scopes"));
        HashSet<GrantedAuthority> authorities = new HashSet<>(roles.size());

        for (String role : roles)
            authorities.add(new SimpleGrantedAuthority(role));

        int userId = 0;
        if (jws.getBody().getSubject() != null) {
            userId = Integer.parseInt(jws.getBody().getSubject());
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                new AuthorizationInfo(userId), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
