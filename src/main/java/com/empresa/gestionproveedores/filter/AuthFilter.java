package com.empresa.gestionproveedores.filter;

import com.empresa.gestionproveedores.bean.AuthBean;
import jakarta.inject.Inject;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Filtro de Autenticación
 * Protege las páginas que requieren login
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"*.xhtml"})
@Slf4j
public class AuthFilter implements Filter {

    @Inject
    private AuthBean authBean;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Filtro de autenticación inicializado");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestURI = httpRequest.getRequestURI();

        // Páginas públicas que no requieren autenticación
        boolean isPaginaPublica = requestURI.contains("/login.xhtml") ||
                requestURI.contains("/javax.faces.resource/") ||
                requestURI.contains("/jakarta.faces.resource/");

        // Verificar autenticación
        boolean autenticado = session != null && authBean != null && authBean.isAutenticado();

        if (isPaginaPublica || autenticado) {
            // Permitir acceso
            chain.doFilter(request, response);
        } else {
            // Redirigir al login
            log.warn("Acceso no autorizado a: {}", requestURI);
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.xhtml");
        }
    }

    @Override
    public void destroy() {
        log.info("Filtro de autenticación destruido");
    }
}