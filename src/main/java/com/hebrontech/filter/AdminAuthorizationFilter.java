package com.hebrontech.filter;

import com.hebrontech.controller.SesionController;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(
        filterName = "AdminAuthorizationFilter",
        urlPatterns = {"/protegido/admin/*"})
public class AdminAuthorizationFilter implements Filter {

    @Inject
    private SesionController sesionController;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        boolean autenticado = httpRequest.getSession(false) != null
                && sesionController.isAutenticado();

        if (!autenticado) {
            httpResponse.sendRedirect(
                    httpRequest.getContextPath() + "/index.htech");
            return;
        }

        if (!"A".equals(sesionController.getTipo())) {
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
