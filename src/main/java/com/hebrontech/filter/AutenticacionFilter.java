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
        filterName = "AutenticacionFilter",
        urlPatterns = {"/protegido/*"})
public class AutenticacionFilter implements Filter {

    @Inject
    private SesionController sesionController;

    @Override
    public void init(FilterConfig filterConfig)
            throws ServletException {
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        httpResponse.setHeader(
                "Cache-Control",
                "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);

        boolean autenticado =
                httpRequest.getSession(false) != null
                && sesionController.isAutenticado();

        if (autenticado) {
            chain.doFilter(request, response);
            return;
        }

        httpResponse.sendRedirect(
                httpRequest.getContextPath() + "/index.htech");
    }

    @Override
    public void destroy() {
    }
}
